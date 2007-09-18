/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
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
	 * Immedieatly executes command.
	 * 
	 * @see cerberus.command.Command.ICommandListener#scheduleCommand(cerberus.command.ICommand)
	 * 
	 * @param addCommand
	 */
	public void handleCommand( ICommand addCommand );
	
	/**
	 * Insertes command into execution queue.
	 * 
	 * @see cerberus.command.Command.ICommandListener#handleCommand(cerberus.command.ICommand)
	 * 
	 * @param addCommand
	 */
	public void scheduleCommand( ICommand addCommand );
	
}
