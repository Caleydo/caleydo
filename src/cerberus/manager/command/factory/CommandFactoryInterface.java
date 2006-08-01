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
	 * Create a new command.
	 * 
	 * @param sData_Cmd_type
	 * @param sData_Cmd_process
	 * @param iData_CmdId
	 * @param iData_Cmd_MementoId
	 * @param sData_Cmd_detail
	 * @param sData_Cmd_attrbute1
	 * @param sData_Cmd_attrbute2
	 * 
	 * @return new command
	 */
	public CommandInterface createCommand( 
			String sData_Cmd_type,
			String sData_Cmd_process,
			final int iData_CmdId,
			final int iData_Cmd_MementoId,
			String sData_Cmd_detail,
			String sData_Cmd_attrbute1,
			String sData_Cmd_attrbute2 );
	
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
