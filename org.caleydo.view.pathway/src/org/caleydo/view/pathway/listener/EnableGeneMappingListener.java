package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;

public class EnableGeneMappingListener extends APathwayListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.enableGeneMapping(true);
	}

}
