package org.caleydo.view.treemap.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.treemap.LevelHighlightingEvent;
import org.caleydo.view.treemap.GLTreeMap;

/**
 * Listener for hierarchy level highlighting.
 * @author Michael Lafer
 *
 */


public class LevelHighlightingListener extends AEventListener<GLTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		LevelHighlightingEvent lhevent = (LevelHighlightingEvent) event;
		handler.setHighLightingLevel(lhevent.getHierarchyLevel());
	}

}