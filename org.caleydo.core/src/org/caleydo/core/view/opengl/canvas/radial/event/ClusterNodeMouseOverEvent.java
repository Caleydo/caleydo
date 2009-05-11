package org.caleydo.core.view.opengl.canvas.radial.event;

import org.caleydo.core.manager.event.AEvent;

public class ClusterNodeMouseOverEvent
	extends AEvent {

	private int iClusterNumber = -1;

	public int getClusterNumber() {
		return iClusterNumber;
	}

	public void setClusterNumber(int iClusterNumber) {
		this.iClusterNumber = iClusterNumber;
	}

	@Override
	public boolean checkIntegrity() {
		if (iClusterNumber == -1)
			throw new IllegalStateException("iClusterNumber was not set");
		return true;
	}

}
