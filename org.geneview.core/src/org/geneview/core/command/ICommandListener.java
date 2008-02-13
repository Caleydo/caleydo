package org.geneview.core.command;

import org.geneview.core.manager.ICommandManager;
import org.geneview.core.command.ICommandActionListener;
import org.geneview.core.data.xml.IMementoNetEventXML;

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
