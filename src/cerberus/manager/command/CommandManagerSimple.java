/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.command;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
//import java.util.Iterator;

import cerberus.manager.CommandManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.command.factory.CommandFactoryInterface;
import cerberus.manager.singelton.SingeltonManager;
import cerberus.manager.type.ManagerObjectType;


import cerberus.command.CommandInterface;
import cerberus.command.CommandListener;
import cerberus.command.CommandType;
import cerberus.command.queue.CommandQueueInterface;

/**
 * @author Michael Kalkusch
 *
 */
public class CommandManagerSimple 
 extends AbstractManagerImpl 
 implements CommandManager {

	private CommandFactoryInterface refCommandFactory;
	
	/**
	 * List of all Commands to be excecuted as soon as possible
	 */
	private Vector<CommandInterface> vecCmd_handle;
	
	/**
	 * List of All Commands to be executed when sooner or later.
	 */
	private Vector<CommandInterface> vecCmd_schedule;
	
	protected Hashtable<Integer,CommandQueueInterface> hash_CommandQueueId;
	
	protected Hashtable<Integer,CommandInterface> hash_CommandId;
	
	/**
	 * 
	 */
	public CommandManagerSimple( GeneralManager setGeneralManager ) {
		super( setGeneralManager, GeneralManager.iUniqueId_TypeOffset_Command );
		
		refCommandFactory = new CommandFactory( setGeneralManager, 
				this, 
				null );
		
		vecCmd_handle = new Vector<CommandInterface> ();
		
		vecCmd_schedule = new Vector<CommandInterface> ();
		
		hash_CommandQueueId = new Hashtable<Integer,CommandQueueInterface> ();
		
		hash_CommandId = new Hashtable<Integer,CommandInterface> ();
				
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.CommandManager#addCommandListener(cerberus.command.CommandListener)
	 */
	public void addCommandListener(CommandListener addCommandListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.CommandManager#removeCommandListener(cerberus.command.CommandListener)
	 */
	public boolean removeCommandListener(CommandListener removeCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.CommandManager#hasCommandListener(cerberus.command.CommandListener)
	 */
	public boolean hasCommandListener(CommandListener hasCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandActionListener#handleCommand(cerberus.command.CommandInterface)
	 */
	public void handleCommand(CommandInterface addCommand) {
		
		addCommand.doCommand();
		vecCmd_handle.addElement( addCommand );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandActionListener#scheduleCommand(cerberus.command.CommandInterface)
	 */
	public void scheduleCommand(CommandInterface addCommand) {
		
		vecCmd_schedule.addElement( addCommand );
		addCommand.doCommand();
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {
		return hash_CommandId.containsKey( iItemId );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {
		return hash_CommandId.get( iItemId );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#size()
	 */
	public int size() {
		return hash_CommandId.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {
		return ManagerObjectType.COMMAND;
	}



	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#registerItem(java.lang.Object, int, cerberus.data.manager.BaseManagerType)
	 */
	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {
		
		CommandInterface registerCommand = (CommandInterface) registerItem;
		
		if ( registerCommand.getClass().equals( 
				cerberus.command.queue.CommandQueueInterface.class )) {
			hash_CommandQueueId.put( iItemId, (CommandQueueInterface) registerItem );
		} else {
			
		}
		
		vecCmd_handle.addElement( registerCommand );		
		hash_CommandId.put( iItemId, registerCommand );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#unregisterItem(int, cerberus.data.manager.BaseManagerType)
	 */
	public boolean unregisterItem(int iItemId, ManagerObjectType type) {	
		
		//TODO: ensure thread safety! 
		if ( type == ManagerObjectType.CMD_QUEUE ) {
			hash_CommandQueueId.remove( iItemId );
		}
		
		if ( hash_CommandId.containsKey( iItemId ) ) {
		
			CommandInterface unregisterCommand = 
				hash_CommandId.remove( iItemId );
			
			return vecCmd_handle.remove( unregisterCommand );
		}
		
		return false;
	}

	public CommandInterface createCommand( final CommandType useSelectionType, String details ) {
	
		return refCommandFactory.createCommand( useSelectionType, details );
	}
	
	public CommandInterface createCommand( final String useSelectionType ) {
		
		return refCommandFactory.createCommand( CommandType.getType( useSelectionType ), null );
	}
	
	
	/**
	 * Create a new command using the CommandType.
	 * @param details TODO
	 */
	public CommandInterface createCommand( final String  useSelectionType, 
			final LinkedList <String> llAttributes ) {
		
		CommandInterface createdCommand = 
			refCommandFactory.createCommand( useSelectionType,
					llAttributes );	
		
		if ( createdCommand.getId() < 0 ) {
			createdCommand.setId( createNewId( ManagerObjectType.SELECTION_MULTI_BLOCK ) );
		}
		
		registerItem( createdCommand, 
				createdCommand.getId(),
				ManagerObjectType.COMMAND );
		
		return createdCommand;
	}
	
	
//	/* (non-Javadoc)
//	 * @see cerberus.data.manager.GeneralManager#createNewId(cerberus.data.manager.BaseManagerType)
//	 */
//	public int createNewId(ManagerObjectType setNewBaseType) {
//		
//		iUniqueId_current += iUniqueId_Increment;
//		
//		return iUniqueId_current;
//	}

	/*
	 * 
	 */
	public boolean hasCommandQueueId( final int iCmdQueueId ) {
		return hash_CommandQueueId.containsKey( iCmdQueueId );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.CommandManager#getCommandQueueByCmdQueueId(int)
	 */
	public CommandQueueInterface getCommandQueueByCmdQueueId( final int iCmdQueueId ) {
		return hash_CommandQueueId.get( iCmdQueueId );
	}
	
	public CommandInterface createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait ) {
		
		CommandInterface newCmd = 
			refCommandFactory.createCommandQueue( sCmdType,
			sProcessType,
			iCmdId,
			iCmdQueueId,
			sQueueThread,
			sQueueThreadWait );
		
		int iNewCmdId = createNewId( ManagerObjectType.COMMAND );
		newCmd.setId( iNewCmdId );
		
		registerItem( newCmd, iNewCmdId, ManagerObjectType.COMMAND );
		
		return newCmd;
	}
}
