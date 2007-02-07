/**
 * 
 */
package cerberus.command.queue;

import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.queue.ACommandQueue;
import cerberus.command.queue.ICommandQueue;
import cerberus.util.exception.CerberusRuntimeExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

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
implements ICommand , ICommandQueue
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
	 * If "undo" is called on queue, the undo() methode is called 
	 * in reverse order to the do() methode.
	 * Default is TURE.
	 * 
	 */
	protected boolean bQueueUndoInReverseOrder = true;
	
	/**
	 * Vector holding several Command's
	 */
	protected Vector <ICommand> vecCommandsInQueue;
	
	
	/**
	 * 
	 */
	public CommandQueueVector( int iUniqueCmdId, int iCmdQueuId ) {
		super( iUniqueCmdId, iCmdQueuId );
		
		vecCommandsInQueue = 
			new Vector <ICommand> (iCmdQueueVector_initialLength);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		if ( bQueueIsExcecuting ) {
			throw new CerberusRuntimeException("Can not execute command queue, that is already processed!", 
					CerberusRuntimeExceptionType.COMMAND );
			//return;
		}
				
		if (( ! bQueueCanBeExecutedSeveralTimes )&&
				( bQueueWasExcecuted )) {
			throw new CerberusRuntimeException("Can not execute command queue, that is already processed!",
					CerberusRuntimeExceptionType.COMMAND );
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
		
		} catch (CerberusRuntimeException pre) {
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
	public void undoCommand() throws CerberusRuntimeException {
		
		if ( this.bQueueCanBeExecutedSeveralTimes ) {
			throw new CerberusRuntimeException("Can not call undo() on command queue, that can be executed several times!", 
					CerberusRuntimeExceptionType.COMMAND );
		}
		
		if ( this.bQueueIsExcecuting ) {
			throw new CerberusRuntimeException("Can not execute command queue, that is already processed!", 
					CerberusRuntimeExceptionType.COMMAND );
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
			
			/* undo for all otehr items in list in reverse order... */
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

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.COMMAND_QUEUE_OPEN;
	}

	/**
	 * Check is QueueID is set.
	 * Attention: This methode is expensive, because getId() is called on
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
