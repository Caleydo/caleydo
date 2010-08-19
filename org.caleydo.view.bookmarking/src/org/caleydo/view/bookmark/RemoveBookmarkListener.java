package org.caleydo.view.bookmark;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.RemoveBookmarkEvent;

public class RemoveBookmarkListener extends AEventListener<GLBookmarkView> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof RemoveBookmarkEvent<?>) {
			RemoveBookmarkEvent<?> removeBookmarkEvent = (RemoveBookmarkEvent<?>) event;
			handler.handleRemoveBookmarkEvent(removeBookmarkEvent);
		}

	}

}
