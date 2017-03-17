package org.td.distrunner.commandhandlers.assignmaster;

public class AssignNewMasterJob {

	//assign new master among master candidates stated in settings
	
	// in case of heartBeats fails for threshold according to jobcount*waittimeconstant
	public void broadcastNewMasterRequesttoMasterCandidates()
	{
		
	}
	
	//if new master request already taken
	public void cancelMyNewMasterRequesttoMasterCandidatesMessage()
	{
		
	}
	
	//after new master selected it notify all nodes
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
