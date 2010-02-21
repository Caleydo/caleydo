package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.manager.event.IListenerOwner;

public interface IStorageGroupsActionHandler
	extends IListenerOwner {

	public void handleMergeStorageGroups();

	public void handleExportStorageGroups();

	// public void handleInterchangeGroups(boolean geneGroup);
	public void handleInterchangeStorageGroups();

}
