package org.caleydo.view.base.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.ExportGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IGroupsActionHandler;

public class GroupExportingListener
		extends
			AEventListener<IGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ExportGroupsEvent) {
			ExportGroupsEvent exportGroupsEvent = (ExportGroupsEvent) event;
			handler.handleExportGroups(exportGroupsEvent.isGeneGroup());
		}
	}
}
