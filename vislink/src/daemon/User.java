package daemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import VIS.AccessInformation;
import VIS.ApplicationAccessInfo;
import VIS.UserWindowAccess;
import VIS.VisualLinksRenderType;

/**
 * @author desko
 *
 */
public class User {
	
	/** Each user is uniquely associated with a pointer ID in Deskotheque. */
	private String pointerID;  
	
	/** The last selection ID the user has triggered. */
	private String prevSelectionID; 
	
	/** The application the user was recently interacting with. */
	private Application prevSrcApp; 
	
	/** The applications which have previously served as target for the user. */
	private List<Application> prevTargetApps;
	
	/** A list of access for all applications. */
	private HashMap<Application, UserWindowAccess> appAccess; 
	
	/** Describes how the visual links are currently rendered for the user. */
	private VisualLinksRenderType currentRenderType; 
	
	/** Timeout handler for user-specific timers. */
	private TimeoutHandler timeoutHandler; 
	
	public User(String pointerID){
		this.pointerID = pointerID; 
		this.prevSelectionID = ""; 
		this.prevSrcApp = null; 
		this.prevTargetApps = new ArrayList<Application>(); 
		this.appAccess = new HashMap<Application, UserWindowAccess>(); 
		this.currentRenderType = VisualLinksRenderType.RenderTypeNormal; 
		this.timeoutHandler = null; 
	}
	
	public TimeoutHandler getTimeoutHandler() {
		return timeoutHandler;
	}

	public void setTimeoutHandler(TimeoutHandler timeoutHandler) {
		this.timeoutHandler = timeoutHandler;
	}

	public VisualLinksRenderType getCurrentRenderType() {
		return currentRenderType;
	}

	public void setCurrentRenderType(VisualLinksRenderType currentRenderType) {
		this.currentRenderType = currentRenderType;
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
	
	public HashMap<Application, UserWindowAccess> getAppAccess(){
		return this.appAccess; 
	}
	
	public void addPrevTargetApp(Application targetApp){
		if(!this.hasApplication(targetApp)){
			this.prevTargetApps.add(targetApp); 
		}
	}
	
	public void removePrevTargetApp(Application targetApp){
		this.prevTargetApps.remove(targetApp); 
	}
	
	/**
	 * Sets the access properties of a certain application for this 
	 * user - no matter if the user actually has links to this application 
	 * or not. 
	 * @param app The application to be set. 
	 * @param access The access rights of the user for the application. 
	 */
	public void setAppAccess(Application app, UserWindowAccess access){
		System.out.println("\nSet access " + access + " for application " + app.getName() + " for user " + this.pointerID +"\n"); 
		this.appAccess.put(app, access); 
	}
	
	/**
	 * Sets the access properties according to the properties received by 
	 * Deskotheque via vis renderer. 
	 * @param appManager The application manager to retrieve the Applications by ID. 
	 * @param accessInfo The access information struct as delivered by Ice. 
	 */
	public void setAppAccess(ApplicationManager appManager, AccessInformation accessInfo){
		for(ApplicationAccessInfo appAccess : accessInfo.applications){
			int appID = appAccess.applicationID; 
			Application app = appManager.getApplicationsById().get(appID); 
			this.setAppAccess(app, appAccess.access); 
		}
	}
	
	public void removeApplication(Application app){
		// first check whether the user has links in the application
		if(this.hasApplication(app)){
			System.out.println("User " + this.pointerID + " has application " + app.getName()); 
			if(this.prevSrcApp == app){
				System.out.println("is source --> clear all"); 
				this.clearApplicationList(); 
			}
			else{
				System.out.println("is target"); 
				this.removePrevTargetApp(app); 
			}
		}
		// now delete the access restrictions for the user 
		for (Entry<Application, UserWindowAccess> e : this.appAccess.entrySet()) {
			if(e.getKey() == app){
				System.out.println("User "+this.pointerID+" has access "+e.getValue()+" for app "+app.getName()); 
				this.appAccess.remove(e); 
			}
		}
	}
	
	/**
	 * Creates a list of target applications (accessible or not) 
	 * by not considering the source application. 
	 * @param srcApp Application not to be included in the list. 
	 * @return A list of all applications except for srcApp. 
	 */
	public List<Application> getTargetApps(Application srcApp){
		List<Application> apps = new ArrayList<Application>(); 
		for (Entry<Application, UserWindowAccess> e : this.appAccess.entrySet()) {
			if(e.getKey() != srcApp){
				System.out.println("User "+this.pointerID+" has access "+e.getValue()+" for target app "+e.getKey().getName()); 
				apps.add(e.getKey()); 
			}
		}
		return apps; 
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
	 * Checks if the given application contains link elements by the user 
	 * and is accessible by the user. 
	 * @param app The application containing links by the user and being accessible. 
	 * @return Returns true if the given application is accessible. 
	 */
	public boolean hasApplicationAccessible(Application app){
		if(this.hasApplication(app)){
			System.out.println(this.toString() + " has app " + app.getName()); 
			if(this.appAccess.get(app) == UserWindowAccess.Accessible){
				System.out.println("(app is accessible)"); 
				return true; 
			}
		}
		return false; 
	}
	
	public boolean isApplicationAccessible(Application app){
		if(this.appAccess.get(app) == UserWindowAccess.Accessible){
			System.out.println("App " + app.getName() + " is accessible for user " + this.pointerID); 
			return true; 
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
	
	public String toString(){
		return "User " + this.pointerID; 
	}

}
