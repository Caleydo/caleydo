/**
 * 
 */
package org.caleydo.core.command.queue;

import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.queue.ACommandQueue;
import org.caleydo.core.command.queue.ICommandQueue;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Create a queue of command's, that can be executed in a row.
 * 
 * @see org.caleydo.core.command.ICommand
 * @author Michael Kalkusch
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
	 * If set TURE This queue is processed at the moment! Default is FALSE.
	 */
	private boolean bQueueIsExcecuting = false;

	/**
	 * If set to true this queue has been executed at leaset once. Default is
	 * FALSE.
	 * 
	 * @see org.caleydo.core.command.queue.CommandQueueVector#bQueueCanBeExecutedSeveralTimes
	 */
	protected boolean bQueueWasExcecuted = false;

	/**
	 * If set to true this queue may be executed several times in a row without
	 * an undoCommadn(). If set to FALSE this queue must only be executed once!
	 * Default is FALSE.
	 */
	protected boolean bQueueCanBeExecutedSeveralTimes = false;

	/**
	 * If "undo" is called on queue, the undo() method is called in reverse
	 * order to the do() method. Default is TURE.
	 */
	protected boolean bQueueUndoInReverseOrder = true;

	/**
	 * Vector holding several Command's
	 */
	protected Vector<ICommand> vecCommandsInQueue;

	/**
	 * Constructor.
	 */
	public CommandQueueVector(final int iUniqueCmdId, final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType, final int iCmdQueuId)
	{

		super(iUniqueCmdId, generalManager, commandManager, commandQueueSaxType, iCmdQueuId);

		vecCommandsInQueue = new Vector<ICommand>(iCmdQueueVector_initialLength);

		setCommandQueueSaxType(CommandQueueSaxType.COMMAND_QUEUE_OPEN);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		if (bQueueIsExcecuting)
		{
			throw new CaleydoRuntimeException(
					"Can not execute command queue, that is already processed!",
					CaleydoRuntimeExceptionType.COMMAND);
			// return;
		}

		if ((!bQueueCanBeExecutedSeveralTimes) && (bQueueWasExcecuted))
		{
			throw new CaleydoRuntimeException(
					"Can not execute command queue, that is already processed!",
					CaleydoRuntimeExceptionType.COMMAND);
		}
		/**
		 * critical section
		 */
		bQueueIsExcecuting = true;

		try
		{
			Iterator<ICommand> iter = vecCommandsInQueue.iterator();

			while (iter.hasNext())
			{
				iter.next().doCommand();
			}

		}
		catch (CaleydoRuntimeException pre)
		{
			System.err.print("Exception during execution of CommandQueue [" + this.iCmdQueueId
					+ "] with exception:[" + pre.toString() + "]");
		}

		bQueueWasExcecuted = true;

		bQueueIsExcecuting = false;

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{

		if (this.bQueueCanBeExecutedSeveralTimes)
		{
			throw new CaleydoRuntimeException(
					"Can not call undo() on command queue, that can be executed several times!",
					CaleydoRuntimeExceptionType.COMMAND);
		}

		if (this.bQueueIsExcecuting)
		{
			throw new CaleydoRuntimeException(
					"Can not execute command queue, that is already processed!",
					CaleydoRuntimeExceptionType.COMMAND);
		}

		/**
		 * Special case: no commands in vector Avoid excecute empty list!
		 */
		if (vecCommandsInQueue.isEmpty())
		{
			return;
		}

		/**
		 * cirtical section
		 */
		bQueueIsExcecuting = true;

		ListIterator<ICommand> iter = vecCommandsInQueue.listIterator();

		if (bQueueUndoInReverseOrder)
		{
			/**
			 * excecute undo in reverse order ..
			 */

			ICommand lastCommandInList = null;

			/* goto end of list ... */
			while (iter.hasNext())
			{
				lastCommandInList = iter.next();
			}
			/* no at last item of list .. */

			/* undo for last item in list.. */
			lastCommandInList.undoCommand();

			/* undo for all other items in list in reverse order... */
			while (iter.hasPrevious())
			{
				iter.previous().undoCommand();
			}
			bQueueIsExcecuting = false;

			return;
		}

		/* if ( bQueueUndoInReverseOrder ) else... */

		while (iter.hasNext())
		{
			iter.next().undoCommand();
		}

		/**
		 * End of cirtical section
		 */
		bQueueIsExcecuting = false;
	}

	/**
	 * Check is QueueID is set. Attention: This method is expensive, because
	 * getId() is called on all elements inside the Vector.
	 * 
	 * @param testCmdQueueId
	 *            uniwue command id to seek for
	 * @return TRUE if testCmdQueueId is inside vector
	 */
	public boolean containsCmdQueueId(final int testCmdQueueId)
	{

		Iterator<ICommand> iter = vecCommandsInQueue.iterator();

		while (iter.hasNext())
		{
			if (iter.next().getId() == testCmdQueueId)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Add a new command.
	 * 
	 * @param cmdItem
	 *            add command
	 * @return FALSE if command is already inside queue, TRUE else
	 */
	public boolean addCmdToQueue(final ICommand cmdItem)
	{

		if (this.vecCommandsInQueue.contains(cmdItem))
		{
			return false;
		}

		this.vecCommandsInQueue.addElement(cmdItem);
		return true;
	}

	/**
	 * Remove a new command.
	 * 
	 * @param cmdItem
	 *            remove command
	 */
	public boolean removeCmdFromQueue(final ICommand cmdItem)
	{

		return this.vecCommandsInQueue.remove(cmdItem);
	}

	/**
	 * Contains a command.
	 * 
	 * @param cmdItem
	 *            test if command is contained in command queue
	 */
	public boolean containsCmdInQueue(final ICommand cmdItem)
	{

		return this.vecCommandsInQueue.contains(cmdItem);
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
