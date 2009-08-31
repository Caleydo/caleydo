package org.caleydo.core.net;

import java.util.HashMap;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.view.CanvasConnectionMap;
import org.caleydo.core.serialize.ApplicationInitData;

/**
 * This interface has to be implemented by environment specific groupware-manager classes.
 * @author Werner Puff
 */
public interface IGroupwareManager
	extends Runnable{
	
	public void startServer();
	
	public void startClient();
	
	public String getHomeGroupwareClient();
	
	public String getPublicGroupwareClient();
	
	public String[] getAvailableGroupwareClients();
	
	public void stop();

	public NetworkManager getNetworkManager();
	
	public void setNetworkManager(NetworkManager networkManager);

	public ApplicationInitData getInitData();
	
	public void sendConnectionLines(HashMap<EIDType, CanvasConnectionMap> canvasConnections);

	public boolean isGroupwareConnectionLinesEnabled();
}
