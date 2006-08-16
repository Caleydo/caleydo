/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command;

import cerberus.manager.ICommandManager;
//import cerberus.command.CommandInterface;
import cerberus.command.ICommandActionListener;
import cerberus.data.xml.IMementoNetEventXML;

/**
 * Handles commands.
 * 
 * Controler/manager in the "Observer" Desing Pattern.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICommandListener 
extends ICommandActionListener, IMementoNetEventXML {
	
	/**
	 * Get the reference to the parent manager of all ICommandListener obejcts.
	 * 
	 * @return parent ICommandManager
	 */
	public ICommandManager getCommandManager();
	
}
