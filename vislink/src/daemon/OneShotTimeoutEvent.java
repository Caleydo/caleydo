package daemon;

public class OneShotTimeoutEvent extends TimeoutEvent {
	
	public static final int ONE_SHOT_DISPLAY_TIME = 1000; 
	public static final int ONE_SHOT_LONG_DISPLAY_TIME = 3000; 
	
	private User user; 

	public OneShotTimeoutEvent(User user) {
		super(TimeoutEventType.ONE_SHOT_TIMEOUT);
		this.user = user; 
	}

	public User getUser() {
		return user;
	}

}
