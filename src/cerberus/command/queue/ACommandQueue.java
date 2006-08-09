/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.queue;

import cerberus.command.ICommand;
import cerberus.command.base.ACommand;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACommandQueue 
	extends ACommand
	implements ICommand {

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
	protected ACommandQueue( int iUniqueId, int iCmdQueueId ) {
		super( iUniqueId );
		
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
