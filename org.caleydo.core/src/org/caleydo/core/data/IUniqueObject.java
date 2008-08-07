package org.caleydo.core.data;

import java.io.Serializable;

/**
 * Interface to all unique objects in Caleydo
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IUniqueObject
	extends Serializable
{
	/**
	 * Get the unique ID
	 * 
	 * @return unique ID
	 */
	public int getID();
}
