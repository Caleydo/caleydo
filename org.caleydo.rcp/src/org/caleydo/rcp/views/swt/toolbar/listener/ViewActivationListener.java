package org.caleydo.rcp.views.swt.toolbar.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.AttachedViewActivationEvent;

public class ViewActivationListener
	extends ToolBarListener {

	@Override
	public void handleEvent(AEvent event) {
		AttachedViewActivationEvent viewActivationEvent = (AttachedViewActivationEvent) event;
		toolBarMediator.renderToolBar(viewActivationEvent.getViewIDs());
	}

}
