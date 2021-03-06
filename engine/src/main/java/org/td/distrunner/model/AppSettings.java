package org.td.distrunner.model;

//these settings will be read from xml file
public class AppSettings {
	// empty for master
	public static String MasterAddress = "127.0.0.1";
	// web socket channel name
	public static String WSChannelName = "ws";
	// api channel name
	public static String APIChannelName = "api";
	// jetty port for server
	public static Integer JettyPort = 8080;
	// heart beat request timing
	public static String HeartBeatRequestJobCronSchedule = "0/5 * * * * ?";
	// master candidate sync request timing
	public static String MasterCandidatesSyncRequestJobCronSchedule = "0/5 * * * * ?";
	// 1 if node is master candidate else 0
	public static Byte IsMasterCandidate = 1;
	// constant for heart beat threshold
	public static Byte HeartBeatTreshold = 5;
	// constant for calculating jobcount*waittimeconstant for new master request
	// in seconds
	public static Integer MasterRequestWaitTimeConstant = 5;
	public static String ApplicationLogSettingsPath = "D:\\ApplicationLogSettings.xml";
	public static String RequestLogSettingsPath = "D:\\RequestLogSettings.xml";
	public static String ProcessJarPath = "D:\\ProcessJars\\";
	public static String UploadTemp = "C:\\Users\\tayfundogdas\\AppData\\Local\\Temp";

	public static void saveToFile() {

	}

	public static void loadFromFile() {

	}
}
