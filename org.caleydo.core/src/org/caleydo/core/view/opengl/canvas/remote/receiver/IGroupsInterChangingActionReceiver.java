package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.manager.event.IListenerOwner;

public interface IGroupsInterChangingActionReceiver  extends IListenerOwner{

	public void handleInterchangeGroups(boolean geneGroup);
}
