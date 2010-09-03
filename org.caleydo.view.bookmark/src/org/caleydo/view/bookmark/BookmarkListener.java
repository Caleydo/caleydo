package org.caleydo.view.bookmark;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.BookmarkEvent;

public class BookmarkListener extends AEventListener<GLBookmarkView> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof BookmarkEvent<?>) {
			
			BookmarkEvent<?> bookmarkEvent = (BookmarkEvent<?>) event;
			handler.handleNewBookmarkEvent(bookmarkEvent);
		}
	}
}
