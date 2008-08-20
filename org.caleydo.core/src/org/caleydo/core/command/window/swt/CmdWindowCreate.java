package org.caleydo.core.command.window.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command class triggers the creation of a SWT GUI window.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class CmdWindowCreate
	extends ACmdExternalAttributes
{
	protected String sLayoutAttributes;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdWindowCreate(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		int iShellID = generalManager.getSWTGUIManager().createWindow(sLabel, sLayoutAttributes);

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(iShellID, iExternalID);
		}
		
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		sLayoutAttributes = sAttribute1;
	}
}
