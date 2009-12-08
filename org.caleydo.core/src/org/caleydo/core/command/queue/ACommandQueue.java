package org.caleydo.core.command.queue;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;

/**
 * @author Michael Kalkusch
 */
public abstract class ACommandQueue
	extends ACommand {

	/**
	 * Command QueueId
	 */
	protected int iCmdQueueId;

	/**
	 * ISet CollectionId using this constructor.
	 * 
	 * @param iCmdQueueId
	 *            define cmd queue by this id
	 */
	protected ACommandQueue(final ECommandType cmdType, final int iCmdQueueId) {
		super(cmdType);

		this.iCmdQueueId = iCmdQueueId;
	}

	/**
	 * Get CommandQueueId. -1 indicates no CommandQueueId.
	 * 
	 * @return CommandQueueId
	 */
	public final int getCmdQueueId() {

		return iCmdQueueId;
	}

	/**
	 * ISet a new QueueID. CommandQueueId = -1 indicates no queueId
	 * 
	 * @param setCmdQueueId
	 *            new QueueId
	 */
	public final void setCmdQueueId(final int setCmdQueueId) {

		this.iCmdQueueId = setCmdQueueId;
	}

}
