package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.NewContentGroupInfoEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewContentGroupInfoHandler;

public class NewContentGroupInfoActionListener
	extends AEventListener<INewContentGroupInfoHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewContentGroupInfoEvent) {
			NewContentGroupInfoEvent newGroupInfoEvent = (NewContentGroupInfoEvent) event;
			handler.handleNewContentGroupInfo(newGroupInfoEvent.getVAType(),
				newGroupInfoEvent.getGroupList(), newGroupInfoEvent.isDeleteTree());
		}
	}
}
