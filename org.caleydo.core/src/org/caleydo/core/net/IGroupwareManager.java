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
package org.caleydo.core.net;

import java.util.HashMap;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.view.vislink.CanvasConnectionMap;

/**
 * This interface has to be implemented by environment specific groupware-manager classes.
 * 
 * @author Werner Puff
 */
public interface IGroupwareManager
	extends Runnable {

	/**
	 * Uses this caleydo application as caleydo server application. Needed paramters are implementation
	 * specific and have to be provided before calling this method.
	 */
	public void startServer();

	/**
	 * Starts this caledyo application as a client. Needed paramters are implementation specific and have to
	 * be provided before calling this method.
	 */
	public void startClient();

	/**
	 * Retrieves the caleydo network name of the caleydo application that initiated the recent event. This
	 * method is only useful in multi user environments like deskotheque.
	 * 
	 * @return private caleydo application network name
	 */
	public String getHomeGroupwareClient();

	/**
	 * Retrieves the caleydo network name of a public caleydo application. The determination of the
	 * application is group ware specific.
	 * 
	 * @return public caleydo application network name
	 */
	public String getPublicGroupwareClient();

	/**
	 * Retrieves a list of available caleydo network names
	 * 
	 * @return
	 */
	public String[] getAvailableGroupwareClients();

	/**
	 * Stops all groupware related operations like network communication and frees needed resources.
	 */
	public void stop();

	/**
	 * Retrieves the {@link NetworkManager} used for caleydo application communication
	 * 
	 * @return The used {@link NetworkManager}
	 */
	public NetworkManager getNetworkManager();

	/**
	 * Sets the {@link NetworkManager} that should be used by this {@link IGroupwareManager}
	 * 
	 * @param networkManager
	 */
	public void setNetworkManager(NetworkManager networkManager);

	/**
	 * On clients it returns the {@link SerializationData} as retrieved from the server to startup a client in
	 * the same manner as the server.
	 * 
	 * @return initialization parameters for clients to start
	 */
	public SerializationData getSerializationData();

	/**
	 * Propagates groupware connection lines to other caleydo applications.
	 * 
	 * @param canvasConnections
	 */
	public void sendConnectionLines(HashMap<IDType, CanvasConnectionMap> canvasConnections);

	/**
	 * <code>true</code> if the used groupware system is able to draw display comprehensive connection lines,
	 * <code>false</code> otherwise.
	 * 
	 * @return
	 */
	public boolean isGroupwareConnectionLinesEnabled();

	/**
	 * TODO: this method is only here, because deskotheque does not deliver server address to connecting
	 * clients yet
	 * 
	 * @param serverAddress
	 */
	public void setServerAddress(String serverAddress);

}
