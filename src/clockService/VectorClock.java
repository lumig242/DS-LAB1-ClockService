package clockService;

import config.Timestamp;

public class VectorClock implements Clock{
	private static Integer[] counter;  
	private static Integer currentID;
	
	public VectorClock(int size, int currentID) {
		counter = new Integer[size]; 
		for(int i = 0; i < size; i++){
			counter[i] = 0;
		}
		VectorClock.currentID = currentID;
	}
	
	@Override
	public Timestamp getTimestampSend() {
		counter[currentID]++;
		return new Timestamp(counter);
	}

	@Override
	public void setTimeReceive(Timestamp timeStamp) {
		counter[currentID]++;
		for(int i = 0; i < counter.length; i++){
			counter[i] = Math.max(counter[i], timeStamp.getVectorTime()[i]);
		}
	}

}
