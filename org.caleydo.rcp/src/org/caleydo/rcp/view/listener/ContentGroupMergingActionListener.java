package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.group.MergeStorageGroupsEvent;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IContentGroupsActionHandler;

public class ContentGroupMergingActionListener
	extends AEventListener<IContentGroupsActionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof MergeStorageGroupsEvent) {
			handler.handleMergeContentGroups();
		}
	}
}
