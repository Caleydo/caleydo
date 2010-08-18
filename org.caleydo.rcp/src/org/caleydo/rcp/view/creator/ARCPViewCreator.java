package org.caleydo.rcp.view.creator;

import org.caleydo.core.manager.view.creator.AViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;

public abstract class ARCPViewCreator
	extends AViewCreator {

	public ARCPViewCreator(String viewType) {
		super(viewType);
	}

	public abstract CaleydoRCPViewPart createView(int parentContainerID, String label);

}
