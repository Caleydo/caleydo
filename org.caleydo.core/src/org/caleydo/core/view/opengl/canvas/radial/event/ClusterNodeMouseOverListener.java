
package org.caleydo.core.view.opengl.canvas.radial.event;


import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

public class ClusterNodeMouseOverListener
	extends AEventListener<IClusterNodeEventReceiver> {
	
	@Override
	public void handleEvent(AEvent event) {
		
		if (event instanceof ClusterNodeMouseOverEvent) {
			ClusterNodeMouseOverEvent mouseOverEvent = (ClusterNodeMouseOverEvent) event;
			handler.handleMouseOver(mouseOverEvent.getClusterNumber());
		}
	}

}
