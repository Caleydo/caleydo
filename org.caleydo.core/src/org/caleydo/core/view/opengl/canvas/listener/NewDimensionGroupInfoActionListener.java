package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.dimensionbased.NewDimensionGroupInfoEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewDimensionGroupInfoHandler;

public class NewDimensionGroupInfoActionListener
	extends AEventListener<INewDimensionGroupInfoHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewDimensionGroupInfoEvent) {
			NewDimensionGroupInfoEvent newGroupInfoEvent = (NewDimensionGroupInfoEvent) event;
			handler.handleNewDimensionGroupInfo(newGroupInfoEvent.getVAType(),
				newGroupInfoEvent.getGroupList(), newGroupInfoEvent.isDeleteTree());
		}
	}
}
