package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.NewStorageGroupInfoEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewStorageGroupInfoHandler;

public class NewStorageGroupInfoActionListener
	extends AEventListener<INewStorageGroupInfoHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewStorageGroupInfoEvent) {
			NewStorageGroupInfoEvent newGroupInfoEvent = (NewStorageGroupInfoEvent) event;
			handler.handleNewStorageGroupInfo(newGroupInfoEvent.getVAType(),
				newGroupInfoEvent.getGroupList(), newGroupInfoEvent.isDeleteTree());
		}
	}
}
