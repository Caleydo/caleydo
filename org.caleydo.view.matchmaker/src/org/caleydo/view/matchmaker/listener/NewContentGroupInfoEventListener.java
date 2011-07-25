package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.dimensionbased.NewContentGroupInfoEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class NewContentGroupInfoEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewContentGroupInfoEvent) {
			NewContentGroupInfoEvent newContentGroupInfoEvent = (NewContentGroupInfoEvent) event;
			handler.handleContentGroupListUpdate(newContentGroupInfoEvent.getVAType(),
					newContentGroupInfoEvent.getSetID(),
					newContentGroupInfoEvent.getGroupList());
		}
	}

}
