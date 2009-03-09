package org.caleydo.core.data.collection;

/**
 * Interface for objects with labels and a cache.
 * 
 * @author Alexander Lex
 * @author Michael Kalkusch
 */
public interface ICollection {
	/**
	 * Return a Label of the item.
	 * 
	 * @return label text
	 */
	public String getLabel();

	/**
	 * Sets the label for this item.
	 * 
	 * @param setLabel
	 *          label text
	 */
	public void setLabel(final String setLabel);

	/**
	 * Deletes the virtual arrays associated with the unique id
	 * 
	 * @param iUniqueID
	 *          the unique ID associated with the virtual array
	 */
	public void removeVirtualArray(int iUniqueID);

	/**
	 * Resets the virtual arrays to the original values
	 * 
	 * @param iUniqueID
	 *          the unique ID associated with the virtual array
	 */
	public void resetVirtualArray(int iUniqueID);
}
