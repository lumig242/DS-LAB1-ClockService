package core;

import java.util.concurrent.LinkedBlockingQueue;

import clockService.VectorClock;
import config.ConfigParser;
import config.Group;
import config.Message;
import config.Timestamp;

public class MulticastController {
	ConfigParser config;
	Controller controller;
	private LinkedBlockingQueue<Message> receiveMsgs;
	
	public MulticastController(ConfigParser config, LinkedBlockingQueue<Message> receiveMsgs, Controller controller){
		this.config = config;
		this.receiveMsgs = receiveMsgs;
		this.controller = controller;
	}
	
	/**
	 * The core multicast method
	 * First get the clock of the related group
	 * Update the group and send the messages to all the members (except it self)
	 * @param msg
	 * @throws InterruptedException
	 */
	public void multicast(Message msg) throws InterruptedException{
		Group group = config.getGroup(msg.getDest());
		VectorClock clock = group.getClock();
		// Increment the timestamp only once
		Timestamp timestamp = clock.getTimestampSend();
		
		// Start the B-multicast
		for(String destServerName:group.getGroupMember()){
			if(!destServerName.equals(config.getLocal_name())){
				// Construct a new message and send it
				Message sendMsg = new Message(msg);
				sendMsg.setDest(destServerName);
				controller.handleSendMessage(sendMsg);
			}else{
				// If send the message to self
				// Simulate this behavior by directly receiving it
				Message sendMsg = new Message(msg);
				controller.handleReceiveMessgae(sendMsg);
			}
		}
		//controller.handleSendMessage(msg);
	}
	
	/**
	 * Handle all the received multicast message 
	 * @param msg
	 */
	public void handleReceiveMessage(Message msg){
		Group group = config.getGroup(msg.getDest());
		VectorClock clock = group.getClock();
		
		// TODO: implement the reliable multicast + causual ordering algorithm
		// According to book P648 15.9 & P657 15.15
		
		// check if received
		if(!group.receiveBefore(msg)){
			try {
				// Multicast it and record in the holdback queue
				multicast(msg);
				group.addMessage(msg);
				// Deliver all the possible message and
				// Update the clock
				while(group.readyToDeliver()){
					Message deliverMsg = group.fetchOneMessage();
					deliverMessage(deliverMsg);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The multicast message received is ready to deliver
	 * Simply put the message into the messagePasser's 
	 * receive queue.
	 * @param msg
	 */
	public void deliverMessage(Message msg){
		receiveMsgs.add(msg);
	}
}
