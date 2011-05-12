package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.MergeStorageGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IStorageGroupsActionHandler;

public class StorageGroupMergingActionListener
	extends AEventListener<IStorageGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof MergeStorageGroupsEvent) {
			handler.handleMergeStorageGroups();
		}
	}
}
