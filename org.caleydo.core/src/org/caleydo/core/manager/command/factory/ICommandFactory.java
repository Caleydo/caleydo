package org.caleydo.core.manager.command.factory;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.ICommand;

/**
 * Base class for Command factory. Design Pattern "Command"
 * 
 * @author Michael Kalkusch
 */
public interface ICommandFactory
{

	/**
	 * Create a new Command assigned to a cmdType.
	 * 
	 * @param cmdType specify the ICommand to be created.
	 * @return new ICommand
	 */
	public ICommand createCommandByType(final CommandType cmdType);

	/**
	 * @see org.caleydo.core.manager.ICommandManager#createCommandQueue(String,
	 *      String, int, int, int, int)
	 */
	public ICommand createCommandQueue(final String sCmdType, final String sProcessType,
			final int iCmdId, final int iCmdQueueId, final int sQueueThread,
			final int sQueueThreadWait);
}
