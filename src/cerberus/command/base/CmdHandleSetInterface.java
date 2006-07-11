/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.base;

import cerberus.data.collection.Set;

/**
 * Classes that need a Set implement this interface. 
 * 
 * @author Michael Kalkusch
 *
 */
public interface CmdHandleSetInterface {

	/**
	 * Set the reference to a Set
	 * 
	 * @param useSet Set to be used
	 */
	public void setSet( final Set useSet );
	
}
