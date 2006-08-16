/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.command.factory;

import java.util.LinkedList;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.queue.ICommandQueue;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Base class for Command factory.
 * 
 * Design Pattern "Command"
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICommandFactory {

	/**
	 * ISet the command type.
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
	 * @see cerberus.command.ICommand.getCommandType()
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
	public ICommand createCommand( 
			final CommandType createCommandByType, 
			final String details);
	
	/**
	 * Create a new command.
	 * 
	 * List of expected Strings inside LinkedList <String>: <br>
	 * sData_CmdId <br>	 
	 * sData_TargetId <br>
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_detail <br>
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * @param sData_Cmd_type
	 * @param llAttributes
	 * 
	 * @return new command
	 */
	public ICommand createCommand( 
			final String sData_Cmd_type,
			final LinkedList <String> llAttributes );

	
	/**
	 * @see cerberus.manager.ICommandManager#createCommandQueue(String, String, int, int, int, int)
	 */
	public ICommand createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait );
	
	
//	public ICommand createCommandQueue( 
//			final String sData_Cmd_type,
//			final LinkedList <String> llAttributes );
}
