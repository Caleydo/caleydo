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
	 * List of All Commands to be executed when soonr or later.
	 */
	private Vector<CommandInterface> vecCmd_schedule;
	
	protected Hashtable<Integer,CommandQueueInterface> hash_CommandQueue;
	
	/**
	 * 
	 */
	public CommandManagerSimple( GeneralManager setGeneralManager ) {
		super( setGeneralManager );
		
		refCommandFactory = new CommandFactory( setGeneralManager, null );
		
		vecCmd_handle = new Vector<CommandInterface> ();
		
		vecCmd_schedule = new Vector<CommandInterface> ();
		
		hash_CommandQueue = new Hashtable<Integer,CommandQueueInterface> ();
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#unregisterItem(int, cerberus.data.manager.BaseManagerType)
	 */
	public boolean unregisterItem(int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public CommandInterface createCommand( final CommandType useSelectionType, String details ) {
	
		return refCommandFactory.createCommand( useSelectionType, details );
	}
	
	public CommandInterface createCommand( final String useSelectionType ) {
		
		return refCommandFactory.createCommand( CommandType.getType( useSelectionType ), null );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#createNewId(cerberus.data.manager.BaseManagerType)
	 */
	public int createNewId(ManagerObjectType setNewBaseType) {
		// TODO Auto-generated method stub
		
		return 0;
	}

	/*
	 * 
	 */
	public boolean hasCommandQueueId( final int iCmdQueueId ) {
		return hash_CommandQueue.containsKey( iCmdQueueId );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.CommandManager#getCommandQueueByCmdQueueId(int)
	 */
	public CommandQueueInterface getCommandQueueByCmdQueueId( final int iCmdQueueId ) {
		return hash_CommandQueue.get( iCmdQueueId );
	}
}
