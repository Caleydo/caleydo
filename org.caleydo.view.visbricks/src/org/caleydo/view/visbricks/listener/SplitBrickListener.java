/**
 * 
 */
package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.SplitBrickEvent;

/**
 * @author Alexander Lex
 *
 */
public class SplitBrickListener extends AEventListener<GLVisBricks> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SplitBrickEvent) {
			SplitBrickEvent splitBrickEvent = (SplitBrickEvent) event;
			handler.splitBrick(splitBrickEvent.getConnectionBandID(), splitBrickEvent.isSplitLeftBrick());
		}
	}

}
