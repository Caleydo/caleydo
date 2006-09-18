package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreateGui;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a pathway view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreatePathway 
extends ACmdCreateGui 
implements ICommand
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreatePathway( 
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) 
	{
		super(refGeneralManager, refParameterHandler);
	}

	/**
	 * Method creates a pathway view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{				
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		PathwayViewRep pathwayView = (PathwayViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_PATHWAY,
						iUniqueTargetId,
						iParentContainerId, 
						sLabel);
		
		viewManager.registerItem(
				pathwayView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		pathwayView.setAttributes(refParameterHandler);
		
		pathwayView.retrieveGUIContainer();
		pathwayView.initView();
		pathwayView.drawView();
	}
}
