package org.caleydo.core.data.collection;

import java.util.ArrayList;
import java.util.List;
import javax.naming.OperationNotSupportedException;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.set.SetIterator;
import org.caleydo.core.data.selection.IVirtualArray;

/**
 * Interface for Sets
 * 
 * @author Alexander Lex
 */
public interface ISet
	extends IUniqueObject, Iterable<IStorage>, ICollection
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
	 * Add a storage based on its id. The storage has to be fully initialized
	 * with data
	 * 
	 * @param iStorageID
	 */
	public void addStorage(int iStorageID);

	/**
	 * Add a storage by reference. The storage has to be fully initialized with
	 * data
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
	 * @param iUniqueID the unique id associated with the virtual array
	 * @param iIndex the index in the virtual array
	 * 
	 * @return the storage
	 */
	public IStorage getStorageFromVA(int iUniqueID, int iIndex);

	/**
	 * Get an iterator that iterates over the storages considering the Virtual
	 * Array.
	 * 
	 * @param the unique ID of the set virtula array
	 * @return the set iterator
	 */
	public SetIterator VAIterator(int iUniqueID);

	/**
	 * Get the number of storages in a set
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Return the size of the virtual array
	 * 
	 * @param iUniqueID the unique id associated with the virtual array
	 * @return the number of sets in the virtual array
	 */
	public int sizeVA(int iUniqueID);

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
	//public void normalize();

	/**
	 * Normalize all storages in the set, based on values of all storages. For a
	 * numerical storage, this would mean, that global minima and maxima are
	 * retrieved instead of local ones (as is done with normalize()) Operates
	 * with the raw data as basis by default, however when a logarithmized
	 * representation is in the storage this is used. Make sure that all
	 * storages are logarithmized.
	 */
	//public void normalizeGlobally();

	/**
	 * Get the minimum value in the set.
	 * 
	 * @throws OperationNotSupportedException when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin();

	/**
	 * Get the maximum value in the set.
	 * 
	 * @throws OperationNotSupportedException when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMax();

	/**
	 * Set an artificial minimum for the dataset. All elements smaller than that
	 * are clipped to this value in the representation. This only affects the
	 * normalization, does not alter the raw data
	 */
	public void setMin(double dMin);

	/**
	 * Set an artificial maximum for the dataset. All elements smaller than that
	 * are clipped to this value in the representation. This only affects the
	 * normalization, does not alter the raw data
	 */
	public void setMax(double dMax);

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 * 
	 * @param dNormalized a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized);

	/**
	 * Calculates a normalized value based on min and max.
	 * 
	 * @param dRaw the raw value
	 * @return a value between 0 and 1
	 */
	public double getNormalizedForRaw(double dRaw);

	/**
	 * Switch the representation of the data. When this is called the data in
	 * normalized is replaced with data calculated from the mode specified.
	 * 
	 * @param dataRep
	 */
	public void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep);

	/**
	 * Calculates log10 on all storages in the set. Take care that the set
	 * contains only numerical storages, since nominal storages will cause a
	 * runtime exception. If you have mixed data you have to call log10 on all
	 * the storages that support it manually. 
	 */
	public void log10();

	
	/**
	 * Calculates log2 on all storages in the set. Take care that the set
	 * contains only numerical storages, since nominal storages will cause a
	 * runtime exception. If you have mixed data you have to call log2 on all
	 * the storages that support it manually. 
	 */
	public void log2();
	
	/**
	 * Creates a default virtual array for the storages in the set
	 * 
	 * @return the unique id associated with the virtual array
	 */
	public int createStorageVA();

	/**
	 * Creates a virtual array based on the list of indices supplied for the
	 * storages in the set
	 * 
	 * @param iAlSelections a list of indices
	 * @return the id of the newly created VA
	 */
	public int createStorageVA(List<Integer> iAlSelections);

	/**
	 * Creates a default virtual array for the set
	 * 
	 * @return the unique id associated with the virtual array
	 */
	public int createSetVA();

	/**
	 * Creates a virtual array based on the list of indices supplied for the set
	 * 
	 * @param iAlSelections a list of indices
	 * @return the unique id associated with the virtual array
	 */
	public int createSetVA(ArrayList<Integer> iAlSelections);

	/**
	 * Returns the virtual array associated with the unique ID
	 * 
	 * @param iUniqueID the unique id
	 * @return the virtual array associated with the unique ID
	 */
	public IVirtualArray getVA(int iUniqueID);

}
