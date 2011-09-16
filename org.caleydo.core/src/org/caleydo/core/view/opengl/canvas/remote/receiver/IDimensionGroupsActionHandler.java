package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.event.IListenerOwner;

public interface IDimensionGroupsActionHandler
	extends IListenerOwner {

	public void handleMergeDimensionGroups();

	public void handleExportDimensionGroups();

	// public void handleInterchangeGroups(boolean geneGroup);
	public void handleInterchangeDimensionGroups();

}
