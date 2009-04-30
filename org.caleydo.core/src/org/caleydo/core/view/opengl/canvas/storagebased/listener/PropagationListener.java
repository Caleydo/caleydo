package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.view.storagebased.PropagationEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLPropagationHeatMap;

/**
 * Propagation listener for {@link GLHeatMap}
 * @author Werner Puff
 */
public class PropagationListener
	implements IEventListener {

	/** heatmap view this propagation listener is related to */
	GLPropagationHeatMap listHeatMapView = null;

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof PropagationEvent) {
			PropagationEvent propagationEvent = (PropagationEvent) event;
			IVirtualArrayDelta delta = propagationEvent.getVirtualArrayDelta();
			listHeatMapView.handlePropagation(delta);
		}
	}
	
	public GLPropagationHeatMap getHeatMapView() {
		return listHeatMapView;
	}

	public void setHeatMapView(GLPropagationHeatMap heatMapView) {
		this.listHeatMapView = heatMapView;
	}

}
