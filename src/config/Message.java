package config;

import java.io.Serializable;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Header header;// there is no source in header 												???
	private Object payload;
	
	@Override
	public String toString() {
		return "Message " +  header.getSeq() +
			   " dest@" + header.getDest() + 
			   " source@" + header.getSource() +
			   " kind@" + this.getKind() + 
			   " data@" + payload;
	};
	
	public Message (String dest, String kind, Object payload) {
		this.header = new Header(dest, kind);
		this.payload = payload;
	}
	
	public Object getPayload() {
		return this.payload;
	}
	
	public String getSource() {
		return this.header.getSource();
	}
	
	public String getDest() {
		return this.header.getDest();
	}

	public void set_source(String source){
		this.header.setSource(source);;
	};
	
	public void set_seqNum(int sequenceNumber){
		this.header.setSeq(sequenceNumber);
	}
	
	public int get_seqNum() {
		return this.header.getSeq();
	}

	public String getKind() {
		return this.header.getKind();
	}
	
	public boolean isDuplicate() {
		return this.header.isDuplicate();
	}
	
	public void setDuplicate(boolean flag) {
		this.header.setDuplicate(flag);
	}
	
	private class Header implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String dest;
		private String source;
		private String kind;
		private int seq;
		private boolean duplicate = false;
		
		public Header(String dest, String kind) {
			this.setDest(dest);
			this.setKind(kind);
			this.setSeq(seq);
		}

		public String getDest() {
			return dest;
		}

		public void setDest(String dest) {
			this.dest = dest;
		}
		
		public String getSource() {
			return source;
		}
		
		public void setSource(String source) {
			this.source = source;
		}

		public String getKind() {
			return kind;
		}

		public void setKind(String kind) {
			this.kind = kind;
		}

		public int getSeq() {
			return seq;
		}

		public void setSeq(int seq) {
			this.seq = seq;
		}
		
		public boolean isDuplicate() {
			return this.duplicate;
		}
		
		public void setDuplicate(boolean flag) {
			this.duplicate = flag;
		}
	}
}
