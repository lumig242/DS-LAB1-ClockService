package clockService;

import java.util.ArrayList;
import java.util.Arrays;

import config.Timestamp;

public class VectorClock implements Clock {
	private Integer[] counter;
	private Integer currentID;

	public VectorClock(int size, int currentID) {
		counter = new Integer[size];
		for (int i = 0; i < size; i++) {
			counter[i] = 0;
		}
		this.currentID = currentID;
	}

	
	@Override
	public Timestamp getTimestampSend() {
		increment();
		return new Timestamp(counter.clone());
	}

	@Override
	public void setTimeReceive(Timestamp timeStamp) {
		counter[currentID] = counter[currentID] + 1;
		for (int i = 0; i < counter.length; i++) {
			counter[i] = Math.max(counter[i], timeStamp.getVectorTime()[i]);
		}
	}
	
	@Override
	public void increment() {
		counter[currentID] = counter[currentID] + 1;
	}
	
	/**
	 * Increment the numebr at position j
	 * @param j
	 */
	public void incrementAt(int j){
		counter[j]++;
	}
	
	public Integer[] getTimeStamp(){
		return counter.clone();
	}

	@Override public String toString() {
		return Arrays.toString(counter);
	};
	
	@Override
	public void printTimestamp() {
		System.out.println(Arrays.toString(counter));
		
	}
	
}
