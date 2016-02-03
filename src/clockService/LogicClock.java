package clockService;

import config.Timestamp;

public class LogicClock implements Clock {
	private Integer counter;
	
	public LogicClock() {
		counter = 0;
	}
	
	@Override
	public Timestamp getTimestampSend() {
		return new Timestamp(++counter);
	}

	@Override
	public void setTimeReceive(Timestamp timeStamp) {
		int maxTime = Math.max(timeStamp.getLogicTime(), counter);
		counter = maxTime + 1;
	}

}
