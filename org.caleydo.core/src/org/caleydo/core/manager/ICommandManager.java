package org.caleydo.core.manager;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.ICommandActionListener;
import org.caleydo.core.command.ICommandListener;
import org.caleydo.core.command.queue.ICommandQueue;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;

/**
 * One Manager handle all ICommandListener. This is a singleton for all Commands
 * and ICommandListener objects. "ISingelton" Design Pattern.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface ICommandManager
	extends ICommandActionListener, IManager<ICommand>
{

	// /**
	// * Create a new CommandQueue object.
	// *
	// * @param sCmdType type of command
	// * @param sProcessType define how to process queue
	// * @param iCmdId unique CmdId
	// * @param iCmdQueueId unique commandQueueId, must not be global unique!
	// * @param sQueueThread define a thread pool, default = -1 means no thread
	// * pool
	// * @param sQueueThreadWait define dependent thread pool, default = -1
	// means
	// * no dependency on other thread to finish
	// * @return new commandQueue
	// */
	// public ICommand createCommandQueue(final String sCmdType, final String
	// sProcessType,
	// final int iCmdId, final int iCmdQueueId, final int sQueueThread,
	// final int sQueueThreadWait);

	/**
	 * create a new command. Calls createCommandByType(CommandType) internal.
	 * 
	 * @see org.caleydo.core.manager.ICommandManager#createCommandByType(ECommandType)
	 * @param phAttributes Define several attributes and assign them in new
	 *            Command
	 * @return new Command with attributes defined in phAttributes
	 */
	public ICommand createCommand(final IParameterHandler phAttributes);

	/**
	 * Create a new command using the CommandType.
	 * 
	 * @param cmdType
	 * @return
	 */
	public ICommand createCommandByType(final ECommandType cmdType);

	/**
	 * Add reference to one ICommandListener object.
	 * 
	 * @param addCommandListener adds reference to ICommandListener object.
	 */
	public void addCommandListener(ICommandListener addCommandListener);

	/**
	 * Remove reference to one ICommandListener object.
	 * 
	 * @param removeCommandListener removes references to ICommandListener
	 *            object.
	 * @return TRUE if the reference was removed, false if the reference was not
	 *         found.
	 */
	public boolean removeCommandListener(ICommandListener removeCommandListener);

	/**
	 * Tests if the reference to one ICommandListener object exists.
	 * 
	 * @param hasCommandListener reference to be tested
	 * @return true if the reference is bound to this ICommandManager
	 */
	public boolean hasCommandListener(ICommandListener hasCommandListener);

	/**
	 * Get a command queue by it's commandQueueId, which is only a key for the
	 * commandQueue and is not a uniqueSystem wide Id.
	 * 
	 * @param iCmdQueueId commandQueueId
	 * @return command queue
	 */
	public ICommandQueue getCommandQueueByCmdQueueId(final int iCmdQueueId);

	/**
	 * Tests if a iCmdQueueId is registered with a CommandQueue object.
	 * 
	 * @param iCmdQueueId test this id
	 * @return TRUE if an CommandQueue is bound that iCmdQueueId
	 */
	public boolean hasCommandQueueId(final int iCmdQueueId);

	/**
	 * Register a org.caleydo.core.command.ICommand after its doCommand() method
	 * was called. Used for redo-undo.
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 * @param runCmd
	 */
	public void runDoCommand(ICommand runCmd);

	/**
	 * Register a org.caleydo.core.command.ICommand after its undoCommand()
	 * method was called. Used for redo-undo.
	 * 
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 * @param runCmd
	 */
	public void runUndoCommand(ICommand runCmd);

	/**
	 * Trigger serialization to file
	 */
	public void writeSerializedObjects(final String sFileName);

	/**
	 * Read serialized commands from file
	 */
	public void readSerializedObjects(final String sFileName);

	public void addUndoRedoViewRep(UndoRedoViewRep undoRedoViewRep);
}
