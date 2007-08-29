package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.jogl.gears.GearsViewRep;

/**
 * Class implementes the command for creating a gears view.
 * 
 * @see cerberus.command.ICommand
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
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		GearsViewRep gearsView = (GearsViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_GEARS,
						iUniqueTargetId, 
							iParentContainerId, 
							sLabel,
							iGLCanvasId,
							iGLEventListernerId);
		
		viewManager.registerItem(
				gearsView, 
				iUniqueTargetId, 
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
