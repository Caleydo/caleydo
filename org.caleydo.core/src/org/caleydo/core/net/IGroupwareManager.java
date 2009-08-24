package org.caleydo.core.net;

import org.caleydo.core.serialize.ApplicationInitData;

/**
 * This interface has to be implemented by environment specific groupware-manager classes.
 * @author Werner Puff
 */
public interface IGroupwareManager {
	
	public void startServer();
	
	public void startClient();
	
	public String getHomeGroupwareClient();
	
	public String getPublicGroupwareClient();
	
	public String[] getAvailableGroupwareClients();
	
	public void stop();

	public NetworkManager getNetworkManager();
	
	public void setNetworkManager(NetworkManager networkManager);

	public ApplicationInitData getInitData();
}
