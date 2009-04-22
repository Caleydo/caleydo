package org.caleydo.rcp.views.swt.toolbar.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;

public class ViewActivationListener
	extends ToolBarListener {

	@Override
	public void handleEvent(AEvent event) {
		ViewActivationEvent viewActivationEvent = (ViewActivationEvent) event;
		toolBarMediator.renderToolBar(viewActivationEvent.getViewIDs());
	}

}
