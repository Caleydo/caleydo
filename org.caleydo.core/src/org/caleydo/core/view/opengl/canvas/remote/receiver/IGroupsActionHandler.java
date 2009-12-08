package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.manager.event.IListenerOwner;

public interface IGroupsActionHandler
	extends IListenerOwner {

	public void handleMergeGroups(boolean geneGroup);

	public void handleExportGroups(boolean geneGroup);

	public void handleInterchangeGroups(boolean geneGroup);
}
