package org.caleydo.core.data.collection;


/**
 * Interface for objects with labels and a cache.
 * 
 * @author Alexander Lex
 * @author Michael Kalkusch
 */
public interface ICollection
{
	/**
	 * Return a Label of the item.
	 * 
	 * @return label text
	 */
	public String getLabel();

	/**
	 * Sets the label for this item.
	 * 
	 * @param setLabel label text
	 */
	public void setLabel(final String setLabel);

	/**
	 * Enables a virtual Array for a specific unique ID. This unique ID should
	 * typically be the id of the calling view Once a virtual array has been
	 * enabled, all get methods will provide data based on the virtual array. By
	 * default there is no difference between the ordering in the storage
	 * elements, but this can be changed, eg. by moving, removing or adding
	 * elements
	 * 
	 * @param iUniqueID the unique id of the calling instance
	 */
	public void enableVirtualArray(int iUniqueID);

	/**
	 * Disables a virtual array for the calling instance on the called instance
	 * 
	 * @param iUniqueID the unique ID associated with the virtual array
	 */
	public void disableVirtualArray(int iUniqueID);
	
	/**
	 * Deletes the virtual arrays associated with the unique id
	 * 
	 * @param iUniqueID the unique ID associated with the virtual array
	 */
	public void removeVirtualArray(int iUniqueID);
	

	/**
	 * Resets the virtual arrays to the original values
	 * 
	 * @param iUniqueID the unique ID associated with the virtual array
	 */
	public void resetVirtualArray(int iUniqueID);
}
