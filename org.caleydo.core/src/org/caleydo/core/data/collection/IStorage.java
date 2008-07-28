package org.caleydo.core.data.collection;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ccontainer.EDataKind;
import org.caleydo.core.data.collection.ccontainer.PrimitiveFloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.PrimitiveIntCContainerIterator;
import org.caleydo.core.data.collection.storage.ERawDataType;


/**
 * Interface for Storages
 * 
 * A Storage is a container that holds various representations of a particular data entity,
 * for example a microarray experiment, or a column on illnesses in a clinical 
 * data file.
 *
 * It contains all information considering one such entity, for example, the raw,
 * normalized and logarithmized data as well as metadata, such as the label of the experiment.
 * 
 * Only the raw data and some metadata can be specified manually, the rest is computed on
 * on demand.
 * 
 * One distinguishes between two basic storage types: numerical and nominal. This is reflected 
 * in the two sub-interfaces INumericalSet and INominalSet.
 * 
 * After construction one of the setRawData methods has to be called. Notice, that only one
 * setRawData may be called exactly once, since a set is designed to contain only one raw
 * data set at a time.
 * 
 * @author Alexander Lex
 *
 */

public interface IStorage 
extends IUniqueObject
{
		
	/**
	 * Set the raw data with data type float
	 * 
	 * @param fArRawData a float array containing the raw data
	 */
	public void setRawData(float[] fArRawData);
	
	/**
	 * Set the raw data with data type int
	 * 
	 * @param fArRawData a int array containing the raw data
	 */
	public void setRawData(int[] iArRawData);

	/**
	 * Returns the data type of the raw data
	 * 
	 * @return a value of ERawDataType
	 */
	public ERawDataType getRawDataType();
	
	/**
	 * Set the label of a set.
	 * A label is for example the name of the column, or the identifier for the dataset
	 * 
	 * It is used for printing identifications 
	 * 
	 * @param sLabel the name of the label
	 */
	public void setLabel(String sLabel);
	
	/**
	 * Returns the label. If nothing was specified with setLabel it returns an empty string.
	 * 
	 * @return
	 */
	public String getLabel();
	
	
	/**
	 * Returns a float value from a storage of which the kind has to be specified
	 * Use iterator when you want to iterate over the whole field, it has better
	 * performance
	 * 
	 * @param storageKind Specify which kind of storage (eg: raw, normalized, log)
	 * @param iIndex The index of the requested Element
	 * @return The associated value
	 */
	public float getFloat(EDataKind storageKind, int iIndex);
	
	/**
	 * Returns a iterator to the storage of which the kind has to be specified
	 * Good performance
	 * 
	 * @param storageKind
	 * @return
	 */
	public PrimitiveFloatCContainerIterator floatIterator(EDataKind storageKind);
	
	/**
	 * Returns a float value from a storage of which the kind has to be specified
	 * Use iterator when you want to iterate over the whole field, it has better
	 * performance
	 * 
	 * @param storageKind Specify which kind of storage (eg: raw, normalized, log)
	 * @param iIndex The index of the requested Element
	 * @return The associated value
	 */
	public float getInt(EDataKind storageKind, int iIndex);
	
	/**
	 * Returns a iterator to the storage of which the kind has to be specified
	 * Good performance
	 * 
	 * @param storageKind
	 * @return
	 */
	public PrimitiveIntCContainerIterator intIterator(EDataKind storageKind);
	
	
	/**
	 * Brings any dataset into a format between 0 and 1. This is used for drawing. Works for nominal
	 * and numerical data.
	 * 
	 * For nominal data the first value is 0, the last value is 1
	 */
	public void normalize();
	
	
	
}
