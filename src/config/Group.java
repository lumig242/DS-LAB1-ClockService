package config;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import clockService.VectorClock;

public class Group {
	private String groupName;
	private ArrayList<String> groupMember;
	private LinkedBlockingQueue<Message> holdBackMsgs = new LinkedBlockingQueue<Message>();
	private VectorClock groupClock;
	
	public Group(String groupName, ArrayList<String> groupMember, String local_name){
		this.groupName = groupName;
		this.groupMember = groupMember;	
		groupClock = new VectorClock(groupMember.size(), groupMember.indexOf(local_name));
	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<String> getGroupMember() {
		return groupMember;
	}

	public LinkedBlockingQueue<Message> getHoldBackMsgs() {
		return holdBackMsgs;
	}
	
	public String getGroupMember(int index) throws ArrayIndexOutOfBoundsException{
		return groupMember.get(index);
	}
	
	public VectorClock getClock(){
		return groupClock;
	}
	
	@Override
	public String toString(){
		return "GroupName@" + groupName + 
				" Members@" + groupMember + 
				" Holdback Queue@" + holdBackMsgs;
	}
	
}
