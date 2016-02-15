package clockService;

import config.Timestamp;

public interface Clock {
	/**
	 * Get the current timestamp
	 * @return timestamp
	 */
	public Timestamp getTimestampSend();
	/**
	 * Update the time in clock service when receive a timestamp
	 * @param timeStamp
	 */
	public void setTimeReceive(Timestamp timeStamp);
	
	public void increment();
}
