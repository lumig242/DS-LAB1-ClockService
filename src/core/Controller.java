package core;

import java.util.concurrent.LinkedBlockingQueue;

import clockService.Clock;
import clockService.LogicClock;
import config.ConfigParser;
import config.Message;
import config.Rule;

/**
 * Controller to handle send/receive messages
 *
 */
public final class Controller {
	private ConfigParser config;
	private LinkedBlockingQueue<Message> receiveMsgs;
	private LinkedBlockingQueue<Message> delayReceiveMsgs;
	private LinkedBlockingQueue<Message> sendMsgs;
	private LinkedBlockingQueue<Message> delaySendMsgs;
	private Clock clock;
	
	public Controller(ConfigParser config, LinkedBlockingQueue<Message> receiveMsgs, 
						LinkedBlockingQueue<Message> delayReceiveMsgs,
						LinkedBlockingQueue<Message> sendMsgs,
						LinkedBlockingQueue<Message> delaySendMsgs,
						Clock clock){
		this.config = config;
		this.receiveMsgs = receiveMsgs;
		this.delayReceiveMsgs = delayReceiveMsgs;
		this.sendMsgs = sendMsgs;
		this.delaySendMsgs = delaySendMsgs;
		this.clock = clock;
	}
	
	public void handleReceiveMessgae(Message msg) throws InterruptedException{
		if(!config.isUpToDate()) {
			config.reconfiguration();
		}
		
		System.out.println("Handle " + msg);
		// Update the system clock
		clock.setTimeReceive(msg.getTimestamp());
		//((LogicClock)clock).debugPrintClock();
		Rule rule = config.matchReceiveRule(msg.getSource(), msg.getDest(), msg.getKind(), msg.get_seqNum());
		if(rule == null) {
			// Put the first message in the queue
	        //System.out.println(msg + "receive");
	        receiveMsgs.put(msg);
	        //System.out.println(receiveMsgs);
	        
	        while(!delayReceiveMsgs.isEmpty()) {
	        	receiveMsgs.put(delayReceiveMsgs.poll());
	        }
		} else {
			switch(rule.getAction().toLowerCase()) {
				case "drop" : {break;}
				case "dropafter" : {break;}
				case "delay" : {
					delayReceiveMsgs.put(msg);
				}
			}
		}
	}
	
	public void handleSendMessage(Message message) throws InterruptedException{
		if(!config.isUpToDate()) {
			config.reconfiguration();
		}
		
		// Attach timestamp
		message.setTimestamp(clock.getTimestampSend());
		//System.out.println("Attached " + message.getTimestamp());

		Rule rule = config.matchSendRule(message.getSource(), message.getDest(), message.getKind(), message.get_seqNum());
		//System.out.println("Rule:" + rule);
		if(rule == null) {
			sendMsgs.put(message);
			//all delayed messages are triggered to send
			while(!delaySendMsgs.isEmpty()) {
				sendMsgs.put(delaySendMsgs.poll());
			}
		} else {
			switch(rule.getAction().toLowerCase()) {
			    case "drop" :{return;}
			    case "dropafter" :{return;}
			    case "delay" :
			    {
			    	delaySendMsgs.put(message);
			    	break;
			    }
			}
		}
	}
}
