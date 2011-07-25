package org.caleydo.core.view.opengl.canvas.remote.receiver;

import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.manager.event.IListenerOwner;

public interface INewDimensionGroupInfoHandler
	extends IListenerOwner {
	public void handleNewDimensionGroupInfo(String eVAType, DimensionGroupList groupList,
		boolean bDeleteTree);

}
