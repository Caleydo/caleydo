/**
 * 
 */
package org.caleydo.core.command.queue;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.command.queue.ICommandQueue;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager; // import
													// org.caleydo.core.manager
													// .type.ManagerObjectType;
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
	public CmdSystemRunCmdQueue(int iSetCmdId, final IGeneralManager setGeneralManager,
			final ICommandManager setCommandManager,
			final CommandQueueSaxType commandQueueSaxType, final int iCommandQueueId)
	{

		super(iSetCmdId, setGeneralManager, setCommandManager, commandQueueSaxType);

		this.iCommandQueueId = iCommandQueueId;

		setCommandQueueSaxType(CommandQueueSaxType.COMMAND_QUEUE_RUN);
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

}
