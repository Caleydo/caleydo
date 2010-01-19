package org.caleydo.view.bookmarking;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.BookmarkEvent;

public class BookmarkListener extends AEventListener<GLBookmarkManager> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof BookmarkEvent<?>) {
			BookmarkEvent<?> bookmarkEvent = (BookmarkEvent<?>) event;
			handler.handleNewBookmarkEvent(bookmarkEvent);
		}

	}

}
