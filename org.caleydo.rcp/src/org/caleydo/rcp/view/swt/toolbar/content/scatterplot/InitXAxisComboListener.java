package org.caleydo.rcp.view.swt.toolbar.content.scatterplot;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.InitAxisComboEvent;

/**
 * Listener that reacts to Inititailaize The ComboBoxes For Axis Selection in the Scatterplot
 * 
 * @author Jürgen Pillhofer
 */
public class InitXAxisComboListener
	extends AEventListener<XAxisSelector> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof InitAxisComboEvent) {
			handler.initComboString(((InitAxisComboEvent) event).getAxisNames());
		}
	}

}
