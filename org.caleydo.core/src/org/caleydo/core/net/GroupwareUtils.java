package org.caleydo.core.net;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ApplicationInitData;
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
	 * @return intiialization data retrieved from the groupware server to complete application startup
	 */
	public static ApplicationInitData startPlexClient() {
		IGroupwareManager groupwareManager = GroupwareUtils.createDeskothequeManager();
		GeneralManager.get().setGroupwareManager(groupwareManager);
		groupwareManager.startClient();
		GeneralManager.get().getViewGLCanvasManager().getDisplayLoopExecution().executeMultiple(
			groupwareManager);
		return (groupwareManager.getInitData());
	}
}
