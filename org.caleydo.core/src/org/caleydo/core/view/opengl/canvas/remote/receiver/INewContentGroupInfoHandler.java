package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.manager.event.IListenerOwner;

public interface INewContentGroupInfoHandler
	extends IListenerOwner {

	public void handleNewContentGroupInfo(ContentVAType eVAType, ContentGroupList groupList,
		boolean bDeleteTree);

}
