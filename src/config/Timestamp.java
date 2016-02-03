package config;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The class for timestamp. To be contained as a field in each message
 * Note: This class should only be Initialized by the clock service Instances
 *       Both types of clock service are stored in a timestamp. Only clock service
 *       instance take care to manage this.
 */
public class Timestamp implements Comparable<Timestamp>, Serializable{
	private Integer[] vecCounter;
	private Integer intCounter;
	private String type;
	
	public Timestamp(Integer counter) {
		intCounter = counter;
		type = "logic";
	}
	
	public Timestamp(Integer[] counter) {
		vecCounter = counter;
		type = "vector";
	}

	public Integer getLogicTime(){
		return intCounter;
	}
	
	public Integer[] getVectorTime(){
		return vecCounter;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		if(type.equals("logic")){
			return "timestamp@" + intCounter;
		}else if(type.equals("vector")){
			return "timestamp@" + Arrays.toString(vecCounter);
		}
		return "";
	}
	
	@Override
	public int compareTo(Timestamp o) {
		/*
		 *  It's not good to override as a comparable here.
		 *  Compare between timestamps doesn't follow the rule.
		 * 
		if (type.equals("logic")){
			return this.getLogicTime().compareTo(o.getLogicTime());
		}else{
			// TODO implement the compare for vector timestamp
			// return this.getVectorTime().compareTo(o.getVectorTime());
			return 0;
		}
		*/
		return 0;
	}
	
	/**
	 * TODO: implement some kind of compare function. Not decided yet.
	 */
	public static int compare(Timestamp o1, Timestamp o2){
		return 0;
	}
	
}
