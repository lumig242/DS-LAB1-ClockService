package config;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The class for timestamp. To be contained as a field in each message
 * Note: This class should only be Initialized by the clock service Instances
 *       Both types of clock service are stored in a timestamp. Only clock service
 *       instance take care to manage this.
 */
public class Timestamp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer[] vecCounter;
	private Integer intCounter;
	private String type;
	
	public enum comp{
		LESS, GREATER, PARALLEL, EQUAL
	}
	
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
	
	/**
	 * compare two timestamps
	 * @param o
	 * @return enum comp, four types in all
	 * @throws Exception 
	 */
	public comp compareTo(Timestamp o){
		if (type.equals("logic")){
			if(intCounter < o.intCounter)	return comp.LESS;
			if(intCounter > o.intCounter)	return comp.GREATER;
			return comp.EQUAL;
		}
		// TODO implement the compare for vector timestamp
		Boolean equal = true, lessThanAndEqual = true, greaterThanAndEqual = true;
		for(int i = 0; i < vecCounter.length; i++){
			if(vecCounter[i] != o.vecCounter[i]){
				equal = false;
			}
			if(vecCounter[i] > o.vecCounter[i]){
				lessThanAndEqual = false;
			}
			if(vecCounter[i] < o.vecCounter[i]){
				greaterThanAndEqual = false;
			}
		}
		
		if(equal.equals(true)){
			return comp.EQUAL;
		}
		// Timestamps are not equal from follows
		if(lessThanAndEqual.equals(true)){
			return comp.LESS;
		}
		if(greaterThanAndEqual.equals(true)){
			return comp.GREATER;
		}
		return comp.PARALLEL;
	}
}
