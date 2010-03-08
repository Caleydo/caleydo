package daemon;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	/** Each user is uniquely associated with a pointer ID in Deskotheque. */
	private String pointerID;  
	
	/** The last selection ID the user has triggered. */
	private String prevSelectionID; 
	
	/** The application the user was recently interacting with. */
	private Application prevSrcApp; 
	
	/** The applications which have previously served as target for the user. */
	private List<Application> prevTargetApps; 
	
	public User(String pointerID){
		this.pointerID = pointerID; 
		this.prevSelectionID = ""; 
		this.prevSrcApp = null; 
		this.prevTargetApps = new ArrayList<Application>(); 
	}
	
	public String getPointerID(){
		return this.pointerID; 
	}
	
	public String getPrevSelectionID(){
		return this.prevSelectionID; 
	}
	
	public void setPrevSelectionID(String selectionID){
		this.prevSelectionID = selectionID; 
	}
	
	public Application getPrevSrcApp(){
		return this.prevSrcApp; 
	}
	
	public void setPrevSrcApp(Application srcApp){
		this.prevSrcApp = srcApp; 
	}
	
	public List<Application> getPrevTargetApps(){
		return this.prevTargetApps; 
	}
	
	public void addPrevTargetApp(Application targetApp){
		if(!this.hasApplication(targetApp)){
			this.prevTargetApps.add(targetApp); 
		}
	}
	
	/**
	 * Returns a list of all applications (source and target apps) where the 
	 * user has visual links in. 
	 * @return A list of all applications associated with the user. 
	 */
	public List<Application> getAllPrevApps(){
		List<Application> apps = new ArrayList<Application>(); 
		apps.add(this.prevSrcApp); 
		apps.addAll(this.prevTargetApps); 
		return apps; 
	}
	
	public void clearApplicationList(){
		this.prevSrcApp = null; 
		this.prevTargetApps.clear(); 
	}
	
	/**
	 * Wraps a few methods to be called when a new visual link request has been reported 
	 * and the user has been identified by Deskotheque: 
	 * Sets the source application and the selection ID and clears previous items. 
	 * @param selectionID The selection ID. 
	 * @param srcApp The reporting source application. 
	 */
	public void setNewSelection(String selectionID, Application srcApp){
		this.clearApplicationList(); 
		this.setPrevSelectionID(selectionID); 
		this.setPrevSrcApp(srcApp); 
	}
	
	/**
	 * Checks if the given application contains link elements by the user. 
	 * Example: map is scrolled --> user has links to the map --> links need 
	 * to be updated. 
	 * @param app The application to be checked. 
	 * @return Returns true if the given application is either the source application 
	 * or a target application. 
	 */
	public boolean hasApplication(Application app){
		if(this.prevSrcApp == app) return true; 
		for(int i = 0; i < this.prevTargetApps.size(); i++){
			if(this.prevTargetApps.get(i) == app){
				return true; 
			}
		}
		return false; 
	}
	
	/**
	 * Checks if the user has ever contributed to the visual links application. 
	 * Example: user scrolls the map --> map registers window movement --> Deskotheque 
	 * reports pointer --> pointer has no active links. 
	 * @return Returns true if there is a selectionID associated with the user. 
	 */
	public boolean isActive(){
		if(this.prevSelectionID.isEmpty()){
			return false; 
		}
		return true; 
	}

}
