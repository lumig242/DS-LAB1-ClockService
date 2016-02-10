package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

import config.Message;
import config.Timestamp;
import core.MessagePasser;

public class LogApp {
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
		
		// Start the receive thread
		final MessagePasser mp = new MessagePasser("Configuration.yaml", localName, clockType);
		final Logger logger = new Logger("message.log");
        // Background reveice thread
		Thread t = new Thread() {
            public void run() {
            	while(true){
            		try{
            			Thread.sleep(500);
            		}catch(Exception e){
            			e.printStackTrace();
            		}
            		Message msg = mp.receive();
	        		if(msg != null){
	        			System.out.println("Recieve in the application " + localName + " : " + msg);
	        			logger.recordOneMessage(msg);
	        		}
            	}	
            }
        };
        t.start();       
        while(true){
        	Scanner sc = new Scanner(System.in);
        	if(sc.nextLine().length() < 2)	continue;
        	System.out.println("===== Now writing logs=====");
        	logger.writeAllLogs();
        }
	}
}
