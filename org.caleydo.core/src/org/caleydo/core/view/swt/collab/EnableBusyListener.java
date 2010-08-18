package org.caleydo.core.view.swt.collab;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * SWT event listener for requesting busy mode
 * 
 * @author Werner Puff
 */
public class EnableBusyListener
	implements Listener {

	Logger log = Logger.getLogger(EnableBusyListener.class.getName());

	Object requester;

	@Override
	public void handleEvent(Event event) {
		log.log(Level.INFO, "enable busy");
		GeneralManager.get().getViewGLCanvasManager().requestBusyMode(requester);
	}

	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
