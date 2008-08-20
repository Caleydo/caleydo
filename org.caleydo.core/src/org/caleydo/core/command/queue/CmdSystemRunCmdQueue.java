package org.caleydo.core.command.queue;

import org.caleydo.core.command.ECommandType;
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
	public CmdSystemRunCmdQueue(final ECommandType cmdType, final int iCommandQueueId)
	{

		super(cmdType);

		this.iCommandQueueId = iCommandQueueId;
	}

	@Override
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

	@Override
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

	@Override
	public void setParameterHandler(IParameterHandler phHandler)
	{
		// not yet implemented
	}
}
