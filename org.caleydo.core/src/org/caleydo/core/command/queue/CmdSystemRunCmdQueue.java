package org.caleydo.core.command.queue;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.parser.parameter.IParameterHandler;

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
	public CmdSystemRunCmdQueue(final ECommandType cmdType, final int iCommandQueueId)
	{

		super(cmdType);

		this.iCommandQueueId = iCommandQueueId;
	}

	@Override
	public void doCommand()
	{

		ICommandQueue cmdQueue = this.generalManager.getCommandManager()
				.getCommandQueueByCmdQueueId(iCommandQueueId);

		if (cmdQueue == null)
		{
			throw new IllegalStateException(
					"CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}

		cmdQueue.doCommand();
	}

	@Override
	public void undoCommand()
	{

		ICommandQueue cmdQueue = this.generalManager.getCommandManager()
				.getCommandQueueByCmdQueueId(iCommandQueueId);

		if (cmdQueue == null)
		{
			throw new IllegalStateException(
					"CmdSystemRunCmdQueue::doCommand() cmdQueue==null !");
		}

		cmdQueue.doCommand();
	}

	@Override
	public void setParameterHandler(IParameterHandler phHandler)
	{
		// not yet implemented
	}
}
