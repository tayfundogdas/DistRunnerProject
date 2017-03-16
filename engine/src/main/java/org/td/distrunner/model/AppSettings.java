package org.td.distrunner.model;

//these settings will be read from xml file
public class AppSettings {
	//empty for master
	public static String MasterAddress = "127.0.0.1";
	public static String WSChannelName = "ws";
	public static String APIChannelName = "api";
	public static int JettyPort = 8080;
	public static String HeartBeatRequestJobCronSchedule = "0/5 * * * * ?";
	public static String MasterCandidatesSyncRequestJobCronSchedule = "0/5 * * * * ?";
	//1 if node is master candidate else 0
	public static Byte IsMasterCandidate = 1;
	
	public void saveToFile(String filename)
	{
		
	}
	
	public void loadFromFile(String filename)
	{
		
	}
}
