package org.td.distrunner.commandhandlers;

import java.util.concurrent.ConcurrentHashMap;

import org.td.distrunner.model.JobModel;

//this method try to schedule jobs as parallel as possible by examining data dependencies
public class MasterWorkSchedulingJob {
	//key is job id
	public static ConcurrentHashMap<String,JobModel> jobs = new ConcurrentHashMap<String,JobModel>();
	//key is clientuniqueid value is job count on client
	public static ConcurrentHashMap<String,Integer> clientJobsCount = new ConcurrentHashMap<String,Integer>();
	
	//schedule process' items to least used working node
	public static void scheduleJob() 
	{
		
	}
	
	//if a result come from assigned node forward it to waiting node and schedule job
	//and report result time for suggesting better optimized blocks
	public static void handleJobResult()
	{
		
	}
	
	//if no heartbeat from scheduled node reschedule to new node
	public static void rescheduleJob()
	{
		
	}
}
