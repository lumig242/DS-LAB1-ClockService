package core;

import java.util.concurrent.LinkedBlockingQueue;

import config.ConfigParser;
import config.Message;
import config.Rule;
import config.Server;

/**
 * The listening server thread
 * @author LumiG
 *
 */
public class ListenerThread implements Runnable {
    private Server server;
    LinkedBlockingQueue<Message> receiveMsgs = new LinkedBlockingQueue<Message>();
    private LinkedBlockingQueue<Message> delayReceiveMsgs = new LinkedBlockingQueue<Message>();
    ConfigParser config;
    
    public ListenerThread(Server server, LinkedBlockingQueue<Message> receiveMsgs, LinkedBlockingQueue<Message> delayReceiveMsgs, ConfigParser config){  
        this.server = server;  
        this.receiveMsgs = receiveMsgs;
        this.delayReceiveMsgs = delayReceiveMsgs;
        this.config = config;
    }
    
    /**
     * Put all the msgs received into a msg queue
     */
    @Override  
    public void run() {  
    	try{
            while(true){  
            	Message msg = (Message) server.getInput().readObject();
            	if(!config.isUpToDate()) {
    				config.reconfiguration();
    			}
            	Rule rule = config.matchReceiveRule(msg.getSource(), msg.getDest(), msg.getKind(), msg.get_seqNum());
            	if(rule == null) {
	                receiveMsgs.put(msg);
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
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    
    
}


