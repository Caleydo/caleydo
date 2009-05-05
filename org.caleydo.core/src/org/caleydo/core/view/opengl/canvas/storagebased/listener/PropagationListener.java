package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.PropagationEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLPropagationHeatMap;

/**
 * Propagation listener for {@link GLHeatMap}
 * @author Werner Puff
 */
public class PropagationListener
	extends AEventListener<GLPropagationHeatMap> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof PropagationEvent) {
			PropagationEvent propagationEvent = (PropagationEvent) event;
			IVirtualArrayDelta delta = propagationEvent.getVirtualArrayDelta();
			handler.handlePropagation(delta);
		}
	}

}
