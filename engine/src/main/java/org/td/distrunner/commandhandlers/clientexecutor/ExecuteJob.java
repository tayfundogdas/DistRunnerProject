package org.td.distrunner.commandhandlers.clientexecutor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

public class ExecuteJob {

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

	private static void downloadAndCacheJarFile(String jobName) throws IOException {
		String packageName = jobName.substring(0, jobName.lastIndexOf('.')) + ".jar";
		Stream<Path> stream = Files.find(Paths.get(AppSettings.ProcessJarPath), 0,
				(path, attr) -> path.startsWith(packageName));
		boolean isJarInLocal = stream.findFirst().isPresent();
		stream.close();
		if (!isJarInLocal) {
			// download file
			URL website = new URL(getFullMasterAPIAddress(packageName));
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(AppSettings.ProcessJarPath + packageName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		}
	}
	
	private static Object runJob(String jobName) throws IOException
	{
		Object result = null;
		//download jar from master if my disk does not contain it
		downloadAndCacheJarFile(jobName);
		//run class inside jar
		
		return result;
	}

	public static void executeJobsAndReportResulttoMaster(List<ClientJobModel> myJobs) {
		// run all job list and report result
		for (ClientJobModel job : myJobs) {
			Message<String> mess = new Message<String>();
			mess.MessageType = MessageTypes.ExecutionResultMessage;
			ExecutionResultModel result = new ExecutionResultModel();
			result.JobId = job.Id; 
			Object execResult = null;
			 try {
				execResult = runJob(job.JobName);
			} catch (IOException e) {
				LogHelper.logError(e);
			}
			result.ExecutionResult = execResult;
			mess.MessageContent = Message.toJsonString(result);
			try {
				WebSocketClientChannel.sendMessagetoMaster(mess);
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}

	}
}
