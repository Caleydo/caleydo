package org.caleydo.core.data.collection;

/**
 * Interface for objects with labels and a cache.
 * 
 * @author Michael Kalkusch
 *
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
	 * @param setLabel label text
	 */
	public void setLabel( final String setLabel);
}
