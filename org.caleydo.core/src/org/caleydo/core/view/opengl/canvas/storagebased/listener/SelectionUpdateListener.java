package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;

public class SelectionUpdateListener
	extends AStorageBasedListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectionUpdateEvent) {
			SelectionUpdateEvent selectioUpdateEvent = (SelectionUpdateEvent) event; 
			ISelectionDelta delta = selectioUpdateEvent.getSelectionDelta();
			boolean scrollToSelection = selectioUpdateEvent.isScrollToSelection();
			view.handleSelectionUpdate(delta, scrollToSelection);
		}
	}

}
