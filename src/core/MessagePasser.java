package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

import clockService.Clock;
import clockService.ClockFactory;
import config.ConfigParser;
import config.Message;
import config.Server;

/**
 * The main class
 * @author LumiG
 *
 */
public class MessagePasser {
	private Server localServer;
	public Controller controller;
	public Clock clock;
	private LinkedBlockingQueue<Message> sendMsgs = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> delaySendMsgs = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> receiveMsgs = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<Message> delayReceiveMsgs = new LinkedBlockingQueue<Message>();
	
	ConfigParser config;
	private int sequenceNumber = 0;
	
	public MessagePasser(String configuration_filename, String local_name){
		//Use logical clock as default
		this(configuration_filename, local_name, "logic");
	}
	
	public MessagePasser(String configuration_filename, String local_name, String clockType){
		// Parse the Yaml configuration file
		config = new ConfigParser(configuration_filename);
		// Add in lab 1
		// Start the clock service due to clockType specified
		if(clockType.equals("logic")){
			clock = ClockFactory.getClockInstance(clockType);
		}else{
			clock = ClockFactory.getClockInstance(clockType, config.getProcessSize(), config.getIndex(local_name));
		}
		localServer = config.getServer(local_name);
		// Refined in lab1
		// Add a controller to remove duplicate code
		controller = new Controller(config, receiveMsgs, delayReceiveMsgs, sendMsgs, delaySendMsgs, clock);
		
		// Start the thread to keep listening on port
		// Start separate thread for all the clients connected
		Thread t = new Thread(new Runnable() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				try {
					ServerSocket socket = new ServerSocket(localServer.getPort());
					System.out.println("Listening on " + localServer);
					Socket client = null;
					while(true){
						// Client connected
						client = socket.accept();
						// Set the input output stream for this node
			        	ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
			        	ObjectInputStream input = new ObjectInputStream(client.getInputStream());
			        
			        	// We have to read the first message to get the name of client
			        	Message msg =  (Message) input.readObject();
			        	
			        	// Handle the first receive message
			        	controller.handleReceiveMessgae(msg);
			        
			        	Server server = config.getServer(msg.getSource());
			        	System.out.println("Connected client!  " + server);
						/**
						 * Trick thing here. To avoid race condition that two server are connecting to 
						 * each other at the same time and thus construct two tcp connection
						 * The node with a SMALLER name will not start the listening session
						 */
						if(!(server.getOutput() != null && server.getName().compareTo(msg.getSource()) > 0)){
			        	//if(!(server.getOutput() != null)){	
			        		// Store the input, output stream
		                	server.setOutput(output);
							server.setInput(input);
							// Start a new thread to listen from the node
							new Thread(new ListenerThread(server, controller)).start();
						}
					}
				} catch (IOException | ClassNotFoundException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		
		// Start a consumer thread to send all the messages in the queue
		new Thread(new SendConsumerThread(sendMsgs, config)).start();
		
	}
	
	/**
	 * Send a single messages
	 * First check if the tcp connection is already established
	 * Put the message in the queue, acts as a producer
	 * @param message
	 */
	public void send(Message message){
		
		message.set_source(localServer.getName());
		//System.out.println("Sent: " + message);
		Server destServer = config.getServer(message.getDest());
		//System.out.println("Destserver: " + destServer);
		
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
				new Thread(new ListenerThread(destServer, controller)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Put the msg into queue
		try {
			message.set_seqNum(sequenceNumber++);
			controller.handleSendMessage(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Message receive(){
		if(receiveMsgs.isEmpty()){
			//System.out.println("No messages received!");
			return null;
		}
		Message msg;
		// May change this to receiveMsgs.take() to make it unblock
		try {
			msg = receiveMsgs.take();
			//System.out.println(receiveMsgs);
			return msg;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	};
}
