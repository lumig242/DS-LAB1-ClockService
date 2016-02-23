package core;

import java.util.concurrent.LinkedBlockingQueue;

import clockService.VectorClock;
import config.ConfigParser;
import config.Group;
import config.GroupMessage;
import config.LockMessage;
import config.Message;
import config.Timestamp;

public class MulticastController {
	ConfigParser config;
	Controller controller;
	private LinkedBlockingQueue<Message> receiveMsgs;
	private LockController lockController;
	
	public MulticastController(ConfigParser config, LinkedBlockingQueue<Message> receiveMsgs, Controller controller){
		this.config = config;
		this.receiveMsgs = receiveMsgs;
		this.controller = controller;
	}
	
	public void setLockController(LockController lockController){
		this.lockController = lockController;
	}
	
	/**
	 * The core multicast method
	 * First get the clock of the related group
	 * Update the group and send the messages to all the members (except it self)
	 * @param msg
	 * @throws InterruptedException
	 */
	public void multicast(Message msg) throws InterruptedException{
		GroupMessage gmsg = (GroupMessage) msg;
		Group group = config.getGroup(gmsg.getGroupName());
		gmsg.set_source(config.getLocal_name());
		// Increment the timestamp only once
		
		System.out.println("====Multicast: " + gmsg);
		System.out.println("====Multicast: " + group);
		// Start the B-multicast
		for(String destServerName:group.getGroupMember()){
			if(!destServerName.equals(config.getLocal_name())){
				// Construct a new message and send it
				Message sendMsg;
				if(gmsg instanceof LockMessage){
					sendMsg = new LockMessage((LockMessage)gmsg);
				}else{
					sendMsg = new GroupMessage(gmsg);
				}
				sendMsg.setDest(destServerName);
				controller.handleSendMessage(sendMsg);
			}else{
				// If send the message to self
				// Simulate this behavior by directly receiving it
				// If I'm in this group
				if(group.getGroupMember().contains(config.getLocal_name())){
					Message sendMsg;
					if(gmsg instanceof LockMessage){
						sendMsg = new LockMessage((LockMessage)gmsg);
					}else{
						sendMsg = new GroupMessage(gmsg);
					}
					sendMsg.setDest(config.getLocal_name());
					controller.handleReceiveMessgae(sendMsg);
					//receiveMsgs.add(msg);
				}
			}
		}
	}
	
	/**
	 * Handle all the received multicast message 
	 * @param msg
	 */
	public void handleMulticastReceiveMessage(Message msg){
		GroupMessage gmsg = (GroupMessage) msg;
		Group group = config.getGroup(gmsg.getGroupName());
		VectorClock clock = group.getClock();
		
		// TODO: implement the reliable multicast + causual ordering algorithm
		// According to book P648 15.9 & P657 15.15
		
		System.out.println("====Receive Multicast: " + gmsg);
		System.out.println("====Receive Multicast: " + group);
		


		if(!group.receiveBefore(gmsg)){
			try {
				// Multicast it and record in the holdback queue
				if(!config.getLocal_name().equals(gmsg.getSource()) &&
						!config.getLocal_name().equals(gmsg.getOriginSource())){
					multicast(new GroupMessage(gmsg));
				}
				group.addMessage(gmsg);
				System.out.println("========addMsg" + group);
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
