/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.command.factory;

import cerberus.command.CommandType;
import cerberus.command.CommandInterface;
import cerberus.command.queue.CommandQueueInterface;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Base class for Command factory.
 * 
 * Design Pattern "Command"
 * 
 * @author Michael Kalkusch
 *
 */
public interface CommandFactoryInterface {

	/**
	 * Set the command type.
	 * 
	 * @param setType
	 * @throws PrometheusCommandException
	 */
	public void setCommandType(CommandType setType) 
		throws CerberusRuntimeException;
	
	
	/**
	 * Get the command type of this class
	 * 
	 * @return command type of this class
	 * @throws PrometheusCommandException
	 * 
	 * @see cerberus.command.CommandInterface.getCommandType()
	 */
	public CommandType getCommandType() 
		throws CerberusRuntimeException;
	
	
	/**
	 * Creates a new command using the information from createCommandByType
	 * 
	 * @param createCommandByType define, which command shall be created
	 * @param details details for command creation
	 * @return new created Command
	 */
	public CommandInterface createCommand( 
			final CommandType createCommandByType, 
			final String details);
	
	/**
	 * @see cerberus.manager.CommandManager#createCommandQueue(String, String, int, int, int, int)
	 */
	public CommandInterface createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait );
	
}
