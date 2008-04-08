/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.command.base;

import org.caleydo.core.data.collection.ISet;

/**
 * Classes that need a ISet implement this interface. 
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICmdHandleSet {

	/**
	 * ISet the reference to a ISet
	 * 
	 * @param useSet ISet to be used
	 */
	public void setSet( final ISet useSet );
	
}
