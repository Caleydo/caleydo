package org.caleydo.core.data.collection;

import java.util.Iterator;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ccontainer.FloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.IntCContainerIterator;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.ERawDataType;



public interface IStorage
	extends IUniqueObject, ICollection {

	
	
	
	/**
	 * Returns a float value from a storage of which the kind has to be specified Use iterator when you want
	 * to iterate over the whole field, it has better performance
	 * 
	 * @param storageKind
	 *            Specify which kind of storage (eg: raw, normalized)
	 * @param iIndex
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public float getFloat(EDataRepresentation storageKind, int iIndex);

	/**
	 * Returns a iterator to the storage of which the kind has to be specified Good performance
	 * 
	 * @param storageKind
	 * @return
	 */
	public FloatCContainerIterator floatIterator(EDataRepresentation storageKind);

	/**
	 * Returns a float value from a storage of which the kind has to be specified Use iterator when you want
	 * to iterate over the whole field, it has better performance
	 * 
	 * @param storageKind
	 *            Specify which kind of storage (eg: raw, normalized, log)
	 * @param iIndex
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public int getInt(EDataRepresentation storageKind, int iIndex);

	/**
	 * Returns a iterator to the storage of which the kind has to be specified Good performance
	 * 
	 * @param storageKind
	 * @return
	 */
	public IntCContainerIterator intIterator(EDataRepresentation storageKind);

	/**
	 * Returns a value of the type Number, from the representation chosen in storageKind, at the index
	 * specified in iIndex
	 * 
	 * @storageKind specifies which kind of storage (eg: raw, normalized)
	 * @iIndex the index of the element
	 * @return the Number
	 */
	public Number get(EDataRepresentation storageKind, int iIndex);

	/**
	 * Returns an iterator on the representation chosen in storageKind
	 * 
	 * @param storageKind
	 *            specifies which kind of storage (eg: raw, normalized)
	 * @return the iterator
	 */
	public Iterator<? extends Number> iterator(EDataRepresentation storageKind);

	/**
	 * Brings any dataset into a format between 0 and 1. This is used for drawing. Works for nominal and
	 * numerical data. Operates with the raw data as basis by default, however when a logarithmized
	 * representation is in the storage this is used (only applies to numerical data). For nominal data the
	 * first value is 0, the last value is 1
	 */
	public void normalize();

	/**
	 * Returns the number of raw data elements
	 * 
	 * @return the number of raw data elements
	 */
	public int size();

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param dataRep
	 */
	public void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep);

}
