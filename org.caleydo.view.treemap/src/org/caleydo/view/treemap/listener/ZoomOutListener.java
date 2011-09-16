package org.caleydo.view.treemap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.treemap.GLHierarchicalTreeMap;

/**
 * Listener for zoom out function.
 * @author Michael Lafer
 *
 */

public class ZoomOutListener extends AEventListener<GLHierarchicalTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		handler.zoomOut();
	}

}
