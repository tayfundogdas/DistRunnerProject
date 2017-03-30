package org.td.distrunner.commandhandlers.clientexecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

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

	private static Object runJob(String jobName, String jobParam) throws Exception {
		Object result = null;
		// download jar from master if my disk does not contain it
		downloadAndCacheJarFile(jobName);
		// run class inside jar
		URL url = new URL("file:/" + AppSettings.ProcessJarPath + getJarFile(jobName));
		URLClassLoader clsLoader = URLClassLoader.newInstance(new URL[] { url });
		@SuppressWarnings("rawtypes")
		Class cls = clsLoader.loadClass(jobName);
		Object obj = cls.newInstance();
		//find suitable method to run
		Method[] methods = cls.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals("Execute")) {
				Type[] params = method.getParameterTypes();
				for (Type type : params) {
					if (!type.getTypeName().equals(Object.class.getTypeName()))
					{
						JavaType jtype = JsonHelper.mapper.getTypeFactory().constructType(type);
						Object par = JsonHelper.mapper.readValue(jobParam, jtype);
						result = method.invoke(obj, par);
					}
				}
			}
		}
		// method = null;
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
		Object execResult = null;
		try {
			execResult = runJob(myJob.JobName, myJob.JobParam);
		} catch (Exception e) {
			execResult = e;
			LogHelper.logError(e);
		}
		result.ExecutionResult =  JsonHelper.getJsonString(execResult);
		mess.MessageContent = JsonHelper.getJsonString(result);
		try {
			CommunicationHelper.sendMessagetoMaster(mess);
			// remove job from my job list
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

	public static void main(String[] args) throws IOException {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		InMemoryObjects.AppId = uuid.toString();

		// for logging
		LogHelper.setupLog();

		ClientJobModel myJob = new ClientJobModel();
		myJob.JobName = "org.td.samples.TokenizeWordsAction";
		String input = FileUtils.readFileToString(new File("D:\\article.txt"));
		myJob.JobParam = JsonHelper.getJsonString(input);
		ExecuteJob.executeJobAndReportResulttoMaster(myJob);
	}
}
