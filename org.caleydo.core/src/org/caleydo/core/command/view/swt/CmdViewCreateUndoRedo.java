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
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		super(generalManager, 
				commandManager,
				commandQueueSaxType);
	}

	/**
	 * Method creates a undo/redo view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = generalManager.getViewGLCanvasManager();
		
		UndoRedoViewRep undoRedoView = (UndoRedoViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_UNDO_REDO,
							iUniqueId, 
							iParentContainerId, 							
							sLabel);
		
		viewManager.registerItem(
				undoRedoView, 
				iUniqueId);

		undoRedoView.setAttributes(iWidthX, iHeightY);
		undoRedoView.initView();
		undoRedoView.drawView();
		
		// Register UNDO/REDO view to command manager.
		generalManager.getCommandManager().
			addUndoRedoViewRep(undoRedoView);
		
		commandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		assert parameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(parameterHandler);	
	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		commandManager.runUndoCommand(this);
	}
}
