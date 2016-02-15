package config;

public class GroupMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupName;
	private String groupID;
	
	public GroupMessage(String dest, String kind, Object payload, String groupName, String groupID) {
		super(dest, kind, payload);
		this.groupName = groupName;
		this.groupID = groupID;
	}
	
	public GroupMessage(Message msg, String groupName, String groupID) {
		super(msg);
		this.groupName = groupName;
		this.groupID = groupID;
	}
	
	public GroupMessage(GroupMessage gmsg) {
		super(gmsg.getDest(), gmsg.getKind(), gmsg.getPayload());
		this.groupID = gmsg.getGroupID();
		this.groupName = gmsg.getGroupName();
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	public String getGroupID() {
		return this.groupID;
	}

}
