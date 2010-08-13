package org.caleydo.core.manager;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * One Manager handle all ICommandListener. This is a singleton for all Commands and ICommandListener objects.
 * "ISingelton" Design Pattern.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface ICommandManager
	extends IManager<ICommand> {

	/**
	 * create a new command. Calls createCommandByType(CommandType) internal.
	 * 
	 * @see org.caleydo.core.manager.ICommandManager#createCommandByType(ECommandType)
	 * @param phAttributes
	 *            Define several attributes and assign them in new Command
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
}
