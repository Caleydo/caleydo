package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.data.selection.StorageGroupList;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.manager.event.IListenerOwner;

public interface INewStorageGroupInfoHandler
	extends IListenerOwner {
	public void handleNewStorageGroupInfo(StorageVAType eVAType, StorageGroupList groupList,
		boolean bDeleteTree);

}
