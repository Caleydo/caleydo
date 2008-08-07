package org.caleydo.core.command.queue;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Runs a command queue.
 * 
 * @author Michael Kalkusch
 */
public class CmdSystemRunCmdQueue
	extends ACommand
{
	protected int iCommandQueueId;

	/**
	 * Constructor.
	 */
	public CmdSystemRunCmdQueue(final CommandType cmdType, final int iCommandQueueId)
	{

		super(cmdType);

		this.iCommandQueueId = iCommandQueueId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		ICommandQueue cmdQueue = this.generalManager.getCommandManager()
				.getCommandQueueByCmdQueueId(iCommandQueueId);

		if (cmdQueue == null)
		{
			throw new CaleydoRuntimeException(
					"CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}

		cmdQueue.doCommand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{

		ICommandQueue cmdQueue = this.generalManager.getCommandManager()
				.getCommandQueueByCmdQueueId(iCommandQueueId);

		if (cmdQueue == null)
		{
			throw new CaleydoRuntimeException(
					"CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}

		cmdQueue.doCommand();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACommand#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(IParameterHandler phHandler)
	{
		// not yet implemented
	}
}
