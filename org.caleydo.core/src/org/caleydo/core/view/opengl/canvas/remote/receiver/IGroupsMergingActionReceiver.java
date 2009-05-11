package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.manager.event.IListenerOwner;

public interface IGroupsMergingActionReceiver
	extends IListenerOwner {

	public void handleMergeGroups(boolean geneGroup);
}
