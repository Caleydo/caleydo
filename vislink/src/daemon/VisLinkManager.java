package daemon;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import Ice.Communicator;
import VIS.AccessInformation;
import VIS.Color4f;
import VIS.Selection;
import VIS.SelectionContainer;
import VIS.SelectionGroup;
import VIS.VisRendererIPrx;
import VIS.VisRendererIPrxHelper;
import VIS.adapterName;
import VIS.adapterPort;

public class VisLinkManager implements InitializingBean, DisposableBean {
	
	ApplicationManager applicationManager;
	
	UserManager userManager; 
	
	HashMap<Integer, BoundingBoxList> app2bbl;
	
	/** target applications for most recent reported selectionId */
	private int[] targetApplicationIds;
	
	/** number of target applications required for rendering */
	int numApps; 
	
	/** mouse pointer id that triggered the visual links as retrieved from deskotheque */
	private String triggeringMousePointerId;

	JAXBContext jaxbContext; 
	
	/** Ice communication object. */
	private Communicator communicator;
	
	/** Proxy object of VisRenderer for remote method invocation. */
	private VisRendererIPrx rendererPrx;
	
	public VisLinkManager() {
		app2bbl = new HashMap<Integer, BoundingBoxList>();
		targetApplicationIds = new int[0];
		triggeringMousePointerId = null;
		this.numApps = 0; 
	}
	
	public void reportWindowChange(String appName) {
		System.out.println("VisLinkManager: reportWindowChange, appName=" + appName);
		
		Application app = applicationManager.getApplications().get(appName);
		
		// clear previous selections
		app2bbl.clear(); 
		
		// find the mouse pointer that has triggered the window change 
		AccessInformation accessInformation = rendererPrx.getAccessInformation(app.getId());
		targetApplicationIds = accessInformation.applicationIds;
		triggeringMousePointerId = accessInformation.pointerId;
		
		// we need to wait for both, the source window and the target windows 
		this.numApps = this.targetApplicationIds.length + 1; 
		
		// check out whether we need to redraw links for that user 
		User user = this.userManager.getUser(triggeringMousePointerId); 
		if(user.isActive()){
			String selectionId = user.getPrevSelectionID(); 
			System.out.println("User changing window content (" + triggeringMousePointerId 
					+ ") had previous selection id " + selectionId); 
			// new settings for the active user 
			user.setNewSelection(selectionId, app); 
			
			// request visual links for source window 
			app.setSendId(selectionId); 
			
			// request visual links for target window 
			for (int appId : targetApplicationIds) {
				Application currentApp = applicationManager.getApplicationsById().get(appId);
				currentApp.setSendId(selectionId);
			}
			
			checkRender();
		}
		
		// now get all users that might be affected by the change in window content 
		List<User> userList = this.userManager.getAffectedUsers(app); 
		System.out.println(userList.size() + " other users were affected by this operation"); 
		for ( User otherUser : userList ){
			
			// only treat other users, not invoking user here
			if(user != otherUser){
				
				// clear previous selections 
				app2bbl.clear(); 
			
				// set current mouse pointer id 
				this.triggeringMousePointerId = otherUser.getPointerID(); 

				// get previous selection id 
				String selectionId = otherUser.getPrevSelectionID(); 
				System.out.println("Affected user (" + otherUser.getPointerID() 
						+ ") had previous selection id " + selectionId); 

				// request visual links for all windows associated with the user 
				List<Application> appList = otherUser.getAllPrevApps(); 
				// we need to wait for all user's applications 
				this.numApps = appList.size(); 
				for( Application userApp : appList ){
					userApp.setSendId(selectionId); 
				}

				checkRender();
			
			}
		}
		
	}
	
