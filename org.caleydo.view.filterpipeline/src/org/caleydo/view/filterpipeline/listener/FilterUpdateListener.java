package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.filterpipeline.GLFilterPipeline;

/**
 * Listener reacting on filter updates.
 * 
 * @author Marc Streit
 */
public class FilterUpdateListener
	extends AEventListener<GLFilterPipeline> {

	@Override
	public void handleEvent(AEvent event) {
		handler.updateFilterPipeline();
	}
}
