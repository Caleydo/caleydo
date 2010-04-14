package daemon;

import java.util.Timer;

public class UserSelection {
	
	/** The application of the user selection. */
	private Application app; 
	
	/** The selection ID. */
	private String selectionID; 
	
	/** The associated user. */
	private String pointerID; 
	
	/** Whether the selection is the source selection of the user. */
	private boolean source; 
	
	/** The reported bounding box list. */
	private BoundingBoxList bbl; 
	
	/** Whether this selection has been reported to the target application. */
	private boolean reported; 
	
	/** Whether this selection has been reported for rendering. */
	private boolean rendered; 
	
	/** Handler for selections which have timed out (e.g. when application has been shut down). */
	private TimeoutHandler timeoutHandler; 
	
	public UserSelection(Application app, String selectionID, String pointerID, VisLinkManager manager){
		this.app = app; 
		this.selectionID = selectionID; 
		this.pointerID = pointerID; 
		this.reported = false; 
		this.rendered = false; 
		this.bbl = null; 
		this.source = false; 
		// schedule timer 
		TimeoutEvent timeoutEvent = new SelectionTimeoutEvent(this); 
		this.timeoutHandler = new TimeoutHandler(timeoutEvent, manager); 
		Timer timer = new Timer(); 
		timer.schedule(this.timeoutHandler, SelectionTimeoutEvent.SELECTION_TIME); 
	}
	
	public boolean isSource() {
		return source;
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	public Application getApplication(){
		return this.app; 
	}
	
	public String getSelectionID(){
		return this.selectionID; 
	}
	
	public String getPointerID(){
		return this.pointerID; 
	}
	
	public BoundingBoxList getBoundingBoxList(){
		return this.bbl; 
	}
	
	public void setBoundingBoxList(BoundingBoxList bbl){
		this.bbl = bbl; 
	}
	
	public boolean wasReported(){
		return this.reported; 
	}
	
	public boolean wasRendered(){
		return this.rendered; 
	}
	
	private void stopTimer(boolean canceled){
		if(this.timeoutHandler != null){
			// timer is still running and selection has been reported, 
			// not canceled
			if(this.timeoutHandler.cancel() && !canceled){
				this.app.reportResponsive(); 
			}
			this.timeoutHandler = null; 
		}
	}
	
	public void cancel(){
		this.stopTimer(true); 
	}
	
	public void setReported(){
		this.reported = true; 
		this.stopTimer(false); 
	}
	
	public void setRendered(){
		this.rendered = true; 
	}
	
	public String toString(){
		return "Selection app=" + this.app.getName() + ", pointerID=" + this.pointerID
		+ ", selectionID: "+ this.selectionID +", reported=" + this.reported + ", rendered=" + this.rendered; 
	}

}
