package daemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectionManager {
	
	private List<UserSelection> selections; 
	
	public SelectionManager(){
		this.selections = new ArrayList<UserSelection>(); 
		this.selections.clear(); 
	}
	
	/// GENERAL ACCESS 
	
//	public void addSelection(Application app, String selectionID, String pointerID){
//		this.addSelection(app, selectionID, pointerID, false); 
//	}
	
	public void addSelection(Application app, String selectionID, String pointerID, boolean source){
		UserSelection selection = new UserSelection(app, selectionID, pointerID); 
		selection.setSource(source); 
		UserSelection existingSelection = this.getSelection(app, pointerID); 
		if(existingSelection != null){
			System.out.println("Existing selection: " + existingSelection); 
			//existingSelection.setReported(); 
			this.selections.remove(existingSelection); 
		}
		this.selections.add(selection); 
		System.out.println("Added new user selection: " + selection.toString()); 
	}
	
	/// ACCESS BY APPLICATION 
	
	public UserSelection getUnreportedSelection(String appName){
		for ( UserSelection selection : selections ){
			if(!selection.wasReported()){
				//System.out.println("Not reported"); 
				if(selection.getApplication().getName().equals(appName)){
					System.out.println("Unreported selection " + selection.toString()); 
					return selection; 
				}
			}
		}
		return null; 
	}
	
	public void clearUnreportedSelections(Application app){
		for ( UserSelection selection : selections ){
			if(!selection.wasReported()){
				if(selection.getApplication() == app){
					System.out.println("Clearing unreported selection "+selection.toString()); 
					this.selections.remove(selection); 
				}
			}
		}
	}
	
	public String getSelectionIDFilter(String appName){
		UserSelection selection = this.getUnreportedSelection(appName); 
		if(selection == null){
			return null; 
		}
		System.out.println("Sending unreported selection ID "+selection.getSelectionID() + " to app " + appName); 
		return selection.getSelectionID(); 
	}
	
	public String getPointerIDFilter(String appName){
		UserSelection selection = this.getUnreportedSelection(appName); 
		if(selection == null){
			return null; 
		}
		System.out.println("Sending unreported pointer ID "+selection.getPointerID() + " to app " + appName); 
		return selection.getPointerID(); 
	}
	
	public void setReported(String appName){
		UserSelection selection = this.getUnreportedSelection(appName); 
		if(selection != null){
			System.out.println("Setting selection " + selection.toString() + " to reported"); 
			selection.setReported(); 
		}
	}
	
	/// ACCESS BY USER 
	
	public List<UserSelection> getSelections(String pointerID){
		List<UserSelection> userSelections = new ArrayList<UserSelection>(); 
		for( UserSelection selection : this.selections){
			if(!selection.wasRendered()){
				if(selection.getPointerID().equals(pointerID)){
					userSelections.add(selection); 
				}
			}
		}
		System.out.println(userSelections.size() + " selections for user " + pointerID); 
		return userSelections; 
	}
	
	public UserSelection getSelection(Application app, String pointerID){
		for(UserSelection selection : this.selections){
			if(app == selection.getApplication() && pointerID.equals(selection.getPointerID())){
				return selection; 
			}
		}
		return null; 
	}
	
	public void clearUserSelections(String pointerID){
		List<UserSelection> userSelections = this.getSelections(pointerID); 
		for(UserSelection selection : userSelections){
			//if(!selection.wasRendered()){
				if(selection.getPointerID().equals(pointerID)){
					this.selections.remove(selection); 
				}
			//}
		}
		System.out.println("Removed " + userSelections.size() + " selections for user " + pointerID); 
	}
	
	public int getNumUserSelections(String pointerID){
		return this.getSelections(pointerID).size(); 
	}
	
	public int getNumMissingReports(String pointerID){
		List<UserSelection> userSelections = this.getSelections(pointerID); 
		int missing = 0; 
		for(UserSelection selection : userSelections){
			if(!selection.wasReported()){
				missing++; 
			}
		}
		return missing; 
	}
	
	public HashMap<Integer, BoundingBoxList> getBoundingBoxList(String pointerID){
		List<UserSelection> userSelections = this.getSelections(pointerID); 
		HashMap<Integer, BoundingBoxList> app2bbl = new HashMap<Integer, BoundingBoxList>();
		for(UserSelection selection : userSelections){
			app2bbl.put(selection.getApplication().getId(), selection.getBoundingBoxList());
		}
		return app2bbl; 
	}

}
