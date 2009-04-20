package org.caleydo.rcp.views.swt.toolbar.listener;

import java.util.ArrayList;

import org.caleydo.core.manager.event.AEvent;

public class RemoveViewSpecificItemsEventListener
	extends ToolBarListener {
	
	@Override
	public void handleEvent(AEvent event) {
		toolBarMediator.renderToolBar(new ArrayList<Integer>());
	}

}
