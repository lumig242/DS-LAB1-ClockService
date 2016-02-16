package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import clockService.Clock;
import clockService.LogicClock;
import config.ConfigParser;
import config.GroupMessage;
import config.Message;
import config.Rule;
import config.Server;

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
	private MulticastController multicstController;
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
	
	public void setMulticastController(MulticastController multicastController){
		this.multicstController = multicastController;
	}
	
	public void handleReceiveMessgae(Message msg) throws InterruptedException{
		if(!config.isUpToDate()) {
			config.reconfiguration();
		}
		
		//System.out.println("Handle " + msg);
		//((LogicClock)clock).debugPrintClock();
		Rule rule = config.matchReceiveRule(msg.getSource(), msg.getDest(), msg.getKind(), msg.get_seqNum());
		if(rule == null) {
			// Put the first message in the queue
	        //System.out.println(msg + "receive");
	        //receiveMsgs.put(msg);
			deliverReceiveMessage(msg);
	        //System.out.println(receiveMsgs);
	        
			while(!delayReceiveMsgs.isEmpty()) {
	        	//receiveMsgs.put(delayReceiveMsgs.poll());
				deliverReceiveMessage(delayReceiveMsgs.poll());
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
	
	/**
	 * Deliver a receive message (after rule matching).
	 * If point to point cast, put into the final deliver queue
	 * If multicast, call the group handler to deal with it
	 * @param message
	 * @throws InterruptedException
	 */
	public void deliverReceiveMessage(Message msg) throws InterruptedException{
		if(!(msg instanceof GroupMessage)) {
			// Update the system clock
			clock.setTimeReceive(msg.getTimestamp());
			receiveMsgs.put(msg);
		}else{
			multicstController.handleMulticastReceiveMessage(msg);
		}
	}
	
	/**
	 * Handle all the send request
	 * Only send the message to the dest server
	 * @param message
	 * @throws InterruptedException
	 */
	public void handleSendMessage(Message message) throws InterruptedException{
		if(!config.isUpToDate()) {
			config.reconfiguration();
		}
		

		Server destServer = config.getServer(message.getDest());
		
		// if this is the first msg sent
		// act as the client
		// create a new TCP connection
		if(destServer.getOutput() == null){
			try {
				System.out.println("Connect to Destserver: " + destServer);
				@SuppressWarnings("resource")
				Socket socket = new Socket(destServer.getIp(), destServer.getPort());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				destServer.setInput(inputStream);
				destServer.setOutput(outputStream);
				new Thread(new ListenerThread(destServer, this)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
