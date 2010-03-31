package daemon;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import Ice.Communicator;
import Ice.ObjectAdapter;
import VIS.AccessInformation;
import VIS.ApplicationAccessInfo;
import VIS.Color4f;
import VIS.Selection;
import VIS.SelectionContainer;
import VIS.SelectionGroup;
import VIS.SelectionReport;
import VIS.VisManagerIPrxHelper;
import VIS.VisRendererIPrx;
import VIS.VisRendererIPrxHelper;
import VIS.VisualLinksRenderType;
import VIS.adapterName;
import VIS.adapterPort;

public class VisLinkManager implements InitializingBean, DisposableBean {
	
	ApplicationManager applicationManager;
	
	UserManager userManager; 
	
	SelectionManager selectionManager; 
	
	ClipboardManager clipboardManager; 

	JAXBContext jaxbContext; 
	
	/** Ice communication object. */
	private Communicator communicator;
	
	/** Proxy object of VisRenderer for remote method invocation. */
	private VisRendererIPrx rendererPrx;
	
	/** Proxy object for VisRenderer to call VisLinkManager. */
	private VisLinkManagerIceInterface iceInterface; 
	
	
	public VisLinkManager() {
		this.clipboardManager = new ClipboardManager(); 
	}
	
	public void reportWindowChange(String appName) {
		System.out.println("VisLinkManager: reportWindowChange, appName=" + appName);
		
		Application app = applicationManager.getApplications().get(appName);

		
		// find the mouse pointer that has triggered the window change 
		AccessInformation accessInformation = rendererPrx.getAccessInformation(app.getId());
		ApplicationAccessInfo[] targetApplicationIds = accessInformation.applications;
		String pointerID = accessInformation.pointerId;
		
		// check out whether we need to redraw links for that user 
		User user = this.userManager.getUser(pointerID); 
		// save the access information for the user 
		user.setAppAccess(this.applicationManager, accessInformation); 
		
		if(user.isActive()){
			String selectionId = user.getPrevSelectionID(); 
			System.out.println("User changing window content (" + pointerID 
					+ ") had previous selection id " + selectionId); 
			// new settings for the active user 
			user.setNewSelection(selectionId, app); 
			
			// request visual links for source window 
			this.selectionManager.addSelection(app, selectionId, pointerID, true); 
			
			// request visual links for target window 
			for (ApplicationAccessInfo appId : targetApplicationIds) {
				Application currentApp = applicationManager.getApplicationsById().get(appId.applicationID);
				this.selectionManager.addSelection(currentApp, selectionId, pointerID, false); 
			}
			
			checkRender(pointerID);
		}
		
		// now get all users that might be affected by the change in window content 
		List<User> userList = this.userManager.getAffectedUsers(app); 
		System.out.println(userList.size() + " users were affected by this operation"); 
		for ( User otherUser : userList ){
			
			// only treat other users, not invoking user here
			if(user != otherUser){
			
				// set current mouse pointer id 
				pointerID = otherUser.getPointerID(); 

				// get previous selection id 
				String selectionId = otherUser.getPrevSelectionID(); 
				System.out.println("Affected user (" + otherUser.getPointerID() 
						+ ") had previous selection id " + selectionId); 

				if(!selectionId.isEmpty()){
					// request visual links for all windows associated with the user 
					List<Application> appList = otherUser.getAllPrevApps(); 
					// we need to wait for all user's applications 
					for( Application userApp : appList ){
						boolean isSource = false; 
						if(userApp == otherUser.getPrevSrcApp()){
							isSource = true; 
						}
						this.selectionManager.addSelection(userApp, selectionId, pointerID, isSource); 
					}

					checkRender(pointerID);
				}
			
			}
		}
		
	}
	
