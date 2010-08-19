package org.caleydo.core.net;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.DataInitializationData;
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
	 * @return intiialization data retrieved from the groupware server to complete application startup
	 */
	public static DataInitializationData startPlexClient(String serverAddress) {
		IGroupwareManager groupwareManager = GroupwareUtils.createDeskothequeManager();
		GeneralManager.get().setGroupwareManager(groupwareManager);
		groupwareManager.setServerAddress(serverAddress);
		groupwareManager.startClient();
		GeneralManager.get().getViewGLCanvasManager().getDisplayLoopExecution()
			.executeMultiple(groupwareManager);
		return (groupwareManager.getInitData());
	}
}
