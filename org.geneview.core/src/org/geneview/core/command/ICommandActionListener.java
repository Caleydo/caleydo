package org.geneview.core.command;

/**
 * Handles commands and forwards them.
 * 
 * Controler/manager in the "Observer" Desing Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICommandActionListener {

	/**
	 * Immediately executes command.
	 * 
	 * @see org.geneview.core.command.Command.ICommandListener#scheduleCommand(org.geneview.core.command.ICommand)
	 * 
	 * @param addCommand
	 */
	public void handleCommand( ICommand addCommand );
	
	/**
	 * Inserts command into execution queue.
	 * 
	 * @see org.geneview.core.command.Command.ICommandListener#handleCommand(org.geneview.core.command.ICommand)
	 * 
	 * @param addCommand
	 */
	public void scheduleCommand( ICommand addCommand );
	
}
