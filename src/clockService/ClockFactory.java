package clockService;

public class ClockFactory {
	public static Clock getClockInstance(String clockType){
		if(clockType.equalsIgnoreCase("logic")){
			return new LogicClock();
		}
		return null;
	}
	
	public static Clock getClockInstance(String clockType, int size, int currentID){
		if(clockType.equalsIgnoreCase("vector")){
			return new VectorClock(size, currentID);
		}
		return null;
	}
}
