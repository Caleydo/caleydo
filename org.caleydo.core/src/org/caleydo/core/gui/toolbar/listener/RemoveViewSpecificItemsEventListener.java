package org.caleydo.core.gui.toolbar.listener;

import java.util.ArrayList;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.IView;

public class RemoveViewSpecificItemsEventListener
	extends ToolBarListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.renderToolBar(new ArrayList<IView>());
	}

}
