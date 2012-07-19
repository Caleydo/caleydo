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

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.SerializationData;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Porvides utility methods for groupware related tasks.
 * 
 * @author Werner Puff
 */
public class GroupwareUtils {

	/**
	 * Loads the groupware manager for deskotheque as defined by the extension
	 * 'org.caleydo.plex.DeskothequeManager'
	 * 
	 * @return {@link IGroupwareManager} for deskotheque
	 */
	public static IGroupwareManager createDeskothequeManager() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint("org.caleydo.plex.GroupwareManager");
		IExtension ext = ep.getExtension("org.caleydo.plex.DeskothequeManager");
		IConfigurationElement[] ce = ext.getConfigurationElements();

		IGroupwareManager groupwareManager = null;
		try {
			groupwareManager = (IGroupwareManager) ce[0].createExecutableExtension("class");
		}
		catch (Exception ex) {
			throw new RuntimeException("Could not instantiate DeskotequeManager", ex);
		}
		return groupwareManager;
	}

	/**
	 * Starts the plugin org.caleydo.plex, retrieves the deskotheque-manager from it and starts this
	 * application as a groupware-client.
	 * 
	 * @param serverAddress
	 *            TODO should better be obtained from deskotheque
	 * @return initialization data retrieved from the groupware server to complete application startup
	 */
	public static SerializationData startPlexClient(String serverAddress) {
		IGroupwareManager groupwareManager = GroupwareUtils.createDeskothequeManager();
		GeneralManager.get().setGroupwareManager(groupwareManager);
		groupwareManager.setServerAddress(serverAddress);
		groupwareManager.startClient();
		GeneralManager.get().getViewManager().getDisplayLoopExecution().executeMultiple(groupwareManager);
		return (groupwareManager.getSerializationData());
	}
}
