/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import cerberus.command.CommandInterface;
import cerberus.command.CommandListener;
import cerberus.command.CommandType;
import cerberus.command.CommandActionListener;
import cerberus.command.queue.CommandQueueInterface;
//import prometheus.data.xml.MementoXML;

/**
 * One Manager handle all CommandListener.
 * 
 * This is a singelton for all Commands and CommandListener objects. 
 * "Singelton" Design Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public interface CommandManager 
extends CommandActionListener, GeneralManager {

	//public CommandInterface createCommand( final ManagerObjectType useSelectionType );
	
	/**
	 * Create a new command using a String.
	 */
	public CommandInterface createCommand( final String useSelectionType );
	
	/**
	 * Create a new CommandQueue object.
	 * 
	 * @param sCmdType type of command
	 * @param sProcessType define how to process queue
	 * @param iCmdId unique CmdId
	 * @param iCmdQueueId unique commandQueueId, must not be global unique!
	 * @param sQueueThread define a thread pool, default = -1 means no thread pool
	 * @param sQueueThreadWait define depandent thread pool, default = -1 means no dependency on other thread to finish
	 * 
	 * @return new commandQueue
	 */
	public CommandInterface createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait );
	
	/**
	 * Create a new command using the CommandType.
	 * @param details TODO
	 */
	public CommandInterface createCommand( final CommandType useSelectionType, String details );
	
	/**
	 * Add reference to one CommandListener object.
	 * 
	 * @param addCommandListener adds referenc to CommandListener obejct.
	 */
	public void addCommandListener( CommandListener addCommandListener );
	
	/**
	 * Remove reference to one CommandListener object.
	 * 
	 * @param removeCommandListener removes referens to CommandListener obejct.
	 * @return TRUE if the reference was removed, false if the reference was not found.
	 */
	public boolean removeCommandListener( CommandListener removeCommandListener );
	
	/**
	 * Tests if the reference to one CommandListener object exists.
	 * 
	 * @param hasCommandListener referenc to be tested
	 * @return true if the reference is bound to this CommandManager
	 */
	public boolean hasCommandListener( CommandListener hasCommandListener );	

	/**
	 * Get a command queue by it's commandQueueId, which is only a key for the commandQueue
	 * and is nopt a uniqueSystem wide Id.
	 * 
	 * @param iCmdQueueId commandQueueId
	 * @return command queue
	 */
	public CommandQueueInterface getCommandQueueByCmdQueueId( final int iCmdQueueId );
	
	/**
	 * Tests if a iCmdQueueId is registered with a CommandQueue obejct.
	 * 
	 * @param iCmdQueueId test this id
	 * @return TRUE if an CommandQueue is bound that iCmdQueueId
	 */
	public boolean hasCommandQueueId( final int iCmdQueueId );
	
}