	public void reportSelection(String appName, String selectionId, String boundingBoxListXML) {
		System.out.println("VisLinkManager: reportSelection, appName=" + appName + ", selId=" + selectionId + ", xml=" + boundingBoxListXML);
		
		Application app = applicationManager.getApplications().get(appName);
		
		
		if (boundingBoxListXML != null && boundingBoxListXML.isEmpty()) {
			boundingBoxListXML = null;
		}
		
		AccessInformation accessInformation = rendererPrx.getAccessInformation(app.getId());
		ApplicationAccessInfo[] targetApplicationIds = accessInformation.applications;
		String pointerID = accessInformation.pointerId;
		
		// save selection as source selection 
		this.selectionManager.addSelection(app, selectionId, pointerID, true); 
		
		UserSelection selection = this.selectionManager.getSelection(app, pointerID); 
		
		if (boundingBoxListXML != null) {
			BoundingBoxList bbl = createBoundingBoxList(boundingBoxListXML);
			selection.setBoundingBoxList(bbl); 
		}
		selection.setReported(); 

		
		// multi-user management: get / create user and store selection ID / source app 
		System.out.println("Get user with pointer ID: " + pointerID);
		User user = this.userManager.getUser(pointerID); 
		// save access information for user 
		user.setAppAccess(this.applicationManager, accessInformation); 
		// store new selection in source window 
		user.setNewSelection(selectionId, app); 
		
		for (ApplicationAccessInfo appId : targetApplicationIds) {
			Application currentApp = applicationManager.getApplicationsById().get(appId.applicationID);
			if (currentApp.getId() != app.getId() || boundingBoxListXML == null) {
				this.selectionManager.addSelection(currentApp, selectionId, pointerID, false); 
			}
		}
		
		checkRender(pointerID);
	}
	
	public void reportOneShot(User user, AccessInformation accessInformation){
		System.out.println("VisLinkManager: reportOneShot (user: " + user.getPointerID() + ")"); 
		
		Application srcApp = user.getPrevSrcApp(); 
		String selectionID = ""; 
		if(srcApp == null){
			System.out.println("User does not have any source window"); 
		}
		else{
			selectionID = this.clipboardManager.getSelection(); 
			System.out.println("Selection (clipboard): "+selectionID); 
			if(!selectionID.isEmpty()){
				int srcAppID = srcApp.getId(); 
				this.reportOneShot(user, selectionID, accessInformation, srcAppID, OneShotTimeoutEvent.ONE_SHOT_LONG_DISPLAY_TIME); 
			}
		}
	}
	
	public void reportOneShot(User user, User owner, AccessInformation accessInformation, int srcAppID){
		System.out.println("VisLinkManager: reportOneShot (user: " + user.getPointerID() + ", owner: " + owner.getPointerID() + ")");
		
		String selectionID = owner.getPrevSelectionID(); 
		this.reportOneShot(user, selectionID, accessInformation, srcAppID, OneShotTimeoutEvent.ONE_SHOT_DISPLAY_TIME); 
	}
	
	public void reportOneShot(User user, String selectionID, AccessInformation accessInformation, int srcAppID, int displayTime){
		System.out.println("VisLinkManager: reportOneShot (user: " + user.getPointerID() + ", string: " + selectionID + ")"); 
		
		// generate timeout handling 
		TimeoutEvent event = new OneShotTimeoutEvent(user); 
		if(user.getTimeoutHandler() != null){
			user.getTimeoutHandler().cancel(); 
			user.setTimeoutHandler(null); 
		}
		user.setTimeoutHandler(new TimeoutHandler(event, this)); 

		Timer timer = new Timer(); 
		timer.schedule(user.getTimeoutHandler(), displayTime); 

		
		// check if the user currently has one-shot links on (only one set allowed at the moment)
		if(user.getCurrentRenderType() == VisualLinksRenderType.RenderTypeOneShot){
			System.out.println("User is currently having one-shot links --> ignoring..."); 
			return; 
		}
		
		// set the current render type 
		user.setCurrentRenderType(VisualLinksRenderType.RenderTypeOneShot); 
		
		// retrieve access information for user 
		ApplicationAccessInfo[] targetApplicationIds = accessInformation.applications; 
		String pointerID = user.getPointerID(); 
		
		// save access information for user 
		user.setAppAccess(this.applicationManager, accessInformation); 
		
		// push selections but do not save user selection 
		for (ApplicationAccessInfo appId : targetApplicationIds) {
			Application app = applicationManager.getApplicationsById().get(appId.applicationID);
			// check whether this is the source application
			boolean isSource = false; 
			if(appId.applicationID == srcAppID){
				System.out.println(app.getName() + " is source");
				isSource = true; 
			}
			// add selection 
			this.selectionManager.addSelection(app, selectionID, pointerID, isSource); 
		}
		
		
		
		checkRender(pointerID);
	}
	
