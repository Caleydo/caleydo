/**
 * 
 */
package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.event.RenameEvent;

/**
 * Listener for renaming a brick
 * 
 * @author Alexander Lex
 * 
 */
public class RenameListener extends AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RenameEvent) {
			RenameEvent renameEvent = (RenameEvent) event;
			handler.rename(renameEvent.getID());
		}

	}

}
