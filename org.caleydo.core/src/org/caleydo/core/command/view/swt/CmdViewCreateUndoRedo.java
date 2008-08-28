package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;

/**
 * Class implements the command for creating the UNDO/REDO GUI view
 * representation.
 * 
 * @author Marc Streit
 */
public class CmdViewCreateUndoRedo
	extends ACmdExternalAttributes
{

	/**
	 * Constructor
	 */
	public CmdViewCreateUndoRedo(final ECommandType cmdType)
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

		if (iExternalID != -1)
		{
			iParentContainerId = generalManager.getIDManager().getInternalFromExternalID(
					iParentContainerId);
		}

		UndoRedoViewRep undoRedoView = (UndoRedoViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_UNDO_REDO, iParentContainerId, sLabel);

		viewManager.registerItem(undoRedoView);

		undoRedoView.initView();
		undoRedoView.drawView();

		// Register UNDO/REDO view to command manager.
		generalManager.getCommandManager().addUndoRedoViewRep(undoRedoView);

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(undoRedoView.getID(),
					iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
