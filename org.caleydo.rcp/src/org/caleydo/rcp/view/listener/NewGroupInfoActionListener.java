package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.NewGroupInfoEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewGroupInfoHandler;

public class NewGroupInfoActionListener
		extends
			AEventListener<INewGroupInfoHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewGroupInfoEvent) {
			NewGroupInfoEvent newGroupInfoEvent = (NewGroupInfoEvent) event;
			handler.handleNewGroupInfo(newGroupInfoEvent.getVAType(),
					newGroupInfoEvent.getGroupList(), newGroupInfoEvent
							.isDeleteTree());
		}
	}
}
