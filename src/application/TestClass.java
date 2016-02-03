package application;

import config.Message;
import core.MessagePasser;

public class TestClass {
	public static void main(String[] args){
		final MessagePasser mp1 = new MessagePasser("Configuration.yaml", "alice", "vector");
		Thread recieve1 = new Thread() {
            public void run() {
            	while(true){
            		try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		Message msg = mp1.receive();
            		if(msg != null){
            			//System.out.println("Recieve in the application alice///" + msg);
            		}
            	}
            }
        };
        //recieve1.start();
        
		Thread send1 = new Thread() {
            public void run() {
            	while(true){
	            	try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                mp1.send(new Message("bob", "test", "testdata!!!!!"));
            	}
            }
        };
        send1.start();
        
        
        
        
        final MessagePasser mp2 = new MessagePasser("Configuration.yaml", "bob", "vector");
		Thread recieve2 = new Thread() {
            public void run() {
            	while(true){
            		try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		Message msg = mp2.receive();
            		if(msg != null){
            			System.out.println("Recieve in the application bob///" + msg);
            		}
            	}
            }
        };
        recieve2.start();
        

		Thread send2 = new Thread() {
            public void run() {
            	while(true){
	            	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                mp2.send(new Message("alice", "test", "testdata!!!!!"));
            	}
            }
        };
        //send2.start();
        
        
	}
}