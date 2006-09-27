package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a pathway view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreatePathway 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreatePathway( 
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) {
		
		super(refGeneralManager, refParameterHandler);
	}

	/**
	 * Method creates a pathway view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {	
		
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

		pathwayView.readInAttributes(refParameterHandler);
		
		pathwayView.retrieveGUIContainer();
		pathwayView.initView();
		pathwayView.drawView();
	}

	public void undoCommand() throws CerberusRuntimeException {
		
		// TODO Auto-generated method stub
		
	}
}
