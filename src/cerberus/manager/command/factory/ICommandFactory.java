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
import cerberus.command.ICommand;
//import cerberus.command.queue.ICommandQueue;
import cerberus.xml.parser.parameter.IParameterHandler;

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
	 * create a new command
	 * 
	 * @param phAttributes
	 * @return
	 */
	public ICommand createCommand(final IParameterHandler phAttributes);
	
	/**
	 * @see cerberus.manager.ICommandManager#createCommandQueue(String, String, int, int, int, int)
	 */
	public ICommand createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait );
	
}
