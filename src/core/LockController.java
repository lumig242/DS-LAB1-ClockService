package core;

import java.util.PriorityQueue;
import java.util.Queue;

import config.ConfigParser;
import config.LockMessage;
import config.Timestamp;

public class LockController {

	private enum STATE {RELEASED, WANTED, HELD}
	
	private boolean vote = false;
	private STATE state = STATE.RELEASED;
	
	private MulticastController multicastController;
	private Controller controller;
	private ConfigParser config;
	private Queue<LockMessage> requestQueue;
	
	public LockController(MulticastController mController, Controller controller, ConfigParser config) {
		this.multicastController = mController;
		this.controller = controller;
		this.config = config;
		requestQueue = new PriorityQueue<LockMessage>();
	}
	
	public void enter() {
		this.state = STATE.WANTED;
		
		//multicast request
		String[] groups = config.getGroups();
		Timestamp timestamp = MessagePasser.logicClock.getTimestampSend();
		for(String groupName: groups) {
			LockMessage lmsg = new LockMessage(groupName, "request", "payload", LockMessage.LOCKTYPE.REQUEST);
			lmsg.setOriginSource(config.getLocal_name());
			lmsg.setTimestamp(timestamp);
			multicastController.multicast(lmsg);
		}
		
		//wait
		// set a global counter as K, when receiving reply, k--, until k==0, alter state as HELD.
	}
	
	public void receiveRequest(LockMessage lmsg) {
		if(state.compareTo(STATE.HELD)==0 || vote) {
			requestQueue.offer(lmsg);
		} else {
			replyMessage(lmsg.getOriginSource());
			vote = true;
		}
	}
	
	public void exit() {
		
	}
	
	public void receiveRelease() {
		
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
	
	
	

}
