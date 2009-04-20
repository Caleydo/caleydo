package org.caleydo.rcp.views.swt.toolbar.listener;

import java.util.ArrayList;

import org.caleydo.core.manager.event.AEvent;

public class DetachedViewActivationListener
	extends ToolBarListener {
	
	@Override
	public void handleEvent(AEvent event) {
		toolBarMediator.renderToolBar(new ArrayList<Integer>());
	}

}
