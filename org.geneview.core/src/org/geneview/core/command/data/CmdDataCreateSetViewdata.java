/**
 * 
 */
package org.geneview.core.command.data;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetParentGLObject;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.viewdata.ISetViewData;
import org.geneview.core.data.view.camera.IViewCamera;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.data.ISetManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.util.exception.GeneViewRuntimeException;


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
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		ISetManager refISetManager =  refGeneralManager.getSingelton().getSetManager();
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
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		ISetManager refISetManager =  refGeneralManager.getSingelton().getSetManager();
		refISetManager.unregisterItem(iUniqueId, ManagerObjectType.SET_VIEWDATA);

	}

}
