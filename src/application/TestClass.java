package application;

import config.Message;
import core.MessagePasser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;


public class TestClass {
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		final String localName;
		// Get the iter Times
		int l = 1;
		if(args.length > 0){
			try{
				l = Integer.parseInt(args[0]);
			}catch(Exception e){
				System.out.println("Invalid number input! Will only send once");
			}
		}
		final int limit = l; 
		
		// Parse the application configuration
		Yaml yaml = new Yaml();
		InputStream input;
		try {
			input = new FileInputStream(new File("ApplicationConfig.yaml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Map<String, Object> values = (Map<String, Object>) yaml.load(input);
		localName = (String) values.get("name");
		final ArrayList<Message> sendMessages = new ArrayList<Message>();
		ArrayList<Map<String, String>> msgs = (ArrayList<Map<String, String>>) values.get("send");
		for(Map<String, String> msg: msgs){
			sendMessages.add(new Message(msg.get("dest"), msg.get("kind"), msg.get("data")));
		}
		// Get all the messages to send
		// System.out.println(sendMessages);
		
		// Start the application
		// Receiver thread
		final MessagePasser mp = new MessagePasser("Configuration.yaml", localName);
		Thread recieve1 = new Thread() {
            public void run() {
            	while(true){
            		try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		Message msg = mp.receive();
            		if(msg != null){
            			System.out.println("Recieve in the application " + localName + " : " + msg);
            		}
            	}
            }
        };
        recieve1.start();
        

        try {
        	System.out.println("Waiting for all the nodes to be set!");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Sender thread
		Thread send1 = new Thread() {
            public void run() {
        		/*
            	for(int i = 0; i < limit; i++){
        			for(Message msg: sendMessages){
		            	try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                mp.send(msg);
		                System.out.println("Sent Message: " + msg);
        			}
        		}*/
            	while(true){
	            	Scanner sc = new Scanner(System.in);
	            	String[] line = sc.nextLine().split(" ");
	            	if(line.length < 2){
	            		System.out.println("Not enough arguments!");
	            		continue;
	            	}
	            	StringBuffer buffer = new StringBuffer();
	            	for(int i = 2; i< line.length; i++){
	            		buffer.append(" ");
	            		buffer.append(line[i]);
	            	}
	            	Message msg = new Message(line[0], line[1], buffer.toString());
	            	mp.send(msg);
	            	System.out.println("Sent Message: " + msg);
            	}
            }
        };
        send1.start();

	}
	
	public static void print_help(){
		System.out.println("Usage: [destination] [kind] [message_content]");
	}
}
