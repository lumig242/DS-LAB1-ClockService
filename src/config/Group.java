package config;

import java.util.ArrayList;

import clockService.VectorClock;

public class Group {
	private String groupName;
	private ArrayList<String> groupMember;
	int local_index;
	private ArrayList<Message> holdBackMsgs = new ArrayList<>();
	private ArrayList<Message> messageReceived = new ArrayList<>();
	private VectorClock groupClock;
	
	public Group(String groupName, ArrayList<String> groupMember, String local_name){
		this.groupName = groupName;
		this.groupMember = groupMember;	
		local_index = groupMember.indexOf(local_name);
		groupClock = new VectorClock(groupMember.size(), local_index);
	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<String> getGroupMember() {
		return groupMember;
	}

	public String getGroupMember(int index) throws ArrayIndexOutOfBoundsException{
		return groupMember.get(index);
	}
	
	public VectorClock getClock(){
		return groupClock;
	}

	/**
	 * Check if this message is received before
	 * @param msg
	 * @return
	 */
	public Boolean receiveBefore(Message msg){
		// If already in the queue
		if(holdBackMsgs.contains(msg)){
			return true;
		}
		// If is an early message
		int j = groupMember.indexOf(((GroupMessage) msg).getOriginSource());
		if(msg.getTimestamp().getVectorTime()[j] <= groupClock.getTimeStamp()[j]){
			return true;
		}
		
		// Duplicate message appears due to some synchronizing reason
		// Hard code here first
		//if(messageReceived.contains(msg)){
		//	return true;
		//}
		return false;
	}
	
	/**
	 * Check if there is a ready-to-deliver message
	 * If so, swap it to the beginning of the list
	 * @return
	 */
	public Boolean readyToDeliver(){
		for(int i = 0; i < holdBackMsgs.size(); i++){
			Message deliverMsg = holdBackMsgs.get(i); 
			if(readyToDeliver(deliverMsg)){
				// Swap it to the begining of the list 
				holdBackMsgs.remove(i);
				holdBackMsgs.add(0, deliverMsg);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if a message is ready to deliver
	 * @return
	 */
	public Boolean readyToDeliver(Message deliverMsg){
		Integer[] deliverTimestamp = deliverMsg.getTimestamp().getVectorTime(); //Vj
		Integer[] currentTimestamp = groupClock.getTimeStamp(); // Vi
		
		// According to P657 Fig 15.15
		// j 
		int srcIndex = groupMember.indexOf(((GroupMessage)deliverMsg).getOriginSource());
		// wait until Vj[j] = vi[j] + 1 && Vj[k] <= Vi[k]
		for(int i = 0; i < groupMember.size(); i++){
			if(i == srcIndex){
				if(deliverTimestamp[i] != currentTimestamp[i] + 1){
					return false;
				}
			}else{
				if(deliverTimestamp[i] > currentTimestamp[i]){
					return false;
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Add a message received
	 * @param msg
	 */
	public void addMessage(Message msg){
		holdBackMsgs.add(0, msg);
		//if(!messageReceived.contains(msg)){
		//	messageReceived.add(msg);
		//}
	}
	
	/**
	 * Fetch a message that is ready to deliver
	 * And increment the clock
	 * @return
	 */
	public Message fetchOneMessage(){
		Message deliverMsg = holdBackMsgs.get(0);
		holdBackMsgs.remove(0);
		int j = groupMember.indexOf(((GroupMessage)deliverMsg).getOriginSource());
		groupClock.incrementAt(j);
		return deliverMsg;
	}

	
	@Override
	public String toString(){
		return "GroupName@" + groupName + 
				" Members@" + groupMember + 
				" Clock@"  + groupClock + 
				" Holdback Queue@" + holdBackMsgs;
	}
	
}
