package org.caleydo.core.data.collection;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ccontainer.FloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.IntCContainerIterator;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.selection.IVirtualArray;

/**
 * Interface for Storages A Storage is a container that holds various representations of a particular data
 * entity, for example a microarray experiment, or a column on illnesses in a clinical data file. It contains
 * all information considering one such entity, for example, the raw, normalized and logarithmized data as
 * well as metadata, such as the label of the experiment. Only the raw data and some metadata can be specified
 * manually, the rest is computed on on demand. One distinguishes between two basic storage types: numerical
 * and nominal. This is reflected in the two sub-interfaces INumericalSet and INominalSet. After construction
 * one of the setRawData methods has to be called. Notice, that only one setRawData may be called exactly
 * once, since a set is designed to contain only one raw data set at a time.
 * 
 * @author Alexander Lex
 */

public interface IStorage
	extends IUniqueObject, ICollection {

	/**
	 * Set the raw data with data type float
	 * 
	 * @param fArRawData
	 *          a float array containing the raw data
	 */
	public void setRawData(float[] fArRawData);

	/**
	 * Set the raw data with data type int
	 * 
	 * @param fArRawData
	 *          a int array containing the raw data
	 */
	public void setRawData(int[] iArRawData);

	/**
	 * Set the raw data with any type that extends Number
	 * 
	 * @param alNumber
	 */
	public void setRawData(ArrayList<? super Number> alNumber);

	/**
	 * Returns the data type of the raw data
	 * 
	 * @return a value of ERawDataType
	 */
	public ERawDataType getRawDataType();

	/**
	 * Returns a float value from a storage of which the kind has to be specified Use iterator when you want to
	 * iterate over the whole field, it has better performance
	 * 
	 * @param storageKind
	 *          Specify which kind of storage (eg: raw, normalized)
	 * @param iIndex
	 *          The index of the requested Element
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
	 * Returns the float associated with the value in the container through the virtual array. The index
	 * specifies a position in the virtual array, where the actual indices on the container are stored.
	 * 
	 * @param storageKind
	 *          specifies which kind of storage (eg: raw, normalized)
	 * @param iIndex
	 *          the index in the Virtual Array
	 * @param iUniqeID
	 *          the unique id associated with the virtual array
	 * @return the value
	 */
	public float getFloatVA(EDataRepresentation storageKind, int iIndex, int iUniqeID);

	/**
	 * Returns an iterator which iterates over the indices stored in a virtual array and returns values from the
	 * storage based on the ordering in the virtual array.
	 * 
	 * @param storageKind
	 *          specifies which kind of storage (eg: raw, normalized)
	 * @param iUniqeID
	 *          the unique id associated with the virtual array
	 * @return the iterator which iterates over the data based on the rules or order in the virtual array
	 */
	public FloatCContainerIterator floatVAIterator(EDataRepresentation storageKind, int iUniqueID);

	/**
	 * Returns a float value from a storage of which the kind has to be specified Use iterator when you want to
	 * iterate over the whole field, it has better performance
	 * 
	 * @param storageKind
	 *          Specify which kind of storage (eg: raw, normalized, log)
	 * @param iIndex
	 *          The index of the requested Element
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
	 * Returns the int associated with the value in the container through the virtual array. The index specifies
	 * a position in the virtual array, where the actual indices on the container are stored.
	 * 
	 * @param storageKind
	 *          specifies which kind of storage (eg: raw, normalized)
	 * @param iIndex
	 *          the index in the Virtual Array
	 * @param iUniqeID
	 *          the unique id associated with the virtual array
	 * @return the value
	 */
	public int getIntVA(EDataRepresentation storageKind, int iIndex, int iUniqueID);

	/**
	 * Returns an iterator which iterates over the indices stored in a virtual array and returns values from the
	 * storage based on the ordering in the virtual array.
	 * 
	 * @param storageKind
	 *          specifies which kind of storage (eg: raw, normalized)
	 * @param iUniqeID
	 *          the unique id associated with the virtual array
	 * @return the iterator which iterates over the data based on the rules or order in the virtual array
	 */
	public IntCContainerIterator intVAIterator(EDataRepresentation storageKind, int iUniqueID);

	/**
	 * Returns a value of the type Number, from the representation chosen in storageKind, at the index specified
	 * in iIndex
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
	 *          specifies which kind of storage (eg: raw, normalized)
	 * @return the iterator
	 */
	public Iterator<? extends Number> iterator(EDataRepresentation storageKind);

	/**
	 * Returns a value of the type Number, from the representation chosen in storageKind, via a virtual array
	 * 
	 * @iIndex the index in the virtual array
	 * @param iUniqueID
	 *          the unique id associated with the virtual array
	 * @return the Number
	 */
	public Number getNumberVA(EDataRepresentation storageKind, int iIndex, int iUniqueID);

	/**
	 * Returns an iterator on the representation chosen in storageKind via a virtual array
	 * 
	 * @param storageKind
	 *          specifies which kind of storage (eg: raw, normalized)
	 * @param iUniqueID
	 *          the unique id associated with the virtual array
	 * @return the iterator
	 */
	public Iterator<? extends Number> iteratorVA(EDataRepresentation storageKind, int iUniqueID);

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
	 * Set the specified virtual array for the specified instance, does not enable the virtual array
	 * 
	 * @param iUniqueID
	 * @param virtualArray
	 */
	public void setVirtualArray(int iUniqueID, IVirtualArray virtualArray);

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param dataRep
	 */
	public void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep);

}
