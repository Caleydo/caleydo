package org.caleydo.core.manager.view;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.NetworkManager;

/**
 * Handles {@link CreateGUIViewEvent}s by invoking the method on the related ViewManager.
 * {@link CreateGUIViewEvent}s are only handled if they do not have a target application name specified or the
 * specified target application name is equals to this network name the executing caleydo application.
 * 
 * @author Werner Puff
 */
public class CreateGUIViewListener
	extends AEventListener<ViewManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateGUIViewEvent) {
			CreateGUIViewEvent createSWTViewEvent = (CreateGUIViewEvent) event;
			System.out.println("create swt view event serialized-view="
				+ createSWTViewEvent.getSerializedView());
			String target = createSWTViewEvent.getTargetApplicationID();
			NetworkManager networkManager = GeneralManager.get().getGroupwareManager().getNetworkManager();
			if (target == null || target.equals(networkManager.getNetworkName())) {
				handler.createSWTView(createSWTViewEvent.getSerializedView());
			}
		}
	}

}
