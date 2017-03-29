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
import org.td.distrunner.engine.CommunicationHelper;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.processmodelparser.JarHelper;

public class ExecuteJob implements Job {

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

	private static Object runJob(String jobName, Object jobParam) throws Exception {
		Object result = null;
		// download jar from master if my disk does not contain it
		downloadAndCacheJarFile(jobName);
		// run class inside jar
		URL url = new URL("file:/" + AppSettings.ProcessJarPath + getJarFile(jobName));
		URLClassLoader clsLoader = URLClassLoader.newInstance(new URL[] { url });
		@SuppressWarnings("rawtypes")
		Class cls = clsLoader.loadClass(jobName);
		@SuppressWarnings("unchecked")
		Method method = cls.getDeclaredMethod("Execute", new Class[] { Object.class });
		Object obj = cls.newInstance();
		result = method.invoke(obj, jobParam);
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
		Message<String> mess = new Message<String>();
		mess.MessageType = MessageTypes.ExecutionResultMessage;
		ExecutionResultModel result = new ExecutionResultModel();
		result.JobId = myJob.Id;
		Object execResult = null;
		try {
			execResult = runJob(myJob.JobName, myJob.JobParam);
		} catch (Exception e) {
			execResult = e;
			LogHelper.logError(e);
		}
		result.ExecutionResult = execResult;
		mess.MessageContent = JsonHelper.getJsonString(result);
		try {
			CommunicationHelper.sendMessagetoMaster(mess);
			//remove job from my job list
			InMemoryObjects.currentNodeJobList.remove(myJob.Id);
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (InMemoryObjects.currentNodeJobList.size() > 0) {
			String myJobId = InMemoryObjects.currentNodeJobList.keys().nextElement();
			ClientJobModel myJob = InMemoryObjects.currentNodeJobList.get(myJobId);
			ExecuteJob.executeJobAndReportResulttoMaster(myJob);
		}
	}
}
