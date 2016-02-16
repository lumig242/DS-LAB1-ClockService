package config;

public class GroupMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupName;
	
	public GroupMessage(String groupName, String kind, Object payload) {
		super(null, kind, payload);
		this.groupName = groupName;
	}
	
	public GroupMessage(Message msg, String groupName) {
		super(msg);
		this.groupName = groupName;
	}
	
	public GroupMessage(GroupMessage gmsg) {
		super(gmsg.getDest(), gmsg.getKind(), gmsg.getPayload());
		this.groupName = gmsg.getGroupName();
	}
	
	public String getGroupName() {
		return this.groupName;
	}

}
