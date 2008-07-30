package org.caleydo.core.command;

import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.command.ICommandActionListener;
import org.caleydo.core.data.xml.IMementoNetEventXML;

/**
 * Handles commands. Controller/manager in the "Observer" design pattern.
 * 
 * @author Michael Kalkusch
 */
public interface ICommandListener
	extends ICommandActionListener, IMementoNetEventXML
{

	/**
	 * Get the reference to the parent manager of all ICommandListener objects.
	 * 
	 * @return parent ICommandManager
	 */
	public ICommandManager getCommandManager();

}
