package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.InterchangeStorageGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IStorageGroupsActionHandler;

public class StorageGroupInterChangingActionListener
	extends AEventListener<IStorageGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof InterchangeStorageGroupsEvent) {

			handler.handleInterchangeStorageGroups();
		}
	}
}
