package org.caleydo.core.view.opengl.canvas.radial.event;

import org.caleydo.core.manager.event.AEvent;

public class ClusterNodeMouseOverEvent
	extends AEvent {

	private int iClusterNumber;

	public int getClusterNumber() {
		return iClusterNumber;
	}

	public void setClusterNumber(int iClusterNumber) {
		this.iClusterNumber = iClusterNumber;
	}
	
}
