
package org.caleydo.core.view.opengl.canvas.radial.event;


import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;

public class ClusterNodeMouseOverListener
	implements IEventListener {
	
	private IClusterNodeEventReceiver eventReceiver;

	public ClusterNodeMouseOverListener(IClusterNodeEventReceiver eventReceiver) {	
		this.eventReceiver = eventReceiver;
	}
	
	@Override
	public void handleEvent(AEvent event) {
		
		if (event instanceof ClusterNodeMouseOverEvent) {
			ClusterNodeMouseOverEvent mouseOverEvent = (ClusterNodeMouseOverEvent) event;
			eventReceiver.handleMouseOver(mouseOverEvent.getClusterNodeName());
		}
	}

	public IClusterNodeEventReceiver getEventReceiver() {
		return eventReceiver;
	}

	public void setEventReceiver(IClusterNodeEventReceiver eventReceiver) {
		this.eventReceiver = eventReceiver;
	}
}
