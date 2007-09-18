/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.queue;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACommand;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACommandQueue 
	extends ACommand {

	/**
	 * Commadn QueueId
	 */
	protected int iCmdQueueId;
	
	/**
	 * ISet CollectionId using this constructor.
	 * 
	 * @param iUniqueId unique system-wide Id
	 * @param iCmdQueueId define cmd queue by this id
	 */
	protected ACommandQueue(final int iUniqueId,
			final IGeneralManager refGeneralManager,	
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final int iCmdQueueId) {
		
		super( iUniqueId,
				refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		this.iCmdQueueId = iCmdQueueId;
	}

	/**
	 * Get CommandQueueId.
	 * -1 indicates no CommandQueueId.
	 * 
	 * @return CommandQueueId
	 */
	public final int getCmdQueueId() {
		return iCmdQueueId;
	}

	/**
	 * ISet a new QueueID.
	 * CommandQueueId = -1 indicates no queueId
	 * 
	 * @param setCmdQueueId new QueueId
	 */
	public final void setCmdQueueId( final int setCmdQueueId ) {
		this.iCmdQueueId = setCmdQueueId;
	}

}
