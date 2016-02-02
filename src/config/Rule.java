package config;

public class Rule{
	private String action, src, dest, kind;
	private int seqNum = -1;
	
	@Override
	public String toString() {
		return "Rules: action@" + getAction() + 
				"  src@" + getSrc() + 
				"  dest@" + getDest() + 
				"  kind@" + getKind() + 
				"  seqNum@" + getSeqNum();
	};
	
	/**
	 * Judge if a rule matches the current message
	 * @param src
	 * @param dest
	 * @param kind
	 * @param seqNum
	 * @return
	 */
	public Boolean match(String src, String dest, String kind, int seqNum){
		switch(action.toLowerCase()) {
			case "drop" :
			{
				return (this.src == null || this.src.equals(src)) &&
						(this.dest == null || this.dest.equals(dest)) &&
						(this.kind == null || this.kind.equals(kind)) &&
						(this.seqNum == -1 || this.seqNum == seqNum);
			}
			case "dropafter" :
			{
				return (this.src == null || this.src.equals(src)) &&
						(this.dest == null || this.dest.equals(dest)) &&
						(this.kind == null || this.kind.equals(kind)) &&
						(this.seqNum == -1 || this.seqNum < seqNum);
			}
			case "delay" :
			{
				return (this.src == null || this.src.equals(src)) &&
						(this.dest == null || this.dest.equals(dest)) &&
						(this.kind == null || this.kind.equals(kind)) &&
						(this.seqNum == -1 || this.seqNum == seqNum);
			}
		}
		
		return false;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public int getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
}