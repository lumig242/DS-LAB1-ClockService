package clockService;

import config.Timestamp;

public interface Clock {
	public Timestamp getTimestampSend();
	public void setTimeReceive(Timestamp timeStamp);
}
