package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class implements the command for the data exchanger view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateSetEditor
	extends ACmdCreate_IdTargetLabelParentXY
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateSetEditor(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);
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
