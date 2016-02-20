package application;

import config.GroupMessage;
import config.Message;
import config.Timestamp;
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
        
//        GroupMessage msg = new GroupMessage("dest", "kind", "data");
//        msg.set_source("alice");
//        Integer[] a = {1,1,1,1,2};
//        msg.setTimestamp( new Timestamp(a));
//        System.out.println(msg);
//        GroupMessage copyMsg = new GroupMessage(msg);
//        System.out.println(copyMsg);
        
        
        // Sender thread
		Thread send1 = new Thread() {
            public void run() {
            	while(true){
	            	try{
            		Scanner sc = new Scanner(System.in);
	            	// TYPE, MSG DEST, MSG TYPE, MSG DATA
	            	// 1 as receive, other all send
	            	String[] line = sc.nextLine().split(" ");
	            	
	            	switch(line[0]) {
		            	case "s" : {
		            		StringBuffer buffer = new StringBuffer();
			            	for(int i = 3; i< line.length; i++){
			            		buffer.append(" ");
			            		buffer.append(line[i]);
			            	}
			            	Message msg = new Message(line[1], line[2], buffer.toString());
			            	mp.send(msg);
			            	System.out.println("Sent Message: " + msg);
			            	break;
		            	}
		            	case "r" : {
		            		Message msg = mp.receive();
		            		if(msg != null){
		            			System.out.println("Recieve in the application " + localName + " : " + msg);
		            		}else{
		            			System.out.println("No Messages received yet!");
		            		}
		            		continue;
		            	}
		            	case "e" : {
		            		mp.triggerEvent();
		            		break;
		            	}
		            	case "m" : {
		            		StringBuffer buffer = new StringBuffer();
			            	for(int i = 3; i< line.length; i++){
			            		buffer.append(" ");
			            		buffer.append(line[i]);
			            	}
			            	GroupMessage msg = new GroupMessage(line[1], line[2], buffer.toString());
			            	mp.multicast(msg);
			            	System.out.println("Multicast Message: " + msg);
			            	break;
		            	}
		            	case "enter":
		            		mp.enterCritical();
		            		break;
		            	case "exit":
		            		mp.exitCritical();
		            		break;
		            	case "show":
		            		if(mp.isHoldingCritical()){
		            			System.out.println("I have the critical section now! Have fun!");
		            		}else{
		            			System.out.println("I don't have the section. So sad :( ");
		            		}
		            	default:
		            		continue;
	            	}
	            	}catch(Exception e){
	            		e.printStackTrace();
	            	}
            	}
            }
        };
        send1.start();
	}
	
	public static void print_help(){
		System.out.println("Usage: [destination] [kind] [message_content]");
	}
}