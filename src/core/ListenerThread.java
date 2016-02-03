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
    private Controller controller;
    
    public ListenerThread(Server server, Controller controller){  
        this.server = server;  
        this.controller = controller;
    }
    
    /**
     * Put all the msgs received into a msg queue
     */
    @Override  
    public void run() {  
    	try{
            while(true){  
            	Message msg = (Message) server.getInput().readObject();
            	System.out.println("receive " + msg);
            	controller.handleReceiveMessgae(msg);
            }    
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
    
    
}


