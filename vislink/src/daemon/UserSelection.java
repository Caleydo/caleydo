package daemon;

public class UserSelection {
	
	/** The application of the user selection. */
	private Application app; 
	
	/** The selection ID. */
	private String selectionID; 
	
	/** The associated user. */
	private String pointerID; 
	
	/** The reported bounding box list. */
	private BoundingBoxList bbl; 
	
	/** Whether this selection has been reported to the target application. */
	private boolean reported; 
	
	/** Whether this selection has been reported for rendering. */
	private boolean rendered; 
	
	public UserSelection(Application app, String selectionID, String pointerID){
		this.app = app; 
		this.selectionID = selectionID; 
		this.pointerID = pointerID; 
		this.reported = false; 
		this.rendered = false; 
		this.bbl = null; 
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
	
	public void setReported(){
		this.reported = true; 
	}
	
	public void setRendered(){
		this.rendered = true; 
	}
	
	public String toString(){
		return "Selection app=" + this.app.getName() + ", pointerID=" + this.pointerID
		+ ", selectionID: "+ this.selectionID +", reported=" + this.reported + ", rendered=" + this.rendered; 
	}

}
