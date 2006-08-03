/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cerberus.manager.CommandManager;

import cerberus.command.CommandType;
import cerberus.command.CommandInterface;

/**
 * Menu ActionListener triggers command.
 * 
 * @author Michael Kalkusch
 *
 */
public class DMenuCmdActionListener implements ActionListener {

	/**
	 * Reference to CommandManger
	 */
	protected CommandManager refCommandManager;
	
	/**
	 * Referecne to Command
	 */
	protected CommandInterface refCommand;
	
	/**
	 * Creates a new listener, which triggers setCommand.
	 */
	public DMenuCmdActionListener(final CommandManager setCommandManager,
			final CommandInterface setCommand) {
		
		refCommandManager = setCommandManager;
		refCommand = setCommand;
	}
	
	/**
	 * Creates a new command using the CommandType and stores it.
	 * 
	 * @param setCommandManager reference to CommandManager
	 * @param createCommandByType type of Command
	 */
	public DMenuCmdActionListener(final CommandManager setCommandManager,
			final CommandType createCommandByType) {
		
		refCommandManager = setCommandManager;
		refCommand = refCommandManager.createCommand(createCommandByType, "");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		refCommandManager.handleCommand( refCommand );
	}

}
