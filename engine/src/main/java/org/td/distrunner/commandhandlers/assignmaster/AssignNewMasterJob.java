package org.td.distrunner.commandhandlers.assignmaster;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.AppSettings;

public class AssignNewMasterJob {

	//assign new master among master candidates stated in settings
	
	// in case of heartBeats fails for threshold broadcast new master request according 
	// to jobcount*waittimeconstant to master candidates
	public void broadcastNewMasterRequesttoMasterCandidates()
	{
		if (InMemoryObjects.heartBeatFailCount >= AppSettings.HeartBeatTreshold)
		{
			
		}
	}
	
	//if new master request already taken cancel my request
	public void cancelMyNewMasterRequesttoMasterCandidatesMessage()
	{
		
	}
	
	//after new master selected notify all nodes
	public void broadcastNewMasterMessagetoNodes()
	{
		
	}
	
	//below two methods for all nodes notified for master change
	public void ensureAllNodesHeartbeatsReceived()
	{
		
	}
	
	public void ensureAllMasterCandidatesSyncReqReceived()
	{
		
	}
	
	
}
