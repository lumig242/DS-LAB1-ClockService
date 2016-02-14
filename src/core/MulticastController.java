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
			Message sendMsg = new Message(msg);
			if(!destServerName.equals(config.getLocal_name())){
				// Construct a new message and send it
				sendMsg.setDest(destServerName);
				controller.handleSendMessage(sendMsg);
			}else{
				// If send the message to self
				// Simulate this behavior by directly receiving it 
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
		LinkedBlockingQueue<Message> holdBackQueue = group.getHoldBackMsgs();
		
		// TODO: implement the reliable multicast + causual ordering algorithm
		// According to book P648 15.9 & P657 15.15
		
		// check if received
		
		// multicast(msg)
		
		// holdBackQueue.add(msg)
		
		// wait
		
		// if ready to deliver
		// holdBackQueue.poll()
		// deliverMessage(msg)
		
		// clock.increment()
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
