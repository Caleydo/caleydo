package daemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import Ice.Current;
import VIS.InteractionEvent;
import VIS.MouseOverCollaboratorSelectionEvent;
import VIS.OneShotRequestEvent;
import VIS.UserWindowAccess;
import VIS.VisManagerIPrx;
import VIS.VisualLinksRenderType;
import VIS.WindowLockEvent;
import VIS._VisManagerIDisp;

public class VisLinkManagerIceInterface extends _VisManagerIDisp{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	VisLinkManager manager; 
	
	VisManagerIPrx appPrx; 

	public VisLinkManagerIceInterface(VisLinkManager manager) {
		this.manager = manager;
	}
	
	public VisManagerIPrx getProxy() {
		return appPrx;
	}


	public void setProxy(VisManagerIPrx appPrx) {
		this.appPrx = appPrx;
	}


	public void reportMouseOverCollaboratorSelectionEvent(MouseOverCollaboratorSelectionEvent event){
		System.out.println("Receiving mouse over collaborator selection event for owner " + event.ownerPointerId); 
		
		// get involved users 
		UserManager userManager = manager.getUserManager(); 
		User user = userManager.getUser(event.pointerId); 
		User owner = userManager.getUser(event.ownerPointerId); 
		
		// report one-shot request to visual links manager 
		manager.reportOneShot(user, owner, event.pointerAccessInformation, event.srcApp); 
	}
	
	public void reportOneShotRequestEvent(OneShotRequestEvent event){
		System.out.println("Receiving one shot request event from pointer"+event.pointerId); 
		
		// get user
		UserManager userManager = manager.getUserManager(); 
		User user = userManager.getUser(event.pointerId); 

		if(user.getCurrentRenderType() != VisualLinksRenderType.RenderTypeOneShot){


			// get selection string
			ClipboardManager clipboard = manager.getClipboardManager(); 
			String selectionID = clipboard.getSelection(); 
			System.out.println("Selection: "+selectionID); 
			System.out.println("Access information: "+event.pointerAccessInformation); 

			// only proceed if a selection could be retrieved
			if(!selectionID.isEmpty()){
				int numChars = selectionID.length(); 
				final int charWidth = 15; 
				final int charHeight = 20; 
				int height = charHeight; 
				int width = numChars * charWidth; 
				System.out.println("Num chars: "+numChars+", width: "+width+", height:"+height); 
				// construct container bounding box 
				BoundingBox bb = new BoundingBox(); 
				bb.setX(event.pointerX - (width / 2)); 
				bb.setY(event.pointerY - (height / 2)); 
				bb.setWidth(width); 
				bb.setHeight(height); 
				bb.setSource(true); 
				// construct temporary container / application name
				String appName = "temp-"+event.pointerId; 
				// (re-)register container application
				manager.registerApplication(appName, bb); 
				// retrieve application 
				ApplicationManager appManager = manager.getApplicationManager(); 
				Application app = appManager.getApplications().get(appName); 
				if(app == null){
					System.out.println("Application could not be created"); 
					return; 
				}
				app.setTemporary(true); 
				System.out.println("Application "+appName+" has id "+app.getId()); 
				// create selection for the user 
				SelectionManager selectionManager = manager.getSelectionManager(); 
				selectionManager.addSelection(app, selectionID, event.pointerId, true); 
				// retrieve selection and set reported 
				UserSelection selection = selectionManager.getSelection(app, event.pointerId); 
				if(selection == null){
					System.out.println("Selection for app "+app.getName()+" - user "+event.pointerId+" could not be found"); 
					return; 
				}
				selection.setReported(); 
				// create region bounding box list and save with selection
				BoundingBoxList bbl = new BoundingBoxList(); 
				BoundingBox regionBB = new BoundingBox(); 
				regionBB.setX(event.pointerX); 
				regionBB.setY(event.pointerY); 
				regionBB.setHeight(0); 
				regionBB.setWidth(0); 
				regionBB.setSource(true); 
				bbl.add(regionBB); 
				bbl.list.get(0).setSource(true); 
				selection.setBoundingBoxList(bbl); 
				// report one-shot request to visual links manager
				manager.reportOneShot(user, selectionID, event.pointerAccessInformation, -1, OneShotTimeoutEvent.ONE_SHOT_LONG_DISPLAY_TIME); 
			}
			else{
				System.out.println("Selection is empty"); 
			}
		}
		else{
			System.out.println("User is currently having one-shot links --> ignoring"); 
		}
		
		// report one-shot request to visual links manager
		//manager.reportOneShot(user, event.pointerAccessInformation); 
	}
	
	public void reportWindowLockEvent(WindowLockEvent event){
		System.out.println("Receiving window lock event from pointer"+event.pointerId); 
		
		// get user
		UserManager userManager = manager.getUserManager(); 
		User user = userManager.getUser(event.pointerId); 
		
		// get application 
		ApplicationManager appManager = manager.getApplicationManager(); 
		Application app = appManager.getApplicationsById().get(event.appID); 
		
		if(app != null){
			
			System.out.println("App "+app.getName()+" is locked" + event.locked); 
			
			// window access to be set 
			// TODO: this is not correct, if the window is unaccessible 
			// due to other reasons (e.g. private display) 
			UserWindowAccess userAccess = UserWindowAccess.Accessible; 
			if(event.locked){
				userAccess = UserWindowAccess.NotAccessible; 
			}
			
			// construct list of affected users 
			List<User> affectedUsers = new ArrayList<User>(); 
			
			// set app inaccessible for all other users 
			HashMap<String, User> users = userManager.getUsers(); 
			for(Entry<String, User> userEntry : users.entrySet()){
				User otherUser = userEntry.getValue(); 
				if(otherUser != user){
					UserWindowAccess prevAccess = otherUser.getWindowAccess(app); 
					otherUser.setAppAccess(app, userAccess); 
					if(prevAccess != userAccess){
						System.out.println("User's "+user.getPointerID()+" access has changed to "+userAccess); 
						affectedUsers.add(otherUser); 
					}
				}
			}
			
			if(affectedUsers.size() > 0){
				// trigger window change report
				this.manager.reportAccessChange(affectedUsers); 
			}
		}
		else{
			System.out.println("Application with ID "+event.appID+" not found"); 
		}
	}


	public void reportEvent(InteractionEvent event, Current current) {
		System.out.println("Receiving interaction event for pointer " + event.pointerId); 
		switch(event.eventType){
		case MouseOverCollaboratorSelection: 
			this.reportMouseOverCollaboratorSelectionEvent((MouseOverCollaboratorSelectionEvent)event); 
			break; 
		case OneShotRequest:
			this.reportOneShotRequestEvent((OneShotRequestEvent)event); 
			break; 
		case WindowLock:
			this.reportWindowLockEvent((WindowLockEvent)event); 
			break; 
		default:
			break; 
		}
	}

}
