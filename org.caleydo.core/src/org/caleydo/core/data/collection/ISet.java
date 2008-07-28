package org.caleydo.core.data.collection;

import org.caleydo.core.data.IUniqueManagedObject;

/**
 * Interface for Sets 
 * 
 * @author Alexander Lex
 *
 */
public interface ISet 
extends IUniqueManagedObject, Iterable<IStorage>
{
	/**
	 * Set the set type. Possible set types in ESetType.
	 * @see ESetType
	 * @param setType the type
	 */
	public void setSetType(ESetType setType);
	
	/**
	 * Get the set type. Possible set types in ESetType.
	 * @see ESetType
	 * @return the type
	 */
	public ESetType getSetType();
	
	/**
	 * Add a storage based on its id
	 * @param iStorageID
	 */
	public void addStorage(int iStorageID);
	
	/**
	 * Add a storage by reference
	 * @param storage the storage
	 */
	public void addStorage(IStorage storage);	
	
	/**
	 * Get the storage at the index iIndex
	 * @param iIndex
	 * @return
	 */
	public IStorage getStorage(int iIndex);	
	
	/**
	 * Get the number of storages in a set
	 * @return
	 */
	public int getSize();
	
	/**
	 * Normalize all storages in the set, based solely on the values within each
	 * storage. 
	 */
	public void normalize();
	
	/**
	 * Normalize all storages in the set, based on values of all storages.
	 * For a numerical storage, this would mean, that global minima and maxima 
	 * are retrieved instead of local ones (as is done with normalize())
	 */
	public void normalizeGlobally();
	
	public void setLabel(String sLabel);
	
	public String getLabel();
	
	
}
