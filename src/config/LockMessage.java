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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
