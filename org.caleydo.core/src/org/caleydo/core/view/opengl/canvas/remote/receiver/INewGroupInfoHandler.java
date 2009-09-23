package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

public interface INewGroupInfoHandler
	extends IListenerOwner {

	public void handleNewGroupInfo(EVAType eVAType, GroupList groupList, boolean bDeleteTree);

}
