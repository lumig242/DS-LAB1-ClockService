package config;

public class GroupMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupName;
	private String originSource;
	
	public String getOriginSource() {
		return originSource;
	}

	public void setOriginSource(String originSource) {
		this.originSource = originSource;
	}

	public GroupMessage(String groupName, String kind, Object payload) {
		super(null, kind, payload);
		this.groupName = groupName;
	}
	
	public GroupMessage(Message msg, String groupName) {
		super(msg);
		this.groupName = groupName;
	}
	
	public GroupMessage(GroupMessage gmsg) {
		super(gmsg);
		this.groupName = gmsg.getGroupName();
		this.originSource = gmsg.originSource;
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	@Override
	public String toString() {
		return "Message " +  super.get_seqNum() +
			   " group@" + groupName + 
			   " dest@" + super.getDest() + 
			   " source@" + super.getSource() +
			   " kind@" + this.getKind() +
			   " timestamp" + this.getTimestamp() +
			   " data@" + super.getPayload();
	}
	
	@Override public boolean equals(Object o) {
		if(!(o instanceof GroupMessage)){
			return false;
		}
		GroupMessage m = (GroupMessage) o;
		return (getDest().equals(m.getDest())) &&
				getKind().equals(m.getKind()) &&
				(get_seqNum() == m.get_seqNum()) &&
				getOriginSource().equals(m.getOriginSource());
	}
}
