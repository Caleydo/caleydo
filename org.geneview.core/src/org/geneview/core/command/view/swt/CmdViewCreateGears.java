package org.geneview.core.command.view.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.jogl.gears.GearsViewRep;

/**
 * Class implementes the command for creating a gears view.
 * 
 * @see org.geneview.core.command.ICommand
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateGears 
extends ACmdCreate_IdTargetLabelParentAttrOpenGL  {
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateGears(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a gears view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		GearsViewRep gearsView = (GearsViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_GEARS,
						iUniqueId, 
						iParentContainerId,
						sLabel);
		
		viewManager.registerItem(
				gearsView, 
				iUniqueId, 
				ManagerObjectType.VIEW);
		
		gearsView.setAttributes(iWidthX, iHeightY);
		gearsView.initView();
		gearsView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {

		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);
	}
	
	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
