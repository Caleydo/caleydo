/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import cerberus.command.CommandQueueSaxType;
//import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.ICommandListener;
import cerberus.command.ICommandActionListener;
import cerberus.command.queue.ICommandQueue;
import cerberus.view.swt.undoredo.UndoRedoViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;
//import prometheus.data.xml.MementoXML;

/**
 * One Manager handle all ICommandListener.
 * 
 * This is a singelton for all Commands and ICommandListener objects. 
 * "ISingelton" Design Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICommandManager 
extends ICommandActionListener, IGeneralManager {

	//public ICommand createCommand( final ManagerObjectType useSelectionType );
	
//	/**
//	 * Create a new command using a String.
//	 */
//	public ICommand createCommand( final String useSelectionType );
	
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
	public ICommand createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait );
	

	/**
	 * create a new command. Calls createCommandByType(CommandQueueSaxType) internal.
	 * 
	 * @see cerberus.manager.ICommandManager#createCommandByType(CommandQueueSaxType)
	 * 
	 * @param phAttributes Define several attributes and assign them in new Command
	 * @return new Command with attributes defined in phAttributes
	 */
	public ICommand createCommand( final IParameterHandler phAttributes );
	
	
	/**
	 *  Create a new command using the CommandQueueSaxType.
	 *  
	 * @param cmdType
	 * @return
	 */
	public ICommand createCommandByType(final CommandQueueSaxType cmdType);
	
//	public ICommand createCommand(final CommandType cmdType, 
//			String details);

	
	/**
	 * Add reference to one ICommandListener object.
	 * 
	 * @param addCommandListener adds referenc to ICommandListener obejct.
	 */
	public void addCommandListener( ICommandListener addCommandListener );
	
	/**
	 * Remove reference to one ICommandListener object.
	 * 
	 * @param removeCommandListener removes referens to ICommandListener obejct.
	 * @return TRUE if the reference was removed, false if the reference was not found.
	 */
	public boolean removeCommandListener( ICommandListener removeCommandListener );
	
	/**
	 * Tests if the reference to one ICommandListener object exists.
	 * 
	 * @param hasCommandListener referenc to be tested
	 * @return true if the reference is bound to this ICommandManager
	 */
	public boolean hasCommandListener( ICommandListener hasCommandListener );	

	/**
	 * Get a command queue by it's commandQueueId, which is only a key for the commandQueue
	 * and is nopt a uniqueSystem wide Id.
	 * 
	 * @param iCmdQueueId commandQueueId
	 * @return command queue
	 */
	public ICommandQueue getCommandQueueByCmdQueueId( final int iCmdQueueId );
	
	/**
	 * Tests if a iCmdQueueId is registered with a CommandQueue obejct.
	 * 
	 * @param iCmdQueueId test this id
	 * @return TRUE if an CommandQueue is bound that iCmdQueueId
	 */
	public boolean hasCommandQueueId( final int iCmdQueueId );
	
	/**
	 * Register a cerberus.command.ICommand after it's doCommand() method was called.
	 * Used for redo-undo.
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 * 
	 * @param runCmd
	 */
	public void runDoCommand( ICommand runCmd );
	
	/**
	 * Register a cerberus.command.ICommand after it's undoCommand() method was called.
	 * Used for redo-undo.
	 * 
	 * @see cerberus.command.ICommand#undoCommand()
	 * 
	 * @param runCmd
	 */
	public void runUndoCommand( ICommand runCmd );
	
	public void addUndoRedoViewRep ( UndoRedoViewRep refUndoRedoViewRep);
	
}
