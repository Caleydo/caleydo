package daemon;

public class SelectionTimeoutEvent extends TimeoutEvent{
	
	public static final int SELECTION_TIME = 1500; 
	
	private UserSelection selection; 

	public SelectionTimeoutEvent(UserSelection selection) {
		super(TimeoutEventType.SELECTION_TIMEOUT);
		this.selection = selection; 
	}
	
	public UserSelection getSelection() {
		return selection;
	}
	
	

}
