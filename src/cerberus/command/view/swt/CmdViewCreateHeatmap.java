package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.heatmap.jogl.Heatmap2DViewRep;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateHeatmap 
extends ACmdCreate 
implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateHeatmap( 
			IGeneralManager refGeneralManager,
			final LinkedList <String> listAttributes) 
	{
		super(refGeneralManager, listAttributes);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		Heatmap2DViewRep heatmapView = (Heatmap2DViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_HEATMAP2D,
						iCreatedObjectId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				heatmapView, 
				iCreatedObjectId, 
				ManagerObjectType.VIEW);
		
		heatmapView.setAttributes(refVecAttributes);
		heatmapView.extractAttributes();
		heatmapView.retrieveGUIContainer();
		heatmapView.initView();
		heatmapView.drawView();
		
//		GearsViewRep gearsView = (GearsViewRep) ((IViewManager)refGeneralManager.
//				getManagerByBaseType(ManagerObjectType.VIEW)).
//					createView(ManagerObjectType.VIEW_SWT_GEARS, 
//							iViewId, iParentContainerId, sLabel);
//		
//		gearsView.setAttributes(refVecAttributes);
//		gearsView.extractAttributes();
//		gearsView.retrieveGUIContainer();
//		gearsView.initView();
//		gearsView.drawView();
	}
}
