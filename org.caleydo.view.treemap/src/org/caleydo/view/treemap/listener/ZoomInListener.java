package org.caleydo.view.treemap.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.treemap.GLTreeMap;

public class ZoomInListener extends AEventListener<GLTreeMap> {

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub
		handler.zoomIn();
	}

}
