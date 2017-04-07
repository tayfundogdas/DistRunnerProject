package org.td.distrunner.commandhandlers.clientexecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.helpers.JarHelper;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class ExecuteJob implements Job {

	public static final String JobFailMesage = "ERROR";

	private static String getFullMasterAPIAddress(String path) {
		StringBuilder str = new StringBuilder();
		str.append("http://");
		str.append(AppSettings.MasterAddress);
		str.append(":");
		str.append(AppSettings.JettyPort);
		str.append("/");
		str.append(AppSettings.APIChannelName);
		str.append("/DownloadJar/");
		str.append(path);

		return str.toString();
	}

	private static String getJarFile(String jobName) {
		return jobName.substring(0, jobName.lastIndexOf('.')) + ".jar";
	}

	private static void downloadAndCacheJarFile(String jobName) throws IOException {
		String jarName = getJarFile(jobName);
		File[] matchingFiles = JarHelper.getMatchingFiles(jarName);
		// if jar is not in cache
		if (matchingFiles.length == 0) {
			// download file
			URL website = new URL(getFullMasterAPIAddress(jarName));
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(AppSettings.ProcessJarPath + jarName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		}
	}

	private static String runJob(String jobName, String jobParam) throws Exception {
		String result = null;
		// download jar from master if my disk does not contain it
		downloadAndCacheJarFile(jobName);
		// run class inside jar
		URL url = new URL("file:/" + AppSettings.ProcessJarPath + getJarFile(jobName));
		URLClassLoader clsLoader = URLClassLoader.newInstance(new URL[] { url });
		@SuppressWarnings("rawtypes")
		Class cls = clsLoader.loadClass(jobName);
		@SuppressWarnings("unchecked")
		Method method = cls.getDeclaredMethod("Execute", new Class[] { String.class });
		Object obj = cls.newInstance();
		result = (String) method.invoke(obj, jobParam);
		method = null;
		obj = null;
		cls = null;
		// closes jar loader
		clsLoader.close();
		clsLoader = null;
		System.gc();

		return result;
	}

	private static void executeJobAndReportResulttoMaster(ClientJobModel myJob) {
		// run job and report result
		Message mess = new Message();
		mess.MessageType = MessageTypes.ExecutionResultMessage;
		ExecutionResultModel result = new ExecutionResultModel();
		result.JobId = myJob.Id;
		String execResult = null;
		try {
			execResult = runJob(myJob.JobName, myJob.JobParam);
		} catch (Exception e) {
			execResult = JobFailMesage;
			LogHelper.logError(e);
			// LogHelper.logTrace("Error on run with job : " +
			// myJob.toString());
		}
		LogHelper.logTrace("Result for " + myJob.Id + " : "
				+ execResult.substring(0, execResult.length() > 50 ? 50 : execResult.length()));
		result.ExecutionResult = execResult;
		mess.MessageContent = JsonHelper.getJsonString(result);
		try {
			//CommunicationHelper.sendMessagetoMaster(mess);
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ClientJobModel myJob = InMemoryObjects.currentNodeJobList.values().stream()
				.filter(x -> x.IsProcessed == null || x.IsProcessed == false).findFirst().orElse(null);
		if (myJob != null) {
			ExecuteJob.executeJobAndReportResulttoMaster(myJob);
			//TODO: remove  executed job
			//InMemoryObjects.currentNodeJobList.remove(myJob.Id);
			myJob.IsProcessed = true;
			InMemoryObjects.currentNodeJobList.put(myJob.Id, myJob);
			System.out.println(myJob.JobName);
		}
	}
}
