package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.manager.event.IListenerOwner;

public interface INewGroupInfoHandler
	extends IListenerOwner {

	public void handleNewGroupInfo(EVAType eVAType, GroupList groupList, boolean bDeleteTree);

}
