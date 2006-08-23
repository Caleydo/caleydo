package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.view.CmdViewCreateAdapter;
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
public class CmdViewCreateHeatmap extends CmdViewCreateAdapter implements ICommand 
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
		Object buffer = ( (IViewManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW)
						).createView(ManagerObjectType.VIEW_SWT_HEATMAP2D, 
						iViewId, 
						iParentContainerId, 
						sLabel);
		
		Heatmap2DViewRep heatmapView = (Heatmap2DViewRep) buffer;
		
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
