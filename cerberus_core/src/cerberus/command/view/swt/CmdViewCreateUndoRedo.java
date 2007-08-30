package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.undoredo.UndoRedoViewRep;

/**
 * Class implementes the command for creating
 * the UNDO/REDO GUI view representation.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateUndoRedo 
extends ACmdCreate_IdTargetLabelParentXY {
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateUndoRedo(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a undo/redo view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		UndoRedoViewRep undoRedoView = (UndoRedoViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_UNDO_REDO,
							iUniqueId, 
							iParentContainerId, 							
							iGlForwarderId,
							sLabel);
		
		viewManager.registerItem(
				undoRedoView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		undoRedoView.setAttributes(iWidthX, iHeightY);
		undoRedoView.initView();
		undoRedoView.drawView();
		
		// Register UNDO/REDO view to command manager.
		refGeneralManager.getSingelton().getCommandManager().
			addUndoRedoViewRep(undoRedoView);
		
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
