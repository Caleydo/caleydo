package cerberus.command.queue;

import cerberus.command.CommandInterface;

public interface CommandQueueInterface {

	/**
	 * Get CommandQueueId.
	 * -1 indicates no CommandQueueId.
	 * 
	 * @return CommandQueueId
	 */
	public int getCmdQueueId();

	/**
	 * Set a new QueueID.
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
	public void addCmdToQueue( final CommandInterface cmdItem );
	
	/**
	 * Remove a new command.
	 * 
	 * @param cmdItem remove command
	 */
	public boolean removeCmdFromQueue( final CommandInterface cmdItem );
	
	/**
	 * Contains a command.
	 * 
	 * @param cmdItem test if command is contained in command queue
	 */
	public boolean containsCmdInQueue( final CommandInterface cmdItem );

}