	public void releaseOneShot(User user){
		System.out.println("VisLinkManager: releaseOneShot (user: " + user.getPointerID()); 
		
		// check which mode the user is currently in 
		if(user.getCurrentRenderType() != VisualLinksRenderType.RenderTypeOneShot){
			System.out.println("not rendering one-shot links --> ignoring ..."); 
			return; 
		}
		
		// reset state 
		user.setCurrentRenderType(VisualLinksRenderType.RenderTypeNormal); 
		
		// restore previous selection ID 
		String selectionID = user.getPrevSelectionID(); 
		
		// set current mouse pointer id 
		String pointerID = user.getPointerID(); 


		System.out.println("Affected user (" + user.getPointerID() 
				+ ") had previous selection id " + selectionID); 
		
		if(!selectionID.isEmpty()){

			// request visual links for all windows associated with the user 
			List<Application> appList = user.getAllPrevApps(); 
			// we need to wait for all user's applications 
			for( Application userApp : appList ){
				boolean isSource = false; 
				if(userApp == user.getPrevSrcApp()){
					isSource = true; 
				}
				this.selectionManager.addSelection(userApp, selectionID, pointerID, isSource); 
			}

			checkRender(pointerID);
		}
	}

	public void reportVisualLinks(String appName, String pointerID, String boundingBoxListXML) {
		System.out.println("VisLinkManager: reportVisualLinks, appName=" + appName + " pointerID=" + pointerID + ", xml=" + boundingBoxListXML);
		
		Application app = applicationManager.getApplications().get(appName);
		
		if(app == null || pointerID == null){
			System.out.println("\n ERROR: application or pointerID is null!\n"); 
			return; 
		}
		
		UserSelection selection = selectionManager.getSelection(app, pointerID); 
		selection.setReported(); 
		
		if(selection == null){
			System.out.println("\n ERROR: no selection registered for appName=" + appName + ", pointerID=" + pointerID + "\n");
			return; 
		}
		
		BoundingBoxList bbl = createBoundingBoxList(boundingBoxListXML);
		
		// multi-user handling: save target application, if applicable
		if(bbl.getList().size() > 0){
			System.out.println("reportVisualLinks(): add target application " + appName +" for " + pointerID); 
			this.userManager.getUser(pointerID).addPrevTargetApp(app); 
			
			// HACK: set source selection based on stored selection 
			bbl.list.get(0).setSource(selection.isSource()); 
		}
		
	
		
		selection.setBoundingBoxList(bbl); 
		checkRender(pointerID);
	}

	public void registerApplication(String appName, String xml) {
		registerApplication(appName, getWindowBoundingBox(xml));
	}

	public void registerApplication(String appName, BoundingBox windowBoundingBox) {
		Application app = applicationManager.getApplications().get(appName);

		if (app != null) {
			System.out.println("re-registering " + appName);
			app.getWindows().clear();
			app.getWindows().add(windowBoundingBox);
			SelectionContainer selectionContainer = createSelectionContainer(app.getId(), windowBoundingBox);
			rendererPrx.updateSelectionContainer(selectionContainer);
		} else {
			app = new Application();
			app.setDate(new Date());
			app.setName(appName);
			app.getWindows().add(windowBoundingBox);

			applicationManager.registerApplication(app);
			SelectionContainer selectionContainer = createSelectionContainer(app.getId(), windowBoundingBox);

			System.out.println("registering " + app); 
			rendererPrx.registerSelectionContainer(selectionContainer);
		}
	}

