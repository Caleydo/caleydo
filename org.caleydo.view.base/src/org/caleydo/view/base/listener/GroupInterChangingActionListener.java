package org.caleydo.view.base.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.InterchangeGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IGroupsActionHandler;

public class GroupInterChangingActionListener
		extends
			AEventListener<IGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof InterchangeGroupsEvent) {
			InterchangeGroupsEvent interchangeGroupsEvent = (InterchangeGroupsEvent) event;
			handler.handleInterchangeGroups(interchangeGroupsEvent
					.isGeneGroup());
		}
	}
}
