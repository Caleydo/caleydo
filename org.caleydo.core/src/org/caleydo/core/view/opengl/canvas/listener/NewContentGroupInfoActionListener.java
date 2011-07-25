package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.tablebased.NewRecordGroupInfoEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewContentGroupInfoHandler;

public class NewContentGroupInfoActionListener
	extends AEventListener<INewContentGroupInfoHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewRecordGroupInfoEvent) {
			NewRecordGroupInfoEvent newGroupInfoEvent = (NewRecordGroupInfoEvent) event;
			handler.handleNewContentGroupInfo(newGroupInfoEvent.getVAType(),
				newGroupInfoEvent.getGroupList(), newGroupInfoEvent.isDeleteTree());
		}
	}
}