	public void registerApplication(Application app) {
		applicationManager.registerApplication(app);
		SelectionContainer selectionContainer = createSelectionContainer(app.getId(), app.getWindows().get(0));

		System.out.println("registering " + app); 
		rendererPrx.registerSelectionContainer(selectionContainer);
	}

		
	private SelectionContainer createSelectionContainer(int appId, BoundingBox wbb) {
		return new SelectionContainer(
				appId,
				wbb.getX(),
				wbb.getY(),
				wbb.getWidth(),
				wbb.getHeight(),
				new Color4f(-1.0f, 0.0f, 0.0f, 0.9f));
	}
	
	private BoundingBox getWindowBoundingBox(String xml) { 
		System.out.println(xml);

		BoundingBox bb = null;
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			StringReader sr = new StringReader(xml);
			bb = (BoundingBox) unmarshaller.unmarshal(sr);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bb;
	}
    
	private BoundingBoxList createBoundingBoxList(String boundingBoxListXML) {
		BoundingBoxList bbl = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(BoundingBoxList.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			StringReader sr = new StringReader(boundingBoxListXML);
			bbl = (BoundingBoxList) unmarshaller.unmarshal(sr);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return bbl;
	}
	
	public void checkRender(String pointerID) {
		int numSelections = this.selectionManager.getNumUserSelections(pointerID); 
		int numMissingSelections = this.selectionManager.getNumMissingReports(pointerID); 
		
		System.out.println("checkRender(): checking rendering for pointer " + pointerID); 
		System.out.println("checkRender(): numTargetApplications=" + (numSelections - 1));
		System.out.println("checkRender(): numBoundingBoxes=" + numSelections); 

		
		if(numMissingSelections == 0){
			System.out.println("VisLinkManager: start rendering vis links for #" + numSelections + " apps");
			renderVisualLinks(this.selectionManager.getBoundingBoxList(pointerID), pointerID); 
			this.selectionManager.clearUserSelections(pointerID); 
		} else {
			System.out.println("waiting for more reports, " + (numSelections - numMissingSelections) + " / " + numSelections);
		}
	}
	
	public ApplicationManager getApplicationManager() {
		return applicationManager;
	}

	public void setApplicationManager(ApplicationManager applicationManager) {
		this.applicationManager = applicationManager;
	}
	
	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public SelectionManager getSelectionManager() {
		return this.selectionManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

    
	/**
	 * Establishes a connection to the VisRenderer und creates 
	 * a proxy object for remote method invocation. 
	 */
	public void connect() {
		System.out.println("Connect to VisRenderer"); 
		
		this.applicationManager.clearApplications(); 
		
		// establish connection to renderer proxy 
		if(rendererPrx == null) {

			// init communication channel 
			communicator = Ice.Util.initialize();

			// get local host name 
			String hostname = ""; 
			try {
				InetAddress addr = InetAddress.getLocalHost();
				hostname = addr.getHostName(); 
				System.out.println("hostname="+hostname); 
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} 

			// get server port, name, and end point 
			int serverPort = adapterPort.value; 
			String serverName = adapterName.value; 
			String serverEndPoint = "tcp -h " + hostname + " -p " + serverPort;
			
			System.out.println("Server name: " + serverName); 
			System.out.println("Server end point: " + serverEndPoint); 

			try {
				// if no renderer system is running, this operation
				// will throw an exception 
				Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":" 
						+ serverEndPoint);
				rendererPrx = VisRendererIPrxHelper.checkedCast(proxy);
			} catch(Ice.ConnectionRefusedException e){
				System.out.println("Connection refused - VisRenderer not found"); 
			}
		} else {
			System.out.println("Already established connection");
		}
		
		// create manager ice interface 
		if(rendererPrx != null){
			
			// create ice interface instance 
			this.iceInterface = new VisLinkManagerIceInterface(this); 
			
			// set a default port to start with 
			int managerPort = 8085; 
			
			// iterate until a free port was found 
			ObjectAdapter adapter = null; 
			while(adapter == null){
				try{
					adapter = this.communicator.createObjectAdapterWithEndpoints("VisManager", "default -p " + managerPort); 
					System.out.println("Connected on port " + managerPort); 
				}
				catch (Ice.SocketException e){
					System.out.println("Port " + managerPort + " already in use"); 
				}
				catch (Exception e){
					System.out.println("General exception for interface on port " + managerPort); 
				}
				managerPort++; 
			}
			
			// add interface to adapter and receive proxy
			Ice.ObjectPrx objPrx = adapter.add(this.iceInterface, communicator.stringToIdentity("VisManagerI"));
			
			// cast proxy and save with interface class 
			this.iceInterface.setProxy(VisManagerIPrxHelper.checkedCast(objPrx)); 
			
			// activate adapter
			adapter.activate(); 
			
			// register interface proxy to renderer 
			this.rendererPrx.registerManager(this.iceInterface.getProxy()); 

		}
		else{
			System.out.println("Renderer proxy was not created"); 
		}
	}

	/**
	 * Clears all elements from the VisRenderer and closes the 
	 * network connection. 
	 */
	public void disconnect() {
		System.out.println("disconnect"); 
		if(this.rendererPrx != null){
			this.rendererPrx.clearAll(); 
		}
		if (communicator != null) {
			try {
				System.out.println("Destroy Ice communicator"); 
				communicator.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    private void renderVisualLinks(HashMap<Integer, BoundingBoxList> app2bbs, String pointerID) {
//		visLinks.drawVisualLinks(list);
    	renderWithIce(app2bbs, pointerID);
    }
    
    private void renderWithIce(HashMap<Integer, BoundingBoxList> app2bbs, String pointerID) {
    	ArrayList<SelectionGroup> selectionGroupList = new ArrayList<SelectionGroup>();
    	User user = this.userManager.getUser(pointerID); 
    	
    	for (Entry<Integer, BoundingBoxList> e : app2bbs.entrySet()) {
        	SelectionGroup selectionGroup = new SelectionGroup();

        	
        	selectionGroup.selections = new Selection[e.getValue().getList().size()];
        	ArrayList<Selection> selectionList = new ArrayList<Selection>();
    		for (BoundingBox bb : e.getValue().getList()) {
        		Selection selection = new Selection(bb.getX(), bb.getY(), bb.getWidth(), bb.getHeight(),
        				new Color4f(-1.0f, 0, 0, 0), bb.isSource());
        		selectionList.add(selection);
    		}
    		selectionGroup.selections = selectionList.toArray(selectionGroup.selections);
    		selectionGroup.containerID = e.getKey();
    		selectionGroupList.add(selectionGroup);
    	}

    	SelectionGroup[] groups  = new SelectionGroup[selectionGroupList.size()];
    	selectionGroupList.toArray(groups);
    	SelectionReport report = new SelectionReport();
    	report.pointerId = pointerID; 
    	report.renderType = user.getCurrentRenderType(); 
    	report.selectionGroups = groups; 
    	rendererPrx.renderAllLinks(report);
    }

	public void afterPropertiesSet() throws Exception {
		System.out.println("VisLinkManager: connecting to renderer");
		connect();
		jaxbContext = JAXBContext.newInstance(BoundingBoxList.class, BoundingBox.class);
	}

	public void destroy() throws Exception {
		System.out.println("VisLinkManager: destroying (unregister apps and disconnect from renderer)");
    	unregisterApplications();
    	disconnect();
	}

    private void unregisterApplications() {
		for (Application app : applicationManager.getApplications().values()) {
			rendererPrx.unregisterSelectionContainer(app.getId());
		}
    }

	public void unregisterApplication(String appName) {
		// unregister from application list 
		Application app = applicationManager.getApplications().remove(appName);
		if (app != null) {
			// unregister from renderer
			rendererPrx.unregisterSelectionContainer(app.getId());
			// unregister selections
			selectionManager.clearUnreportedSelections(app); 
			// unregister from user
			userManager.clearApplicationFromUser(app); 
		}
	}

	public void clearVisLinks() {
		rendererPrx.clearSelections();
	}

}
