package org.caleydo.rcp.view.swt.toolbar.listener;

import java.util.ArrayList;

import org.caleydo.core.manager.event.AEvent;

public class RemoveViewSpecificItemsEventListener
	extends ToolBarListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.renderToolBar(new ArrayList<Integer>());
	}

}
