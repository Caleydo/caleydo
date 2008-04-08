package org.caleydo.core.command.queue;

import org.caleydo.core.command.ICommand;
import org.caleydo.core.parser.ascii.IParserObject;

public interface ICommandQueue 
extends ICommand, IParserObject {

	/**
	 * Get CommandQueueId.
	 * -1 indicates no CommandQueueId.
	 * 
	 * @return CommandQueueId
	 */
	public int getCmdQueueId();

	/**
	 * ISet a new QueueID.
	 * CommandQueueId = -1 indicates no queueId
	 * 
	 * @param setCmdQueueId new QueueId
	 */
	public void setCmdQueueId( final int setCmdQueueId );

	/**
	 * Check is QueueID is set.
	 * 
	 * @return TRUE if queueId is set
	 */
	public boolean containsCmdQueueId( final int testCmdQueueId );
	
	/**
	 * Add a new command.
	 * 
	 * @param cmdItem add command
	 */
	public boolean addCmdToQueue( final ICommand cmdItem );
	
	/**
	 * Remove a new command.
	 * 
	 * @param cmdItem remove command
	 */
	public boolean removeCmdFromQueue( final ICommand cmdItem );
	
	/**
	 * Contains a command.
	 * 
	 * @param cmdItem test if command is contained in command queue
	 */
	public boolean containsCmdInQueue( final ICommand cmdItem );

}