package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.manager.event.IListenerOwner;

public interface INewContentGroupInfoHandler
	extends IListenerOwner {

	public void handleNewContentGroupInfo(String eVAType, ContentGroupList groupList, boolean bDeleteTree);

}
