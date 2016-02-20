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
	private int replyCount;
	private int groupmemberNo;
	
	public LockController(MulticastController mController, Controller controller, ConfigParser config) {
		this.multicastController = mController;
		this.controller = controller;
		this.config = config;
		requestQueue = new PriorityQueue<LockMessage>();
		for(String gName: config.getLocalGroupList()){
			groupmemberNo += config.getGroup(gName).getGroupMember().size();
		}
	}
	
	public void enter() {
		this.state = STATE.WANTED;
		notifyGroup(LOCKTYPE.REQUEST);
		
		//wait
		// set a global counter as K, when receiving reply, k--, until k==0, alter state as HELD.
		replyCount = groupmemberNo;
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
		if(!state.equals(STATE.RELEASED)){
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
		for(String groupName: config.getLocalGroupList()){
			LockMessage lmsg = new LockMessage(groupName, "request", "payload", locktype);
			lmsg.setOriginSource(config.getLocal_name());
			lmsg.setTimestamp(timestamp);
			try {
				multicastController.multicast(lmsg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		LOCKTYPE locktype = lmsg.getLocktype();
		if(locktype.equals(LOCKTYPE.RELEASE)){
			receiveRelease();
		}
		else if(locktype.equals(LOCKTYPE.REQUEST)){
			receiveRequest(lmsg);
		}
		else if(locktype.equals(LOCKTYPE.REPLY)){
			replyCount--;
		}
	}
}
