package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.MergeDimensionGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IDimensionGroupsActionHandler;

public class DimensionGroupMergingActionListener
	extends AEventListener<IDimensionGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof MergeDimensionGroupsEvent) {
			handler.handleMergeDimensionGroups();
		}
	}
}
