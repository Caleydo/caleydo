package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.ExportContentGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IContentGroupsActionHandler;

public class ContentGroupExportingListener
	extends AEventListener<IContentGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ExportContentGroupsEvent) {
			handler.handleExportContentGroups();
		}
	}
}