	public void reportSelection(String appName, String selectionId, String boundingBoxListXML) {
		System.out.println("VisLinkManager: reportVisualLinks, appName=" + appName + ", selId=" + selectionId + ", xml=" + boundingBoxListXML);
		
		Application app = applicationManager.getApplications().get(appName);
		
		if (boundingBoxListXML != null && boundingBoxListXML.isEmpty()) {
			boundingBoxListXML = null;
		}
		
		app2bbl.clear();
		if (boundingBoxListXML != null) {
			BoundingBoxList bbl = createBoundingBoxList(boundingBoxListXML);
			app2bbl.put(app.getId(), bbl);
		}

		AccessInformation accessInformation = rendererPrx.getAccessInformation(app.getId());
		targetApplicationIds = accessInformation.applicationIds;
		triggeringMousePointerId = accessInformation.pointerId;
		
		// we need to wait for all target applications for rendering 
		this.numApps = this.targetApplicationIds.length + 1; 
		
		// multi-user management: get / create user and store selection ID / source app 
		System.out.println("Get user with pointer ID: " + triggeringMousePointerId);
		User user = this.userManager.getUser(triggeringMousePointerId); 		
		user.setNewSelection(selectionId, app); 
		
		for (int appId : targetApplicationIds) {
			Application currentApp = applicationManager.getApplicationsById().get(appId);
			if (currentApp.getId() != app.getId() || boundingBoxListXML == null) {
				currentApp.setSendId(selectionId);
			}
		}
		
		checkRender();
	}

	public void reportVisualLinks(String appName, String boundingBoxListXML) {
		System.out.println("VisLinkManager: reportVisualLinks, appName=" + appName + ", xml=" + boundingBoxListXML);
		Application app = applicationManager.getApplications().get(appName);
		BoundingBoxList bbl = createBoundingBoxList(boundingBoxListXML);
		
		// multi-user handling: save target application, if applicable
		if(bbl.getList().size() > 0){
			this.userManager.getUser(triggeringMousePointerId).addPrevTargetApp(app); 
		}
		
		app2bbl.put(app.getId(), bbl);
		checkRender();
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

	public String retrieveSelectionId(String appName) {
		Application app = applicationManager.getApplications().get(appName);
		return app.fetchSendId();
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
	
	public void checkRender() {
		System.out.println("checkRender(): numTargetApplications=" + (this.numApps - 1));
		System.out.println("checkRender(): app2bbl.size()=" + app2bbl.size());
		if (app2bbl.size() >= (this.numApps)) {
			System.out.println("VisLinkManager: start rendering vis links for #" + (this.numApps) + " apps");
			renderVisualLinks(app2bbl);
			app2bbl.clear();
		} else {
			System.out.println("waiting for more reports, " + app2bbl.size() + " / " + (this.numApps));
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

    
	/**
	 * Establishes a connection to the VisRenderer und creates 
	 * a proxy object for remote method invocation. 
	 */
	public void connect() {
		System.out.println("Connect to VisRenderer"); 
		
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

    private void renderVisualLinks(HashMap<Integer, BoundingBoxList> app2bbs) {
//		visLinks.drawVisualLinks(list);
    	renderWithIce(app2bbs);
    }
    
    private void renderWithIce(HashMap<Integer, BoundingBoxList> app2bbs) {
    	ArrayList<SelectionGroup> selectionGroupList = new ArrayList<SelectionGroup>();
    	
    	for (Entry<Integer, BoundingBoxList> e : app2bbs.entrySet()) {
        	SelectionGroup selectionGroup = new SelectionGroup();
        	
        	// check whether this is the source selection group 
        	User user = this.userManager.getUser(triggeringMousePointerId); 
        	int srcAppID = -1; 
        	if(user.getPrevSrcApp() != null){
        		srcAppID = user.getPrevSrcApp().getId(); 
        	}
        	
        	selectionGroup.selections = new Selection[e.getValue().getList().size()];
        	ArrayList<Selection> selectionList = new ArrayList<Selection>();
    		for (BoundingBox bb : e.getValue().getList()) {
    			// HACK! 
    			if(e.getKey() == srcAppID){
        			bb.setSource(true); 
        		}
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
    	rendererPrx.renderAllLinks(groups);
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
		Application app = applicationManager.getApplications().remove(appName);
		if (app != null) {
			rendererPrx.unregisterSelectionContainer(app.getId());
			
		}
	}

	public void clearVisLinks() {
		rendererPrx.clearSelections();
	}

}
