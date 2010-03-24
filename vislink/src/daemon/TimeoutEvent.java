package daemon;

public class TimeoutEvent {
	
	public enum TimeoutEventType{ ONE_SHOT_TIMEOUT }; 
	
	private TimeoutEventType eventType;

	public TimeoutEvent(TimeoutEventType eventType) {
		this.eventType = eventType;
	}

	public TimeoutEventType getEventType() {
		return eventType;
	} 

}
