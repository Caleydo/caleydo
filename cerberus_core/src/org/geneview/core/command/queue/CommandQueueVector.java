/**
 * 
 */
package org.geneview.core.command.queue;

import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.ICommand;
import org.geneview.core.command.queue.ACommandQueue;
import org.geneview.core.command.queue.ICommandQueue;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Create a queue of command's, that can be executed in a row.
 * 
 * @see cerberus.command.ICommand
 * 
 * @author Michael Kalkusch
 *
 */
public class CommandQueueVector 
extends ACommandQueue
implements ICommandQueue
{

	/**
	 * Initial size of vector for command's.
	 */
	private static final int iCmdQueueVector_initialLength = 4;
	
	/**
	 * If set TURE This queue is processed at the moment!
	 * Default is FALSE.
	 */
	private boolean bQueueIsExcecuting = false;

	/**
	 * If set to true this queue has been executed at leaset once.
	 * Default is FALSE.
	 * 
	 * @see cerberus.command.queue.CommandQueueVector#bQueueCanBeExecutedSeveralTimes
	 */
	protected boolean bQueueWasExcecuted = false;
	
	/**
	 * If set to true this queue may be executed several times in a row without 
	 * an undoCommadn().
	 * If set to FALSE this queue must only be executed once!
	 * Default is FALSE.
	 */
	protected boolean bQueueCanBeExecutedSeveralTimes = false;
	
	/**
	 * If "undo" is called on queue, the undo() method is called 
	 * in reverse order to the do() method.
	 * Default is TURE.
	 * 
	 */
	protected boolean bQueueUndoInReverseOrder = true;
	
	/**
	 * Vector holding several Command's
	 */
	protected Vector <ICommand> vecCommandsInQueue;
	
	
	/**
	 * Constructor.
	 * 
	 */
	public CommandQueueVector(final int iUniqueCmdId, 
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final int iCmdQueuId ) {
		
		super( iUniqueCmdId, 
				refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType,
				iCmdQueuId );
		
		vecCommandsInQueue = 
			new Vector <ICommand> (iCmdQueueVector_initialLength);
		
		setCommandQueueSaxType(CommandQueueSaxType.COMMAND_QUEUE_OPEN);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		if ( bQueueIsExcecuting ) {
			throw new GeneViewRuntimeException("Can not execute command queue, that is already processed!", 
					GeneViewRuntimeExceptionType.COMMAND );
			//return;
		}
				
		if (( ! bQueueCanBeExecutedSeveralTimes )&&
				( bQueueWasExcecuted )) {
			throw new GeneViewRuntimeException("Can not execute command queue, that is already processed!",
					GeneViewRuntimeExceptionType.COMMAND );
		}
		/**
		 * critical section
		 */
		bQueueIsExcecuting = true;
		
		try {
			Iterator <ICommand> iter = 
				vecCommandsInQueue.iterator();
			
			while ( iter.hasNext() ) {
				iter.next().doCommand();
			}
		
		} catch (GeneViewRuntimeException pre) {
			System.err.print("Exception during execution of CommandQueue [" + 
					this.iCmdQueueId + "] with exception:[" +
					pre.toString() + "]" );
		}
		
		bQueueWasExcecuted = true;

		bQueueIsExcecuting = false;
		

	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
		if ( this.bQueueCanBeExecutedSeveralTimes ) {
			throw new GeneViewRuntimeException("Can not call undo() on command queue, that can be executed several times!", 
					GeneViewRuntimeExceptionType.COMMAND );
		}
		
		if ( this.bQueueIsExcecuting ) {
			throw new GeneViewRuntimeException("Can not execute command queue, that is already processed!", 
					GeneViewRuntimeExceptionType.COMMAND );
		}		

		
		/**
		 *  Special case: no commands in vector
		 * Avoid excecute empty list! 
		 */
		if ( vecCommandsInQueue.isEmpty() ) {
			return;
		}
				
		/**
		 * cirtical section
		 */
		bQueueIsExcecuting = true;
		
		ListIterator <ICommand> iter = 
			vecCommandsInQueue.listIterator();
				
		if ( bQueueUndoInReverseOrder ) {			
			/**
			 * excecute undo in reverse order ..
			 */	
			
			ICommand lastCommandInList = null;
			
			/* goto end of list ... */
			while ( iter.hasNext() ) {
				lastCommandInList = iter.next();
			}
			/* no at last item of list .. */
			
			/* undo for last item in list.. */
			lastCommandInList.undoCommand();
			
			/* undo for all other items in list in reverse order... */
			while ( iter.hasPrevious() ) {
				iter.previous().undoCommand();
			}
			bQueueIsExcecuting = false;
			
			return;
		}
		
		/* if ( bQueueUndoInReverseOrder ) else... */
		
		while ( iter.hasNext() ) {
			iter.next().undoCommand();
		}
		
		/**
		 * End of cirtical section
		 */
		bQueueIsExcecuting = false;
	}


	/**
	 * Check is QueueID is set.
	 * Attention: This method is expensive, because getId() is called on
	 * all elements inside the Vector.
	 * 
	 * @param testCmdQueueId uniwue command id to seek for
	 * 
	 * @return TRUE if testCmdQueueId is inside vector
	 */
	public boolean containsCmdQueueId( final int testCmdQueueId ) {
		
		Iterator <ICommand> iter = 
			vecCommandsInQueue.iterator();
		
		while ( iter.hasNext() ) {
			if ( iter.next().getId() == testCmdQueueId ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Add a new command.
	 * 
	 * @param cmdItem add command
	 * 
	 * @return FALSE if command is already inside queue, TRUE else
	 */
	public boolean addCmdToQueue( final ICommand cmdItem ) {
		if ( this.vecCommandsInQueue.contains( cmdItem ) ) {
			return false;
		}
		
		this.vecCommandsInQueue.addElement( cmdItem );
		return true;
	}
	
	/**
	 * Remove a new command.
	 * 
	 * @param cmdItem remove command
	 */
	public boolean removeCmdFromQueue( final ICommand cmdItem ) {
		return this.vecCommandsInQueue.remove( cmdItem );		
	}
	
	/**
	 * Contains a command.
	 * 
	 * @param cmdItem test if command is contained in command queue
	 */
	public boolean containsCmdInQueue( final ICommand cmdItem ) {
		return this.vecCommandsInQueue.contains( cmdItem );
	}

	public void init()
	{
		// nothing to do, all done in constructor		
	}

	public void destroy()
	{
		vecCommandsInQueue.clear();
		vecCommandsInQueue = null;		
	}
}
