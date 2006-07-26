/**
 * 
 */
package cerberus.command.queue;

import java.util.Vector;
import java.util.Iterator;

import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.queue.AbstractCommandQueue;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author kalkusch
 *
 */
public class CommandQueueVector 
extends AbstractCommandQueue
implements CommandInterface 
{

	private static final int iCmdQueueVector_initialLength = 4;
	/**
	 * If set TURE This queue is processed at the moement!
	 */
	private boolean bQueueIsExcecuting = false;
	
	/**
	 * If set to true this queue has been executed at leaset once.
	 * 
	 * @see cerberus.command.queue.CommandQueueVector#bQueueCanBeExecutedSeveralTimes
	 */
	protected boolean bQueueWasExcecuted = false;
	
	/**
	 * If set to true this queue may be executed several times in a row without 
	 * an undoCommadn().
	 * If set to FALSE this queue must only be executed once!
	 */
	protected boolean bQueueCanBeExecutedSeveralTimes = false;
	
	/**
	 * Vector holding several Command's
	 */
	protected Vector <CommandInterface> vecCommandsInQueue;
	
	
	/**
	 * 
	 */
	public CommandQueueVector( int iSetCmdCollectionId ) {
		super( iSetCmdCollectionId );
		
		vecCommandsInQueue = 
			new Vector <CommandInterface> (iCmdQueueVector_initialLength);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		if ( bQueueIsExcecuting ) {
			throw new CerberusRuntimeException("Can not execute command queue, that is already processed!", 
					CerberusExceptionType.COMMAND );
			//return;
		}
				
		if (( ! bQueueCanBeExecutedSeveralTimes )&&
				( bQueueWasExcecuted )) {
			throw new CerberusRuntimeException("Can not execute command queue, that is already processed!",
					CerberusExceptionType.COMMAND );
		}
		/**
		 * critical section
		 */
		bQueueIsExcecuting = true;
		
		try {
			Iterator <CommandInterface> iter = 
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
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.QUEUE_OPEN;
	}


}
