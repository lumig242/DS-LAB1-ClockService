package application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import config.Message;
import config.Timestamp;

public class Logger {
	private String fileName;
	private static int seqNum = 0;
	private static int parallelSeq = 0;
	private Message lastMsg = null;
	
	private List<Message> receiveMsgs = new ArrayList<Message>();
	
	public Logger(String fileName){
		this.fileName = fileName;
		
	}
	
	/**
	 * The apis to insert a new message to the logger
	 * Using insert sorting here
	 * @param msg
	 */
	public void recordOneMessage(Message msg){
		if(receiveMsgs.isEmpty()){
			receiveMsgs.add(msg);
		}else{
			int index = 0;
			while(index < receiveMsgs.size()){
				// If not smaller, equal or parallel
				// Insert
				if(!receiveMsgs.get(index).getTimestamp().compareTo(msg.getTimestamp()).equals(Timestamp.comp.LESS)){
					receiveMsgs.add(index, msg);
					return;
				}
				index ++ ;
			}
			receiveMsgs.add(msg);
		}
	}
	
	public void writeAllLogs(){
		try{
			FileWriter file = new FileWriter(fileName, true);
			PrintWriter fileout = new PrintWriter(new BufferedWriter(file));
			System.out.println(receiveMsgs);
			for(Message msg: receiveMsgs){
				if(lastMsg != null){
					Timestamp.comp compType = msg.getTimestamp().compareTo(lastMsg.getTimestamp());
					if(compType.equals(Timestamp.comp.PARALLEL)){
						parallelSeq++;
					}else if(compType.equals(Timestamp.comp.GREATER)){
						parallelSeq = 0;
						seqNum++;
					}
				}
				String outLogs = constructSeq(seqNum, parallelSeq) + msg;
				fileout.println(outLogs);
				System.out.println(outLogs);
				lastMsg = msg;
			}
			file.close();
		    receiveMsgs.clear();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	private String constructSeq(int _seqNum, int _parallelSeq){
		if(_parallelSeq == 0) return "Seq ["  + _seqNum + "] ";
		return "Seq ["  + _seqNum + "." + _parallelSeq + "] ";
	}
}
