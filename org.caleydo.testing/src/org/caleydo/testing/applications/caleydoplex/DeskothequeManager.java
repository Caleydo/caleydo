package org.caleydo.testing.applications.caleydoplex;

import DKT.GroupwareClientAppIPrx;
import DKT.GroupwareClientAppIPrxHelper;
import DKT.GroupwareInformation;
import DKT.MasterApplicationIPrx;
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
		GroupwareInformation info = masterPrx.registerGroupwareClient(groupwareClientPrx, "Caleydo", serverPrx, 1300, 0, 100, 100);
		
		System.out.println("Groupware information: displayID: " + info.displayID
				+ ", is private: " + info.isPrivate + ", deskoXID: " + info.deskoXID);
		
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
