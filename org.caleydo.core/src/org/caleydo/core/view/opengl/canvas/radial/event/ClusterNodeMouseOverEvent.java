package org.caleydo.core.view.opengl.canvas.radial.event;

import org.caleydo.core.manager.event.AEvent;

public class ClusterNodeMouseOverEvent
	extends AEvent {

	private String sClusterNodeName;

	public String getClusterNodeName() {
		return sClusterNodeName;
	}

	public void setClusterNodeName(String sClusterNodeName) {
		this.sClusterNodeName = sClusterNodeName;
	}
	
}
