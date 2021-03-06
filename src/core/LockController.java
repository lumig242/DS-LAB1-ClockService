package core;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import config.ConfigParser;
import config.LockMessage;
import config.LockMessage.LOCKTYPE;
import config.Timestamp;
import core.MessagePasser;

public class LockController {

	private enum STATE {RELEASED, WANTED, HELD}
	
	private boolean vote = false;
	private STATE state = STATE.RELEASED;
	
	private MulticastController multicastController;
	private Controller controller;
	private ConfigParser config;
	private Queue<LockMessage> requestQueue;
	private List<LockMessage> receiveBefore = new ArrayList<>();
	private int replyCount;
	private int groupmemberNo;
	
	private static int seqNumber = 0;
	
	public LockController(MulticastController mController, Controller controller, ConfigParser config) {
		this.multicastController = mController;
		this.controller = controller;
		this.config = config;
		requestQueue = new PriorityQueue<LockMessage>();
		groupmemberNo = config.getGroup(config.getLocal_group_name()).getGroupMember().size();
			
		
	}
	
	public void enter() {
		if(!this.state.equals(STATE.RELEASED)){
			System.out.println("I cannot request the sectoin again!");
			return;
		}
		this.state = STATE.WANTED;
		//wait
		// set a global counter as K, when receiving reply, k--, until k==0, alter state as HELD.
		replyCount = groupmemberNo;

		notifyGroup(LOCKTYPE.REQUEST);		
	}
	
	public void receiveRequest(LockMessage lmsg) {
		if(state.equals(STATE.HELD) || vote) {
			requestQueue.offer(lmsg);
		} else {
			replyMessage(lmsg.getOriginSource());
			vote = true;
		}
	}
	
	public void exit() {
		if(!state.equals(STATE.HELD)){
			System.out.println("Currently not in the critical section!");
			return;
		}
		state = STATE.RELEASED;
		notifyGroup(LOCKTYPE.RELEASE);
	}
	
	public void receiveRelease() {
		if(!requestQueue.isEmpty()){
			replyMessage(requestQueue.poll().getOriginSource());
			vote = false;
		}
		else{
			vote = false;
		}
	}
	
	public void replyMessage(String dest) {
		LockMessage replyMessage = new LockMessage("message", "reply","payload", LockMessage.LOCKTYPE.REPLY);
		replyMessage.setDest(dest);
		replyMessage.setOriginSource(config.getLocal_name());
		replyMessage.set_source(config.getLocal_name());
		replyMessage.set_seqNum(seqNumber++);
		try {
			controller.handleSendMessage(replyMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Notify all the processes in Vi
	 * @param locktype: either request or release
	 */
	private void notifyGroup(LOCKTYPE locktype){
		//multicast request
		Timestamp timestamp = MessagePasser.lockMsgLogicClock.getTimestampSend();
		String groupName = config.getLocal_group_name();
		LockMessage lmsg = new LockMessage(groupName, "notify", "payload", locktype);
		lmsg.setOriginSource(config.getLocal_name());
		lmsg.setTimestamp(timestamp);
		lmsg.set_seqNum(seqNumber);
		try {
			multicastController.multicast(lmsg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		seqNumber ++;
	}
	
	/**
	 * Used by the application to see if current process is holding the critical section
	 * @return boolean
	 */
	public Boolean isHoldingCritical(){
		if(state.equals(STATE.HELD)){
			return true;
		}
		return false;
	}
	
	
	public void handleLockReceiveMessage(LockMessage lmsg){
		System.out.println("-=-=-=Receive Lock Message" + lmsg);
		// Handle this message
		LOCKTYPE locktype = lmsg.getLocktype();
		if(locktype.equals(LOCKTYPE.REPLY)){
			System.out.println("Handle Reply");
			replyCount--;
			if(replyCount == 0){
				state = STATE.HELD;
			}
			System.out.println(replyCount + " reply counts to gather.");
			System.out.println("Current status: " + state);
			return;
		}
		
		// Ugly remove the duplicate messages
		if(receiveBefore.contains(lmsg))	return;
		receiveBefore.add(lmsg);
		
		if(locktype.equals(LOCKTYPE.RELEASE)){
			receiveRelease();
		}
		else if(locktype.equals(LOCKTYPE.REQUEST)){
			receiveRequest(lmsg);
		}
		
		if(!lmsg.getOriginSource().equals(config.getLocal_name()) && !lmsg.getSource().equals(config.getLocal_name())){
			// Reliable multicast
			try {
				multicastController.multicast(new LockMessage(lmsg));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return;
	}
}
