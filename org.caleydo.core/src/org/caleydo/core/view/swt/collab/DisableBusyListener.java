package org.caleydo.core.view.swt.collab;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DisableBusyListener
	implements Listener {

	Logger log = Logger.getLogger(DisableBusyListener.class.getName());

	Object requester;

	@Override
	public void handleEvent(Event event) {
		log.log(Level.INFO, "enable busy");
		GeneralManager.get().getViewGLCanvasManager().releaseBusyMode(requester);
	}

	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
