package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.event.IListenerOwner;

public interface IContentGroupsActionHandler
	extends IListenerOwner {

	public void handleMergeContentGroups();

	public void handleExportContentGroups();

	// public void handleInterchangeGroups(boolean geneGroup);
	public void handleInterchangeContentGroups();

}
