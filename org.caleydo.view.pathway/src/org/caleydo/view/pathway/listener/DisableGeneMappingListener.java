package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;

public class DisableGeneMappingListener extends APathwayListener {

	@Override
	public void handleEvent(AEvent event) {
		handler.enableGeneMapping(false);
	}

}
