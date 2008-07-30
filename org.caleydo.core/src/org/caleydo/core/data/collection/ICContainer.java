package org.caleydo.core.data.collection;

/**
 * @author Alexander Lex Interface for the containers
 */
public interface ICContainer
{

	/**
	 * Returns the size of the container
	 * 
	 * @return the size of the container
	 */
	public int size();

	/**
	 * Brings any dataset into a format between 0 and 1. This is used for
	 * drawing. For nominal data the first value is 0, the last value is 1
	 */
	public ICContainer normalize();

}
