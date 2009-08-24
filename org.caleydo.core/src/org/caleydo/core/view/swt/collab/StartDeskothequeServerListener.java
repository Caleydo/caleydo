package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.GroupwareUtils;
import org.caleydo.core.net.IGroupwareManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Listener for the "start server" button that triggers the creation of the 
 * deskotheque related groupware management.
 * 
 * @author Werner Puff
 */
public class StartDeskothequeServerListener
	implements Listener {

	@Override
	public void handleEvent(Event event) {
		IGroupwareManager groupwareManager = GroupwareUtils.createDeskothequeManager();
		GeneralManager.get().setGroupwareManager(groupwareManager);
		groupwareManager.startServer();
	}

}
