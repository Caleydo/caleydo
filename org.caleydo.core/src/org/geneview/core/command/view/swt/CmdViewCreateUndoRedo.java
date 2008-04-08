package org.geneview.core.command.view.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.undoredo.UndoRedoViewRep;

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
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		UndoRedoViewRep undoRedoView = (UndoRedoViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_UNDO_REDO,
							iUniqueId, 
							iParentContainerId, 							
							sLabel);
		
		viewManager.registerItem(
				undoRedoView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		undoRedoView.setAttributes(iWidthX, iHeightY);
		undoRedoView.initView();
		undoRedoView.drawView();
		
		// Register UNDO/REDO view to command manager.
		generalManager.getSingelton().getCommandManager().
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
