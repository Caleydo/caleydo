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
	
//	public TimeoutHandler(TimeoutEvent event){
//		super(); 
//		this.event = event; 
//	}
	
	public void handleSelectionTimeout(SelectionTimeoutEvent event){
		System.out.println("Selection timeout for selection "+ event.getSelection().toString());
		if(!event.getSelection().wasReported()){
			System.out.println("SELECTION WAS NOT REPORTED YET!"); 
			event.getSelection().setReported(); 
			// empty bounding box list 
			BoundingBoxList bbl = new BoundingBoxList(); 
			event.getSelection().setBoundingBoxList(bbl); 
			// report application unresponsive
			event.getSelection().getApplication().reportNonResponsive(); 
			if(this.manager != null){
				this.manager.checkRender(event.getSelection().getPointerID()); 
			}
		}
	}
	
	public void handleOneShotEvent(OneShotTimeoutEvent event){
		System.out.println("One-shot timeout event for user " + event.getUser().getPointerID()); 
		if(this.manager != null){
			this.manager.releaseOneShot(event.getUser());
		}
	}

	@Override
	public void run() {
		if(this.event != null){
			switch(this.event.getEventType()){
			case ONE_SHOT_TIMEOUT: 
				this.handleOneShotEvent((OneShotTimeoutEvent)event); 
				break; 
			case SELECTION_TIMEOUT:
				this.handleSelectionTimeout((SelectionTimeoutEvent)event); 
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
