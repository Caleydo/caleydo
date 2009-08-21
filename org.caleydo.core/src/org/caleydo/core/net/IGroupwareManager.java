package org.caleydo.core.net;

/**
 * This interface has to be implemented by environment specific groupware-manager classes.
 * @author Werner Puff
 */
public interface IGroupwareManager {
	
	public void startServer();
	
	public void startClient();
	
	public void getHomeGroupwareClient();
	
	public void getPublicGroupwareClient();
	
	public String[] getAvailableGroupwareClients();
	
	public void stop();

	public NetworkManager getNetworkManager();
	
	public void setNetworkManager(NetworkManager networkManager);
}
