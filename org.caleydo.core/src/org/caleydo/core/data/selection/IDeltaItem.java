package org.caleydo.core.data.selection;

/**
 * 
 * @author Alexander Lex
 * 
 */
public interface IDeltaItem
	extends Cloneable
{
	/**
	 * Returns the selection ID
	 * 
	 * @return the selection ID
	 */
	public int getPrimaryID();

	/**
	 * 
	 * @return
	 */
	public int getSecondaryID();

	public void setPrimaryID(int iPrimaryID);

	public void setSecondaryID(int iSecondaryID);

	public Object clone();

}
