package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.filterpipeline.RcpGLFilterPipelineView;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Listener reacting on filter updates.
 * 
 * @author Marc Streit
 */
public class FilterUpdateListener
	extends AEventListener<RcpGLFilterPipelineView> {

	@Override
	public void handleEvent(AEvent event) {
		handler.handleFilterUpdatedEvent();
	}
}
