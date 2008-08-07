package org.caleydo.core.command.data;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command creates a virtual array.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateVirtualArray
	extends ACmdCreate_IdTargetLabel
{
	/**
	 * Constructor.
	 */
	public CmdDataCreateVirtualArray(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
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
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabel#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);
	}
}
