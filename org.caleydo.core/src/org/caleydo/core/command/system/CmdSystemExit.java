package org.caleydo.core.command.system;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command shuts down application.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdSystemExit
	extends ACommand
{
	/**
	 * Constructor.
	 */
	public CmdSystemExit(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		System.exit(0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		// no undo of system shutdown!
	}
}
