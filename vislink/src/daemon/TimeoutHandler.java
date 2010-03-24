package daemon;

import java.util.TimerTask;


public class TimeoutHandler extends TimerTask {
	
	private TimeoutEvent event;
	
	private VisLinkManager manager; 

	public TimeoutHandler(TimeoutEvent event, VisLinkManager manager) {
		super();
		this.event = event;
		this.manager = manager; 
	}
	
	public void handleOneShotEvent(OneShotTimeoutEvent event){
		System.out.println("One-shot timeout event for user " + event.getUser().getPointerID()); 
		this.manager.releaseOneShot(event.getUser()); 
	}

	@Override
	public void run() {
		if(this.event != null){
			switch(this.event.getEventType()){
			case ONE_SHOT_TIMEOUT: 
				this.handleOneShotEvent((OneShotTimeoutEvent)event); 
				break; 
			default:
				System.out.println("Unsupported timeout event type " + event.getEventType()); 
			}
		}
		else{
			System.out.println("TimeoutEvent is null"); 
		}
		
	} 
	
	

}
