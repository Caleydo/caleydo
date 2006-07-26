/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.queue;

import cerberus.command.CommandInterface;
import cerberus.command.base.AbstractCommand;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AbstractCommandQueue 
	extends AbstractCommand
	implements CommandInterface {

	/**
	 * Commadn QueueId
	 */
	protected int iCmdQueueId;
	
	/**
	 * Set CollectionId using this constructor.
	 * 
	 * @param iSetCmdCollectionId set collection Id
	 */
	protected AbstractCommandQueue( int iSetCmdCollectionId) {
		super( iSetCmdCollectionId );
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
	 * Set a new QueueID.
	 * CommandQueueId = -1 indicates no queueId
	 * 
	 * @param setCmdQueueId new QueueId
	 */
	public final void setCmdQueueId( final int setCmdQueueId ) {
		this.iCmdQueueId = setCmdQueueId;
	}

}
