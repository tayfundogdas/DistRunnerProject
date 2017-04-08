package distrunner.engine;

import static org.junit.Assert.*;

import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.td.distrunner.commandhandlers.clientexecutor.ExecuteJob;
import org.td.distrunner.commandhandlers.heartbeat.HeartBeatRequestJob;
import org.td.distrunner.commandhandlers.workschedule.MasterWorkSchedulingJob;
import org.td.distrunner.communication.mock.ClientSocket;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.CacheHelper;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;

public class ProcessRunnerTest {

	ClientSocket commChannel = new ClientSocket();
	HeartBeatRequestJob hbJob = new HeartBeatRequestJob();
	ExecuteJob execJob = new ExecuteJob();

	@Before
	public void setUp() throws Exception {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		InMemoryObjects.AppId = uuid.toString();

		// for logging
		LogHelper.setupLog();

		// for communication channel
		CommunicationHelper.setClientMode(commChannel);

		// for loading process cache
		CacheHelper.LoadProcessCache();

		// put this as client
		hbJob.execute(null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testJobRun() throws Exception {
		// register client
		hbJob.execute(null);

		// start job
		String firstParam = JsonHelper.getJsonString("https://en.wikipedia.org/wiki/Java");
		MasterWorkSchedulingJob.startProcess("org.td.samples.StringProcessor", firstParam);
		assertEquals(InMemoryObjects.clientJobs.size(), 1);

		// on client run job
		for (int i = 0; i < 3; ++i) {
			// refresh client job retrieval
			execJob.execute(null);
		}

		while (InMemoryObjects.clientJobs.size() > 0)
			execJob.execute(null);

		// look if process removed from table
		System.out.println(InMemoryObjects.clientJobs.size());

		/*
		 * StringBuilder dump = new StringBuilder(); for (Message mess :
		 * commChannel.Messages) { dump.append(mess.toString());
		 * dump.append(System.lineSeparator()); }
		 * FileUtils.writeStringToFile(new File("D:\\output.txt"),
		 * dump.toString());
		 */
	}

	public static void main(String[] args) throws Exception {
		ProcessRunnerTest test = new ProcessRunnerTest();
		test.setUp();
		test.testJobRun();
	}

}
