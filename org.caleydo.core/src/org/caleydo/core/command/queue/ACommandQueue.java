package org.caleydo.core.command.queue;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;

/**
 * @author Michael Kalkusch
 */
public abstract class ACommandQueue
	extends ACommand
{

	/**
	 * Commadn QueueId
	 */
	protected int iCmdQueueId;

	/**
	 * ISet CollectionId using this constructor.
	 * 
	 * @param iUniqueId
	 *            unique system-wide Id
	 * @param iCmdQueueId
	 *            define cmd queue by this id
	 */
	protected ACommandQueue(final int iUniqueId, final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType, final int iCmdQueueId)
	{

		super(iUniqueId, generalManager, commandManager, commandQueueSaxType);

		this.iCmdQueueId = iCmdQueueId;
	}

	/**
	 * Get CommandQueueId. -1 indicates no CommandQueueId.
	 * 
	 * @return CommandQueueId
	 */
	public final int getCmdQueueId()
	{

		return iCmdQueueId;
	}

	/**
	 * ISet a new QueueID. CommandQueueId = -1 indicates no queueId
	 * 
	 * @param setCmdQueueId
	 *            new QueueId
	 */
	public final void setCmdQueueId(final int setCmdQueueId)
	{

		this.iCmdQueueId = setCmdQueueId;
	}

}
