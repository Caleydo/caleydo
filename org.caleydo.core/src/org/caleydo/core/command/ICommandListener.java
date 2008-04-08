package org.caleydo.core.command;

import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.command.ICommandActionListener;
import org.caleydo.core.data.xml.IMementoNetEventXML;

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
