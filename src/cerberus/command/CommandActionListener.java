/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

/**
 * Handles commands and forwards them.
 * 
 * Controler/manager in the "Observer" Desing Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public interface CommandActionListener {

	/**
	 * Immedieatly executes command.
	 * 
	 * @see cerberus.command.Command.CommandListener#scheduleCommand(cerberus.command.CommandInterface)
	 * 
	 * @param addCommand
	 */
	public void handleCommand( CommandInterface addCommand );
	
	/**
	 * Insertes command into execution queue.
	 * 
	 * @see cerberus.command.Command.CommandListener#handleCommand(cerberus.command.CommandInterface)
	 * 
	 * @param addCommand
	 */
	public void scheduleCommand( CommandInterface addCommand );
	
}
