package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.tablebased.NewRecordGroupInfoEvent;
import org.caleydo.view.matchmaker.GLMatchmaker;

public class NewContentGroupInfoEventListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewRecordGroupInfoEvent) {
			NewRecordGroupInfoEvent newContentGroupInfoEvent = (NewRecordGroupInfoEvent) event;
			handler.handleContentGroupListUpdate(newContentGroupInfoEvent.getVAType(),
					newContentGroupInfoEvent.getTableID(),
					newContentGroupInfoEvent.getGroupList());
		}
	}

}
