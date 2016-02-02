package core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import config.ConfigParser;
import config.Message;
import config.Server;

/**
 * The consumer thread. It keeps fetching msgs stored in the send queue.
 * @author LumiG
 *
 */
public class SendConsumerThread implements Runnable {
	LinkedBlockingQueue<Message> sendMsgs = new LinkedBlockingQueue<Message>();
	ConfigParser config;
	
	public SendConsumerThread(LinkedBlockingQueue<Message> sendMsgs, ConfigParser config) {
		this.sendMsgs = sendMsgs;
		this.config = config;
	}
	
	@Override
	public void run() {
		while(true){
			// Sleep for seconds if no msgs in the queue
			if(sendMsgs.isEmpty()){
				try {
					Thread.sleep(200);
					continue;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Message msg = sendMsgs.poll();
			Server destServer = config.getServer(msg.getDest());
			ObjectOutputStream outputStream = destServer.getOutput();
			try {
				//send
				outputStream.writeObject(msg);
				outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
}
