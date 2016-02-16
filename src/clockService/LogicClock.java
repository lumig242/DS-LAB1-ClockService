package clockService;

import config.Timestamp;

public class LogicClock implements Clock {
	private Integer counter;
	
	public LogicClock() {
		counter = 0;
	}
	
	public void debugPrintClock(){
		System.out.println(counter);
	}
	
	@Override
	public Timestamp getTimestampSend() {
		System.out.println("COUNTER SEND" + counter);
		increment();
		return new Timestamp(counter);
	}

	@Override
	public void setTimeReceive(Timestamp timeStamp) {
		System.out.println("COUNTER R" + counter);
		int maxTime = Math.max(timeStamp.getLogicTime(), counter);
		counter = maxTime + 1;
	}

	@Override
	public void increment() {
		counter++;
	}

	@Override
	public void printTimestamp() {
		System.out.println(counter);
		
	}
	
	


}
