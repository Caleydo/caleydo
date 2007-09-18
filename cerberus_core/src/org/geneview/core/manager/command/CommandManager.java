/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.manager.command;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
//import java.util.Iterator;

import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.command.factory.CommandFactory;
import org.geneview.core.manager.command.factory.ICommandFactory;
//import org.geneview.core.manager.singelton.SingeltonManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.view.swt.undoredo.UndoRedoViewRep;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.ICommand;
import org.geneview.core.command.ICommandListener;
import org.geneview.core.command.queue.ICommandQueue;

/**
 * @author Michael Kalkusch
 *
 */
public class CommandManager 
 extends AAbstractManager 
 implements ICommandManager {

	private ICommandFactory refCommandFactory;
	
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
	 * 
	 */
	public CommandManager( IGeneralManager setGeneralManager ) {
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Command, 
				ManagerType.COMMAND );
		
		refCommandFactory = new CommandFactory( setGeneralManager, 
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
	 * @see org.geneview.core.data.manager.CommandManager#addCommandListener(org.geneview.core.command.ICommandListener)
	 */
	public void addCommandListener(ICommandListener addCommandListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.CommandManager#removeCommandListener(org.geneview.core.command.ICommandListener)
	 */
	public boolean removeCommandListener(ICommandListener removeCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.CommandManager#hasCommandListener(org.geneview.core.command.ICommandListener)
	 */
	public boolean hasCommandListener(ICommandListener hasCommandListener) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommandActionListener#handleCommand(org.geneview.core.command.ICommand)
	 */
	public void handleCommand(ICommand addCommand) {
		
		addCommand.doCommand();
		vecCmd_handle.addElement( addCommand );
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommandActionListener#scheduleCommand(org.geneview.core.command.ICommand)
	 */
	public void scheduleCommand(ICommand addCommand) {
		
		vecCmd_schedule.addElement( addCommand );
		addCommand.doCommand();
		
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {
		return hash_CommandId.containsKey( iItemId );
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.GeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {
		return hash_CommandId.get( iItemId );
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.GeneralManager#size()
	 */
	public int size() {
		return hash_CommandId.size();
	}


	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.GeneralManager#registerItem(java.lang.Object, int, org.geneview.core.data.manager.BaseManagerType)
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
	 * @see org.geneview.core.data.manager.GeneralManager#unregisterItem(int, org.geneview.core.data.manager.BaseManagerType)
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

		ICommand createdCommand = refCommandFactory.createCommandByType(cmdType);
		
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
	 * @see org.geneview.core.manager.ICommandManager#getCommandQueueByCmdQueueId(int)
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
			refCommandFactory.createCommandQueue( sCmdType,
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
	 * @see org.geneview.core.manager.ICommandManager#runDoCommand(org.geneview.core.command.ICommand)
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
	 * @see org.geneview.core.manager.ICommandManager#runUndoCommand(org.geneview.core.command.ICommand)
	 */
	public synchronized void runUndoCommand(ICommand runCmd) {

		iCountRedoCommand++;
		vecUndo.remove( runCmd );
		
		vecRedo.addElement( runCmd );
	}

//	/**
//	 * @deprecated use createCommandByType(final CommandQueueSaxType cmdType)
//	 */
//	public ICommand createCommand(CommandType cmdType, String details) {
//
//		assert false : "update to new command creation strucutre!";
//		return null;
//	}

	public void addUndoRedoViewRep(UndoRedoViewRep refUndoRedoViewRep) {
		
		arUndoRedoViews.add(refUndoRedoViewRep);		
		arUndoRedoViews.get(0).updateCommandList(vecUndo);

	}	
	
}
