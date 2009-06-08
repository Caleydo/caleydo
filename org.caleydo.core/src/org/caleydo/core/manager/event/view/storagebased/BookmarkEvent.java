package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the current selection should be book-marked
 * 
 * @author Alexander Lex
 */
public class BookmarkEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
