package org.caleydo.rcp.views.swt.toolbar.listener;

import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.rcp.views.swt.toolbar.ToolBarMediator;

public abstract class ToolBarListener
	implements IEventListener {

	/** toolBarMediator this listener is related to */
	protected ToolBarMediator toolBarMediator;

	public ToolBarMediator getToolBarMediator() {
		return toolBarMediator;
	}

	public void setToolBarMediator(ToolBarMediator toolBarMediator) {
		this.toolBarMediator = toolBarMediator;
	}

}
