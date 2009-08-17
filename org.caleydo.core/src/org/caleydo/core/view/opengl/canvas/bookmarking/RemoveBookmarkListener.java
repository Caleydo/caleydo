package org.caleydo.core.view.opengl.canvas.bookmarking;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.RemoveBookmarkEvent;

public class RemoveBookmarkListener
	extends AEventListener<GLBookmarkManager> {

	@Override
	public void handleEvent(AEvent event) {
		
		if (event instanceof RemoveBookmarkEvent<?>) {
			RemoveBookmarkEvent<?> removeBookmarkEvent = (RemoveBookmarkEvent<?>) event;
			handler.handleRemoveBookmarkEvent(removeBookmarkEvent);
		}
		
		

	}

}
