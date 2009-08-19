package org.caleydo.testing.applications.caleydoplex;

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
	
	private Communicator communicator;

	
	public void establishConnection() {
		GroupwareClient groupwareClient = new GroupwareClient();
		
		communicator = Ice.Util.initialize();
		Ice.ObjectAdapter adapter = communicator
				.createObjectAdapterWithEndpoints("GroupwareClient",
						"default -p 8050");
		Ice.ObjectPrx objPrx = adapter.add(groupwareClient, communicator
				.stringToIdentity("GroupwareClientAppI"));
		groupwareClientPrx = GroupwareClientAppIPrxHelper.checkedCast(objPrx);
		adapter.activate();
		
		String serverName = "ServerAppI-fcggpc203-1"; 
		String serverEndPoint = "tcp -h fcggpc203 -p 8011"; 
		
		Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":" + serverEndPoint); 
		ServerApplicationIPrx serverPrx = ServerApplicationIPrxHelper.checkedCast(proxy); 
		
		masterPrx = serverPrx.getMasterProxy(); 
		
		// testing communication 
		
		// registration at master proxy 
		// FIXME: real window extents required! 
		GroupwareInformation info = masterPrx.registerGroupwareClient(groupwareClientPrx, "Caleydo", serverPrx, 0, 0, 100, 100);
		
		System.out.println("Groupware information: displayID: " + info.displayID
				+ ", is private: " + info.isPrivate + ", deskoXID: " + info.deskoXID);
		
		// obtaining resource manager proxy 
		ResourceManagerIPrx resourceManagerPrx = masterPrx.getResourceManagerProxy(); 
		
		// getting available target locations 
		String[] targetClients = resourceManagerPrx.getAvailableGroupwareClients(info.deskoXID); 
		if(targetClients.length == 0){
			System.out.println("No target clients found"); 
		}
		for(int i = 0; i < targetClients.length; i++){
			System.out.println("Target client [" + i + "]: " + targetClients); 
		}
		
		// get home of groupware client 
		String homeClient = resourceManagerPrx.getHomeGroupwareClient(info.deskoXID); 
		System.out.println("Home client: " + homeClient);
		
		// get public groupware client 
		String publicClient = resourceManagerPrx.getPublicGroupwareClient(info.deskoXID); 
		System.out.println("Public client: " + publicClient); 
		
		// unregister groupware client 
		resourceManagerPrx.unregisterGroupwareClient(info.deskoXID); 
		
		//System.out.println("Master hostname: " + masterPrx.getLocalHostName()); 
	}
	
	public void destroy() {
		if (communicator != null) {
            try {
            	communicator.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}
