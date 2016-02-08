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
		String clockType = "Vector";
		if(args.length > 0){
			if(args[0].equals("logic")){
				clockType = "logic";
			}
		}
 
		
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
		final MessagePasser mp = new MessagePasser("Configuration.yaml", localName, clockType);
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
            	while(true){
	            	Scanner sc = new Scanner(System.in);
	            	// TYPE, MSG DEST, MSG TYPE, MSG DATA
	            	// 1 as receive, other all send
	            	String[] line = sc.nextLine().split(" ");
	            	if(line[0].equals("1")){
	            		//receive
	            		Message msg = mp.receive();
	            		if(msg != null){
	            			System.out.println("Recieve in the application " + localName + " : " + msg);
	            		}else{
	            			System.out.println("No Messages received yet!");
	            		}
	            		continue;
	            	}
	            	if(line.length < 3){
	            		System.out.println("Not enough arguments!");
	            		continue;
	            	}
	            	StringBuffer buffer = new StringBuffer();
	            	for(int i = 3; i< line.length; i++){
	            		buffer.append(" ");
	            		buffer.append(line[i]);
	            	}
	            	Message msg = new Message(line[1], line[2], buffer.toString());
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