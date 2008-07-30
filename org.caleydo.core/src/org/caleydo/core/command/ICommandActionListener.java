package org.caleydo.core.command;

/**
 * Handles commands and forwards them. Controller/manager in the "Observer"
 * design pattern.
 * 
 * @author Michael Kalkusch
 */
public interface ICommandActionListener
{

	/**
	 * Immediately executes command.
	 * 
	 * @see org.caleydo.core.command.Command.ICommandListener#scheduleCommand(org.caleydo.core.command.ICommand)
	 * @param addCommand
	 */
	public void handleCommand(ICommand addCommand);

	/**
	 * Inserts command into execution queue.
	 * 
	 * @see org.caleydo.core.command.Command.ICommandListener#handleCommand(org.caleydo.core.command.ICommand)
	 * @param addCommand
	 */
	public void scheduleCommand(ICommand addCommand);

}
