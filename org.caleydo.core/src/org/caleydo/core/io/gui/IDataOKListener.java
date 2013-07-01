/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.io.gui;

/**
 * <p>
 * Interface used for nested GUI dialogs, so that they can be notified when the data of a sub-widget is ready
 * to be processed.
 * </p>
 * <p>
 * By calling {@link #dataOK()} the sub-widget tells the parent that it's data is correct. The parent then
 * needs to check whether it's data is ready as well and enable the ok button if this is the case.
 * </p>
 * 
 * @author Alexander Lex
 */
public interface IDataOKListener {

	/**
	 * Called by a sub-widget to it's parent which implements this interface when all it's data is complete.
	 */
	public void dataOK();

}
