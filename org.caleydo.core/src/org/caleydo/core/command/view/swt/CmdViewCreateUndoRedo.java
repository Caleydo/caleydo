package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;

/**
 * Class implements the command for creating the UNDO/REDO GUI view
 * representation.
 * 
 * @author Marc Streit
 */
public class CmdViewCreateUndoRedo
	extends ACmdCreate_IdTargetLabelParentXY
{

	/**
	 * Constructor
	 */
	public CmdViewCreateUndoRedo(final CommandType cmdType)
	{
		super(cmdType);
	}

	/**
	 * Method creates a undo/redo view, sets the attributes and calls the init
	 * and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		UndoRedoViewRep undoRedoView = (UndoRedoViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_UNDO_REDO, iExternalID, iParentContainerId, sLabel);

		viewManager.registerItem(undoRedoView, iExternalID);

		undoRedoView.setAttributes(iWidthX, iHeightY);
		undoRedoView.initView();
		undoRedoView.drawView();

		// Register UNDO/REDO view to command manager.
		generalManager.getCommandManager().addUndoRedoViewRep(undoRedoView);

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
