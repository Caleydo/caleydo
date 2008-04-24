/**
 * 
 */
package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetParentGLObject;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.set.viewdata.ISetViewData;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;


/**
 * @author Michael Kalkusch
 *
 */
public class CmdDataCreateSetViewdata
extends ACmdCreate_IdTargetParentGLObject {

	/**
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdDataCreateSetViewdata(IGeneralManager refGeneralManager,
			ICommandManager refCommandManager,
			CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {

		ISetManager refISetManager =  generalManager.getSingleton().getSetManager();
		ISetViewData refISetViewData = (ISetViewData) refISetManager.createSet(SetType.SET_VIEW_DATA);
		
		IViewCamera refViewCamera = refISetViewData.getViewCamera();
		
		refViewCamera.setCameraPosition(cameraOrigin);
		refViewCamera.setCameraRotation(cameraRotation);
		
		refViewCamera.setId( iUniqueId );
		
		refISetManager.registerItem(refViewCamera, 
				iUniqueId, 
				ManagerObjectType.SET_VIEWDATA);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {

		ISetManager refISetManager =  generalManager.getSingleton().getSetManager();
		refISetManager.unregisterItem(iUniqueId, ManagerObjectType.SET_VIEWDATA);

	}

}
