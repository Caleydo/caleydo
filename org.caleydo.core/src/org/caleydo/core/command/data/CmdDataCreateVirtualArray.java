package org.caleydo.core.command.data;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command creates a virtual array.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateVirtualArray
	extends ACmdExternalAttributes
{
	/**
	 * Constructor.
	 */
	public CmdDataCreateVirtualArray(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
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
	}
}
