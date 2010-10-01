package org.caleydo.view.treemap.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.treemap.GLHierarchicalTreeMap;

public class ZoomOutListener extends AEventListener<GLHierarchicalTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		handler.zoomOut();
	}

}
