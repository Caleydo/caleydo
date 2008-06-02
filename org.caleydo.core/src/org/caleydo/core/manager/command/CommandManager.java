package org.caleydo.core.manager.command;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.ICommandListener;
import org.caleydo.core.command.queue.ICommandQueue;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.command.factory.CommandFactory;
import org.caleydo.core.manager.command.factory.ICommandFactory;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;

/**
 * @author Michael Kalkusch
 *
 */
public class CommandManager 
extends AManager 
implements ICommandManager {

	private ICommandFactory commandFactory;
	
	/**
	 * List of all Commands to be excecuted as soon as possible
	 */
	private Vector<ICommand> vecCmd_handle;
	
	/**
	 * List of All Commands to be executed when sooner or later.
	 */
	private Vector<ICommand> vecCmd_schedule;
	
	protected Hashtable<Integer,ICommandQueue> hash_CommandQueueId;
	
	protected Hashtable<Integer,ICommand> hash_CommandId;
	
	protected Vector <ICommand> vecUndo;
		
	protected Vector <ICommand> vecRedo;
	
	protected ArrayList<UndoRedoViewRep> arUndoRedoViews;
	
	private int iCountRedoCommand = 0;
	
	
	/**
	 * Constructor.
	 */
	public CommandManager( IGeneralManager setGeneralManager ) {
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Command, 
				ManagerType.COMMAND );
		
		commandFactory = new CommandFactory( setGeneralManager, 
				this);
		
		vecCmd_handle = new Vector<ICommand> ();
		
		vecCmd_schedule = new Vector<ICommand> ();
		
		hash_CommandQueueId = new Hashtable<Integer,ICommandQueue> ();
		
		hash_CommandId = new Hashtable<Integer,ICommand> ();
				
		vecUndo = new Vector <ICommand> (100);
		
		vecRedo = new  Vector <ICommand> (100);
		
		arUndoRedoViews = new ArrayList<UndoRedoViewRep>();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.CommandManager#addCommandListener(org.caleydo.core.command.ICommandListener)
	 */
	public void addCommandListener(ICommandListener addCommandListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.CommandManager#removeCommandListener(org.caleydo.core.command.ICommandListener)
	 */
	public boolean removeCommandListener(ICommandListener removeCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.CommandManager#hasCommandListener(org.caleydo.core.command.ICommandListener)
	 */
	public boolean hasCommandListener(ICommandListener hasCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommandActionListener#handleCommand(org.caleydo.core.command.ICommand)
	 */
	public void handleCommand(ICommand addCommand) {
		
		addCommand.doCommand();
		vecCmd_handle.addElement( addCommand );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommandActionListener#scheduleCommand(org.caleydo.core.command.ICommand)
	 */
	public void scheduleCommand(ICommand addCommand) {
		
		vecCmd_schedule.addElement( addCommand );
		addCommand.doCommand();
		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {
		return hash_CommandId.containsKey( iItemId );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {
		return hash_CommandId.get( iItemId );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#size()
	 */
	public int size() {
		return hash_CommandId.size();
	}


	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#registerItem(java.lang.Object, int, org.caleydo.core.data.manager.BaseManagerType)
	 */
	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {
		
		ICommand registerCommand = (ICommand) registerItem;
		
		if ( registerCommand.getClass().equals( 
				ICommandQueue.class )) {
			hash_CommandQueueId.put( iItemId, (ICommandQueue) registerItem );
		} else {
			
		}
		
		vecCmd_handle.addElement( registerCommand );		
		hash_CommandId.put( iItemId, registerCommand );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#unregisterItem(int, org.caleydo.core.data.manager.BaseManagerType)
	 */
	public boolean unregisterItem(int iItemId, ManagerObjectType type) {	
		
		//TODO: ensure thread safety! 
		if ( type == ManagerObjectType.CMD_QUEUE ) {
			hash_CommandQueueId.remove( iItemId );
		}
		
		if ( hash_CommandId.containsKey( iItemId ) ) {
		
			ICommand unregisterCommand = 
				hash_CommandId.remove( iItemId );
			
			return vecCmd_handle.remove( unregisterCommand );
		}
		
		return false;
	}
	
	public ICommand createCommandByType(final CommandQueueSaxType cmdType) {

		ICommand createdCommand = commandFactory.createCommandByType(cmdType);
		
		//BUG! creating command is not executing command!
//		//FIXME: should iterate over all undo/redo views.
//		if ( ! arUndoRedoViews.isEmpty() )
//		{
//			arUndoRedoViews.get(0).addCommand(createdCommand);
//		}
		
		return createdCommand;
	}

	
	public ICommand createCommand(final IParameterHandler phAttributes)
	{
		CommandQueueSaxType cmdType = 
			CommandQueueSaxType.valueOf( 
					phAttributes.getValueString( 
							CommandQueueSaxType.TAG_TYPE.getXmlKey() ) );
		
		ICommand createdCommand = createCommandByType(cmdType);
		
		if ( phAttributes != null ) 
		{
			createdCommand.setParameterHandler( phAttributes );	
		}
		
		//BUG! creating command is not executing command!
//		//FIXME: should iterate over all undo/redo views.
//		if (arUndoRedoViews.isEmpty() == false)
//		{
//			arUndoRedoViews.get(0).addCommand(createdCommand);
//		}
		
		return createdCommand;
	}
	

	/*
	 * 
	 */
	public boolean hasCommandQueueId( final int iCmdQueueId ) {
		return hash_CommandQueueId.containsKey( iCmdQueueId );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.manager.ICommandManager#getCommandQueueByCmdQueueId(int)
	 */
	public ICommandQueue getCommandQueueByCmdQueueId( final int iCmdQueueId ) {
		return hash_CommandQueueId.get( iCmdQueueId );
	}
	
	public ICommand createCommandQueue( final String sCmdType,
			final String sProcessType,
			final int iCmdId,
			final int iCmdQueueId,
			final int sQueueThread,
			final int sQueueThreadWait ) {
		
		ICommand newCmd = 
			commandFactory.createCommandQueue( sCmdType,
			sProcessType,
			iCmdId,
			iCmdQueueId,
			sQueueThread,
			sQueueThreadWait );
		
		int iNewCmdId = createId( ManagerObjectType.COMMAND );
		newCmd.setId( iNewCmdId );
		
		registerItem( newCmd, iNewCmdId, ManagerObjectType.COMMAND );
		
		return newCmd;
	}

	/**
	 * @see org.caleydo.core.manager.ICommandManager#runDoCommand(org.caleydo.core.command.ICommand)
	 */
	public synchronized void  runDoCommand(ICommand runCmd) {

		vecUndo.addElement( runCmd );
		
		if ( iCountRedoCommand > 0 ) 
		{
			vecRedo.remove(runCmd);
			iCountRedoCommand--;
		}		
		
		//FIXME: think of multipel tread support! current Version is not thread safe!
		Iterator <UndoRedoViewRep> iter = arUndoRedoViews.iterator();
		
		assert iter != null : "arUndoRedoViews was not inizalized! Iterator ist null-pointer";		
		
		while ( iter.hasNext() )
		{
			iter.next().addCommand(runCmd);
		}
		
	}

	/**
	 * @see org.caleydo.core.manager.ICommandManager#runUndoCommand(org.caleydo.core.command.ICommand)
	 */
	public synchronized void runUndoCommand(ICommand runCmd) {

		iCountRedoCommand++;
		vecUndo.remove( runCmd );
		
		vecRedo.addElement( runCmd );
	}

	public void addUndoRedoViewRep(UndoRedoViewRep undoRedoViewRep) {
		
		arUndoRedoViews.add(undoRedoViewRep);		
		arUndoRedoViews.get(0).updateCommandList(vecUndo);

	}	
	
}
