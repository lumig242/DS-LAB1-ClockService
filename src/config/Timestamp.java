package config;

public class Timestamp implements Comparable<Timestamp>{
	private Integer[] vecCounter;
	private Integer intCounter;
	private String type;
	
	public Timestamp(Integer counter) {
		intCounter = counter;
		type = "logic";
	}
	
	public Timestamp(Integer[] counter) {
		vecCounter = counter;
		type = "vector";
	}

	public Integer getLogicTime(){
		return intCounter;
	}
	
	public Integer[] getVectorTime(){
		return vecCounter;
	}
	
	@Override
	public int compareTo(Timestamp o) {
		if (type.equals("logic")){
			return this.getLogicTime().compareTo(o.getLogicTime());
		}else{
			// TODO implement the compare for vector timestamp
			// return this.getVectorTime().compareTo(o.getVectorTime());
			return 0;
		}
	}
	
}
