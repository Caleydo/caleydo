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
	 * Resets the selectionId.
	 * 
	 * @param iSetCollectionId
	 *            new unique collection Id
	 */
	public void setId(int iSetCollectionId);

	/**
	 * Get a unique Id
	 * 
	 * @return unique Id
	 */
	public int getId();
}
