package org.caleydo.view.parcoords.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.AFlagSetterEvent;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;

public class ChangeOrientationListener
		extends
			AEventListener<AStorageBasedView> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AFlagSetterEvent) {
			AFlagSetterEvent flagEvent = (AFlagSetterEvent) event;
			handler.changeOrientation(flagEvent.getFlag());
		}

	}

}
