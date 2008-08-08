package org.caleydo.core.command.window.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command class triggers the creation of a SWT GUI window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class CmdWindowCreate
	extends ACmdCreate_IdTargetLabelAttrDetail
{
	protected String sLayoutAttributes;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdWindowCreate(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		int iShellID = generalManager.getSWTGUIManager().createWindow(sLabel, sLayoutAttributes);

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(iShellID, iExternalID);
		}
		
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		sLayoutAttributes = sAttribute1;
	}
}
