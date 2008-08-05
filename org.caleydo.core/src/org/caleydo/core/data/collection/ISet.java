package org.caleydo.core.data.collection;

import javax.naming.OperationNotSupportedException;
import org.caleydo.core.data.IManagedObject;
import org.caleydo.core.data.collection.set.SetIterator;

/**
 * Interface for Sets
 * 
 * @author Alexander Lex
 */
public interface ISet
	extends IManagedObject, Iterable<IStorage>, ICollection
{

	/**
	 * Set the set type. Possible set types in ESetType.
	 * 
	 * @see ESetType
	 * @param setType the type
	 */
	public void setSetType(ESetType setType);

	/**
	 * Get the set type. Possible set types in ESetType.
	 * 
	 * @see ESetType
	 * @return the type
	 */
	public ESetType getSetType();

	/**
	 * Add a storage based on its id
	 * 
	 * @param iStorageID
	 */
	public void addStorage(int iStorageID);

	/**
	 * Add a storage by reference
	 * 
	 * @param storage the storage
	 */
	public void addStorage(IStorage storage);

	/**
	 * Get the storage at the index iIndex
	 * 
	 * @param iIndex
	 * @return
	 */
	public IStorage get(int iIndex);

	/**
	 * Get the storage via the index in the virtual array
	 * 
	 * @param iIndex the index in the virtual array
	 * @param iUniqueID the unique id associated with the virtual array
	 * @return the storage
	 */
	public IStorage getStorageFromVA(int iIndex, int iUniqueID);
	
	/**
	 * Get an iterator that iterates over the storages
	 * @return
	 */
	public SetIterator VAIterator(int iUniqueID);

	/**
	 * Get the number of storages in a set
	 * 
	 * @return
	 */
	public int size();
	
	/**
	 * Get the depth of the set, which is the length of the storages
	 * 
	 * @return the number of elements in the storages contained in the list
	 */
	public int depth();

	/**
	 * Normalize all storages in the set, based solely on the values within each
	 * storage. Operates with the raw data as basis by default, however when a
	 * logarithmized representation is in the storage this is used.
	 */
	public void normalize();

	/**
	 * Normalize all storages in the set, based on values of all storages. For a
	 * numerical storage, this would mean, that global minima and maxima are
	 * retrieved instead of local ones (as is done with normalize()) Operates
	 * with the raw data as basis by default, however when a logarithmized
	 * representation is in the storage this is used. Make sure that all
	 * storages are logarithmized.
	 */
	public void normalizeGlobally();

	/**
	 * Get the minimum value in the set.
	 * 
	 * @throws OperationNotSupportedException when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin() throws OperationNotSupportedException;

	/**
	 * Get the maximum value in the set.
	 * 
	 * @throws OperationNotSupportedException when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMax() throws OperationNotSupportedException;

	/**
	 * Calculates log10 on all storages in the set. Take care that the set
	 * contains only numerical storages, since nominal storages will cause a
	 * runtime exception. If you have mixed data you have to call log10 on all
	 * the storages that support it manually Call normalize() after this to have
	 * it represented correctly in the visualizations.
	 */
	public void log10();

}
