package test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import VIS.Color4f;
import VIS.SelectionContainer;
import VIS.SelectionGroup;
import VIS.VisRendererIPrx;
import VIS.VisRendererIPrxHelper;
import VIS.adapterName;
import VIS.adapterPort;
import Ice.Communicator;

/**
 * Manages the connection and wraps up the remote methods to 
 * VisRenderer. 
 * @author Manuela Waldner
 *
 */
public class RenderManager {
	
	/**
	 * Ice communication object. 
	 */
	private Communicator communicator;
	
	/**
	 * Proxy object of VisRenderer for remote method invocation. 
	 */
	private VisRendererIPrx rendererPrx; 
	
	/**
	 * Establishes a connection to the VisRenderer und creates 
	 * a proxy object for remote method invocation. 
	 */
	public void connect(){
		System.out.println("Connect to VisRenderer"); 
		
		if(rendererPrx == null){

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

			try{

				// if no renderer system is running, this operation
				// will throw an exception 
				Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":" 
						+ serverEndPoint);
				rendererPrx = VisRendererIPrxHelper.checkedCast(proxy);
			}
			catch(Ice.ConnectionRefusedException e){
				System.out.println("Connection refused - VisRenderer not found"); 
			}
		}
		else{
			System.out.println("Already established connection");
		}
	}
	
	/**
	 * Clears all elements from the VisRenderer and closes the 
	 * network connection. 
	 */
	public void disconnect()
	{
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
	
	/**
	 * Registers a selection container (i.e. window) at the 
	 * VisRenderer application. 
	 * A container is described by a unique id, a screen rectangle 
	 * and a color for the node (bundling) point of the container. 
	 * @param id Unique ID of the application / window. 
	 * @param x X screen coordinate. 
	 * @param y Y screen coordinate. 
	 * @param w Width of the container. 
	 * @param h Height of the container. 
	 * @param r Red color component. If -1, the default container 
	 * color is issued by the VisRenderer and the other color components 
	 * are discarded. 
	 * @param g Green color component. 
	 * @param b Blue color component. 
	 * @param a Alpha. 
	 * @return Returns true if the element was registered at the 
	 * VisRenderer. Registration fails if either no connection has 
	 * been established of the id is invalid (e.g. duplicate). 
	 */
	public boolean registerSelectionContainer(int id, int x, int y, int w, 
			int h, float r, float g, float b, float a)
	{
		System.out.println("registerSelectionContainer"); 
		if(this.rendererPrx != null){
			System.out.println("Create container"); 
			SelectionContainer container = new SelectionContainer(); 

			container.id = id; 
			container.x = x; 
			container.y = y; 
			container.w = w; 
			container.h = h; 
			container.color = new Color4f(r, g, b, a); 
			
			System.out.println("Contact renderer"); 
		
			return this.rendererPrx.registerSelectionContainer(container); 
		}
		return false; 
	}
	
	/**
	 * Sends a list of selection groups (see VisRenderer Ice interface) 
	 * to the VisRenderer, which contacts the window manager to draw 
	 * the generated visual links. 
	 * @param selections The group of selections. SelectionGroup is defined 
	 * by the Ice interface of the VisRenderer. 
	 */
	public void renderLinks(SelectionGroup[] selections){
		System.out.println("renderLinks"); 
		if(this.rendererPrx != null){
			System.out.println("Send data to renderer"); 
			this.rendererPrx.renderAllLinks(selections); 
		}
	}


}
