package org.caleydo.core.view.opengl.canvas.radial.event;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.manager.event.AEvent;

public class ClusterNodeSelectionEvent
	extends AEvent {

	private int iClusterNumber = -1;
	private ESelectionType selectionType;

	public int getClusterNumber() {
		return iClusterNumber;
	}

	public void setClusterNumber(int iClusterNumber) {
		this.iClusterNumber = iClusterNumber;
	}
	
	public void setSelectionType(ESelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public ESelectionType getSelectionType() {
		return selectionType;
	}

	@Override
	public boolean checkIntegrity() {
		if (iClusterNumber == -1)
			throw new IllegalStateException("iClusterNumber was not set");
		return true;
	}

	

}
