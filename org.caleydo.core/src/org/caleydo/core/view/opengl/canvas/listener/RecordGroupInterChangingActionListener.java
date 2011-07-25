package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.InterchangeContentGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IContentGroupsActionHandler;

public class RecordGroupInterChangingActionListener
	extends AEventListener<IContentGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof InterchangeContentGroupsEvent) {
			handler.handleInterchangeContentGroups();
		}
	}
}
