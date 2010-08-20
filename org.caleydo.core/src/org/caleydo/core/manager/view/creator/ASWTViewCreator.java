package org.caleydo.core.manager.view.creator;

import org.caleydo.core.view.AView;

public abstract class ASWTViewCreator
	extends AViewCreator {
	
	public ASWTViewCreator(String viewType) {
		super(viewType);
	}

	public abstract AView createView(int parentContainerID, String label);
}
