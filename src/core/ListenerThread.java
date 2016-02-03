package core;

import config.Message;
import config.Server;

/**
 * The listening server thread
 * @author LumiG
 *
 */
public class ListenerThread implements Runnable {
    private Server server;
    
    public ListenerThread(Server server){  
        this.server = server;  
    }
    
    /**
     * Put all the msgs received into a msg queue
     */
    @Override  
    public void run() {  
    	try{
            while(true){  
            	Message msg = (Message) server.getInput().readObject();
            	MessagePasser.controller.handleReceiveMessgae(msg);
            }    
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    
    
}


