package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.NewContentGroupInfoEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class NewContentGroupInfoEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewContentGroupInfoEvent) {
			NewContentGroupInfoEvent newContentGroupInfoEvent = (NewContentGroupInfoEvent) event;
			if (newContentGroupInfoEvent.getVAType() == ContentVAType.CONTENT) {
				handler.handleContentGroupListUpdate(newContentGroupInfoEvent.getSetID(),
						newContentGroupInfoEvent.getGroupList());
			}
		}

	}

}
