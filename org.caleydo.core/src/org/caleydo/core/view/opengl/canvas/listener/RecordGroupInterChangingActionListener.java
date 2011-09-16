package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.group.InterchangeContentGroupsEvent;
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
