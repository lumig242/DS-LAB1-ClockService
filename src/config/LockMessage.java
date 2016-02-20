package config;

public class LockMessage extends GroupMessage implements Comparable{

	public enum LOCKTYPE{REQUEST, RELEASE, REPLY}

	private LOCKTYPE type;
	
	public LockMessage(String groupName, String kind, Object payload, LOCKTYPE t) {
		super(groupName, kind, payload);
		this.type = t;
	}
	
	public LockMessage(LockMessage lmsg) {
		super(lmsg);
		this.type = lmsg.type;
	}

	/**
	 * First compare the logical timestamp,
	 * otherwise compare the string identifier
	 */
	@Override
	public int compareTo(Object o) {
		LockMessage lmsg = (LockMessage) o;
		if(this.getTimestamp().getLogicTime().equals(lmsg.getTimestamp().getLogicTime())){
			return this.getTimestamp().getLogicTime().compareTo(lmsg.getTimestamp().getLogicTime());
		}
		return this.getOriginSource().compareTo(lmsg.getOriginSource());
	}

	public LOCKTYPE getLocktype(){
		return type;
	}
}
