package org.caleydo.core.view.opengl.layout.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

/**
 * Listener for {@link LayoutSizeCollisionEvent}
 * 
 * @author Alexander Lex
 */
public class LayoutSizeCollisionListener
	extends AEventListener<ILayoutSizeCollisionHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LayoutSizeCollisionEvent) {
			LayoutSizeCollisionEvent layoutEvent = (LayoutSizeCollisionEvent) event;
			handler.handleLayoutSizeCollision(layoutEvent.getManagingClassID(), layoutEvent.getLayoutID(),
				layoutEvent.getToBigBy());
		}

	}

}
