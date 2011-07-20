package org.caleydo.core.data.collection.storage;

import java.util.EnumMap;
import java.util.Iterator;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ICollection;
import org.caleydo.core.data.collection.ccontainer.FloatCContainer;
import org.caleydo.core.data.collection.ccontainer.FloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.ICContainer;
import org.caleydo.core.data.collection.ccontainer.IntCContainer;
import org.caleydo.core.data.collection.ccontainer.IntCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.NumericalCContainer;
import org.caleydo.core.manager.GeneralManager;

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

public abstract class AStorage
	extends AUniqueObject
	implements ICollection {
	protected EnumMap<EDataRepresentation, ICContainer> hashCContainers;

	protected String label;

	boolean isRawDataSet = false;

	ERawDataType rawDataType = ERawDataType.UNDEFINED;

	EDataRepresentation dataRep;

	/**
	 * Constructor Initializes objects
	 */
	public AStorage(int iUniqueID) {
		super(iUniqueID);

		GeneralManager.get().getStorageManager().registerItem(this);

		hashCContainers = new EnumMap<EDataRepresentation, ICContainer>(EDataRepresentation.class);
		label = new String("Not specified");
	}

	/**
	 * Returns the data type of the raw data
	 * 
	 * @return a value of ERawDataType
	 */
	public ERawDataType getRawDataType() {
		return rawDataType;
	}

	@Override
	public void setLabel(String sLabel) {
		this.label = sLabel;
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * Set the raw data with data type float
	 * 
	 * @param fArRawData
	 *            a float array containing the raw data
	 */
	public void setRawData(float[] fArRawData) {

		if (isRawDataSet)
			throw new IllegalStateException("Raw data was already set in Storage " + uniqueID
				+ " , tried to set again.");

		rawDataType = ERawDataType.FLOAT;
		isRawDataSet = true;

		FloatCContainer container = new FloatCContainer(fArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
	}

	/**
	 * Set the raw data with data type int
	 * 
	 * @param fArRawData
	 *            a int array containing the raw data
	 */
	public void setRawData(int[] iArRawData) {

		if (isRawDataSet)
			throw new IllegalStateException("Raw data was already set, tried to set again.");

		rawDataType = ERawDataType.INT;
		isRawDataSet = true;

		IntCContainer container = new IntCContainer(iArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
	}

	public void setUncertaintyData(float[] uncertaintyData) {
		if (hashCContainers.containsKey(EDataRepresentation.UNCERTAINTY_RAW))
			throw new IllegalStateException("Certainty data was already set in Storage " + uniqueID
				+ " , tried to set again.");

		FloatCContainer container = new FloatCContainer(uncertaintyData);
		hashCContainers.put(EDataRepresentation.UNCERTAINTY_RAW, container);
	}


	public boolean containsDataRepresentation(EDataRepresentation dataRepresentation) {
		return hashCContainers.containsKey(dataRepresentation);
	}

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
	public float getFloat(EDataRepresentation storageKind, int iIndex) {
		if (!hashCContainers.containsKey(storageKind))
			throw new IllegalArgumentException("Requested storage kind " + storageKind +" not produced");
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type float");

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	/**
	 * Returns a iterator to the storage of which the kind has to be specified Good performance
	 * 
	 * @param storageKind
	 * @return
	 */
	public FloatCContainerIterator floatIterator(EDataRepresentation storageKind) {

		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type float");

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.iterator();
	}

	/**
	 * Returns an int value from a storage of which the kind has to be specified Use iterator when you want to
	 * iterate over the whole field, it has better performance
	 * 
	 * @param storageKind
	 *            Specify which kind of storage (eg: raw, normalized, log)
	 * @param iIndex
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public int getInt(EDataRepresentation storageKind, int iIndex) {
		if (!(hashCContainers.get(storageKind) instanceof IntCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type int");

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	/**
	 * Returns a iterator to the storage of which the kind has to be specified Good performance
	 * 
	 * @param storageKind
	 * @return
	 */
	public IntCContainerIterator intIterator(EDataRepresentation storageKind) {
		if (!(hashCContainers.get(storageKind) instanceof IntCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type int");

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.iterator();
	}

	/**
	 * Returns a value of the type Number, from the representation chosen in storageKind, at the index
	 * specified in iIndex
	 * 
	 * @storageKind specifies which kind of storage (eg: raw, normalized)
	 * @iIndex the index of the element
	 * @return the Number
	 */
	public Number get(EDataRepresentation storageKind, int iIndex) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer<?>))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	/**
	 * Returns an iterator on the representation chosen in storageKind
	 * 
	 * @param storageKind
	 *            specifies which kind of storage (eg: raw, normalized)
	 * @return the iterator
	 */
	public Iterator<? extends Number> iterator(EDataRepresentation storageKind) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer<?>))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.iterator();
	}

	/**
	 * Returns the number of raw data elements
	 * 
	 * @return the number of raw data elements
	 */
	public int size() {
		return hashCContainers.get(EDataRepresentation.RAW).size();
	}

	@Override
	public String toString() {
		return "Storage for " + getRawDataType() + ", size: " + size();
	}

	/**
	 * Brings any dataset into a format between 0 and 1. This is used for drawing. Works for nominal and
	 * numerical data. Operates with the raw data as basis by default, however when a logarithmized
	 * representation is in the storage this is used (only applies to numerical data). For nominal data the
	 * first value is 0, the last value is 1
	 */
	public abstract void normalize();

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param dataRep
	 */
	public abstract void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep);

}
