package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class implements the command for the data exchanger view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateSetEditor
	extends ACmdExternalAttributes
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateSetEditor(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{

		// IViewManager viewManager = ((IViewManager) generalManager
		// .getManagerByObjectType(ManagerObjectType.VIEW));
		//		
		// NewSetEditorViewRep setEditorView = (NewSetEditorViewRep)viewManager
		// .createView(ManagerObjectType.VIEW_SWT_DATA_SET_EDITOR,
		// iUniqueID,
		// iParentContainerId,
		// sLabel);
		//		
		// viewManager.registerItem(
		// setEditorView,
		// iUniqueID);
		//
		// viewManager.addViewRep(setEditorView);
		//
		// setEditorView.setAttributes(iWidthX, iHeightY);
		// setEditorView.initView();
		// setEditorView.drawView();
		//		
		// commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
