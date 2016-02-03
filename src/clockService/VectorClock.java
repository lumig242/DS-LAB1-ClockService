package clockService;

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
		System.out.println(Arrays.toString(counter) + "   getsend");
		counter[currentID] = counter[currentID] + 1;
		return new Timestamp(counter.clone());
	}

	@Override
	public void setTimeReceive(Timestamp timeStamp) {
		counter[currentID] = counter[currentID] + 1;
		for (int i = 0; i < counter.length; i++) {
			counter[i] = Math.max(counter[i], timeStamp.getVectorTime()[i]);
		}
	}

}
