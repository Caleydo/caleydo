/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.base;

/**
 * Interface to all unique objects in Caleydo
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IUniqueObject {
	/**
	 * Get the unique ID of the object
	 * 
	 * @return unique ID
	 */
	public int getID();
}
