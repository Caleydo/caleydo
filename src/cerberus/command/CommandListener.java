/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

import cerberus.manager.CommandManager;
//import cerberus.command.CommandInterface;
import cerberus.command.CommandActionListener;
import cerberus.data.xml.MementoNetEventXML;

/**
 * Handles commands.
 * 
 * Controler/manager in the "Observer" Desing Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public interface CommandListener 
extends CommandActionListener, MementoNetEventXML {
	
	/**
	 * Get the reference to the parent manager of all CommandListener obejcts.
	 * 
	 * @return parent CommandManager
	 */
	public CommandManager getCommandManager();
	
}
