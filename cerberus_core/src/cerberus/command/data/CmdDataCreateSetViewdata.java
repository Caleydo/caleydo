/**
 * 
 */
package cerberus.command.data;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetParentGLObject;
import cerberus.data.collection.SetDataType;
import cerberus.data.collection.set.viewdata.ISetViewData;
import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.GeneViewRuntimeException;


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
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		ISetManager refISetManager =  refGeneralManager.getSingelton().getSetManager();
		ISetViewData refISetViewData = (ISetViewData) refISetManager.createSet(SetDataType.SET_VIEWCAMERA);
		
		IViewCamera refViewCamera = refISetViewData.getViewCamera();
		
		refViewCamera.setCameraPosition(cameraOrigin);
		refViewCamera.setCameraRotation(cameraRotation);
		
		refViewCamera.setId( iUniqueId );
		
		refISetManager.registerItem(refViewCamera, 
				iUniqueId, 
				ManagerObjectType.SET_VIEWDATA);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		ISetManager refISetManager =  refGeneralManager.getSingelton().getSetManager();
		refISetManager.unregisterItem(iUniqueId, ManagerObjectType.SET_VIEWDATA);

	}

}
