/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.testing.applications.caleydoplex;

import java.net.InetAddress;
import java.net.UnknownHostException;

import DKT.GroupwareClientAppIPrx;
import DKT.GroupwareClientAppIPrxHelper;
import DKT.GroupwareInformation;
import DKT.MasterApplicationIPrx;
import DKT.ResourceManagerIPrx;
import DKT.ServerApplicationIPrx;
import DKT.ServerApplicationIPrxHelper;
import Ice.Communicator;

public class DeskothequeManager {

	private GroupwareClientAppIPrx groupwareClientPrx;

	private MasterApplicationIPrx masterPrx;
	
	private ServerApplicationIPrx serverPrx; 

	private Communicator communicator;

	String deskoID;

	public void establishConnection(int x, int y, int w, int h) {
		
		System.out.println("Establish Deskotheque connection"); 

		// check if there is already a connection 
		if (this.masterPrx == null) {

			GroupwareClient groupwareClient = new GroupwareClient();

			communicator = Ice.Util.initialize();
			// this port number is pre-defined in Deskotheque and usually
			// needs to be retrieved from the Deskotheque application
			// - as this is not possible from Java, we need to hardcode the port
			int port = 8050;
			Ice.ObjectAdapter adapter = null;

			// we need this loop as the port has to be unique
			// --> if more than one Caleydo instance is running,
			// the port number needs to be incremented!
			while(adapter == null) {
				try {
					System.out.println("Using port " + port);
					adapter = communicator.createObjectAdapterWithEndpoints(
							"GroupwareClient", "default -p " + port);
				} catch (Exception e) {
					System.out.println("Port " + port + " already in use");
				}
				port ++;
			}
			Ice.ObjectPrx objPrx = adapter.add(groupwareClient, communicator
					.stringToIdentity("GroupwareClientAppI"));
			groupwareClientPrx = GroupwareClientAppIPrxHelper
					.checkedCast(objPrx);
			adapter.activate();

			// get local host name 
			String hostname = ""; 
			try {
				InetAddress addr = InetAddress.getLocalHost();
				hostname = addr.getHostName(); 
				System.out.println("hostname="+hostname); 
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} 
			
			// get virtual display 
			String displayVar = System.getenv("DISPLAY"); 
			System.out.println("DISPLAY=" + displayVar); 
			// the display variable is in the format: 
			// :0.0 or :1
			// - but we want a single integer instead 
			if(displayVar.length() >= 2){
				// the first number is obviously what we need 
				displayVar = displayVar.substring(1, 2);
			}
			// turn display string into integer 
			Integer displayNumInt = new Integer(displayVar); 
			int displayNum = displayNumInt.intValue(); 
			System.out.println("Display number: " + displayNum); 
			
			// the server name is of the form 
			// ServerAppI-<hostname>-<xDisplay>
			String serverName = "ServerAppI-" + hostname + "-" + displayNum; 
			// the server endpoint is of the form 
			// tcp -h <hostname> -p 8011
			// the port 8011 is defined by Deskotheque so we have to 
			// hardcode that value here 
			String serverEndPoint = "tcp -h " + hostname + " -p 8011";
			
			try{

				// if no Deskotheque system is running, this operatio
				// will through an exception 
				Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":" 
						+ serverEndPoint);
				serverPrx = ServerApplicationIPrxHelper
				.checkedCast(proxy);

				masterPrx = serverPrx.getMasterProxy();

				// registration at master proxy
				GroupwareInformation info = masterPrx.registerGroupwareClient(
						groupwareClientPrx, "Caleydo", serverPrx, x, y, w, h);

				System.out.println("Groupware information: displayID: "
						+ info.displayID + ", is private: " + info.isPrivate
						+ ", deskoXID: " + info.deskoXID);

				// info.deskoXID is the unique identifier for Deskotheque
				this.deskoID = info.deskoXID;

				// testing communication

				// obtaining resource manager proxy
				ResourceManagerIPrx resourceManagerPrx = masterPrx
				.getResourceManagerProxy();

				// getting available target locations
				String[] targetClients = resourceManagerPrx
				.getAvailableGroupwareClients(info.deskoXID);
				if (targetClients.length == 0) {
					System.out.println("No target clients found");
				}
				for (int i = 0; i < targetClients.length; i++) {
					System.out.println("Target client [" + i + "]: "
							+ targetClients[i]);
				}

				// get home of groupware client
				String homeClient = resourceManagerPrx
				.getHomeGroupwareClient(info.deskoXID);
				System.out.println("Home client: " + homeClient);

				// get public groupware client
				String publicClient = resourceManagerPrx
				.getPublicGroupwareClient(info.deskoXID);
				System.out.println("Public client: " + publicClient);
			}
			catch(Ice.ConnectionRefusedException e){
				System.out.println("Connection refused - Deskotheque not found"); 
			}
		} else {
			System.out.println("Already established connection with ID "
					+ this.deskoID);
		}
	}

	public void destroy() {
		if (this.masterPrx != null) {
			// unregister at resource manager
			ResourceManagerIPrx resourceManagerPrx = masterPrx
					.getResourceManagerProxy();
			resourceManagerPrx.unregisterGroupwareClient(this.deskoID);
		}
		if (communicator != null) {
			try {
				communicator.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public MasterApplicationIPrx getMasterProxy(){
		return this.masterPrx; 
	}
	
	public ServerApplicationIPrx getServerProxy(){
		return this.serverPrx; 
	}
	
	public String getDeskoID(){
		return this.deskoID; 
	}

}
