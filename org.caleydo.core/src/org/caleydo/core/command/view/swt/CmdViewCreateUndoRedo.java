package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;

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
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByObjectType(ManagerObjectType.VIEW));
		
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
		generalManager.getCommandManager().
			addUndoRedoViewRep(undoRedoView);
		
		commandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		commandManager.runUndoCommand(this);
	}
}
