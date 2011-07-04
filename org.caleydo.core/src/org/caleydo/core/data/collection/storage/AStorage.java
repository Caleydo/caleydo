package org.caleydo.core.data.collection.storage;

import java.util.EnumMap;
import java.util.Iterator;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.IStorage;
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
	implements IStorage {
	protected EnumMap<EDataRepresentation, ICContainer> hashCContainers;

	protected String sLabel;

	boolean bRawDataSet = false;

	ERawDataType rawDataType = ERawDataType.UNDEFINED;

	EDataRepresentation dataRep;

	/**
	 * Constructor Initializes objects
	 */
	public AStorage(int iUniqueID) {
		super(iUniqueID);

		GeneralManager.get().getStorageManager().registerItem(this);

		hashCContainers = new EnumMap<EDataRepresentation, ICContainer>(EDataRepresentation.class);
		sLabel = new String("Not specified");
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
		this.sLabel = sLabel;
	}

	@Override
	public String getLabel() {
		return sLabel;
	}

	/**
	 * Set the raw data with data type float
	 * 
	 * @param fArRawData
	 *            a float array containing the raw data
	 */
	public void setRawData(float[] fArRawData) {

		if (bRawDataSet)
			throw new IllegalStateException("Raw data was already set in Storage " + uniqueID
				+ " , tried to set again.");

		rawDataType = ERawDataType.FLOAT;
		bRawDataSet = true;

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

		if (bRawDataSet)
			throw new IllegalStateException("Raw data was already set, tried to set again.");

		rawDataType = ERawDataType.INT;
		bRawDataSet = true;

		IntCContainer container = new IntCContainer(iArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
	}

	public void setCertaintyData(float[] certaintyData) {

		if (bRawDataSet)
			throw new IllegalStateException("Raw data was already set in Storage " + uniqueID
				+ " , tried to set again.");

		rawDataType = ERawDataType.FLOAT;
		bRawDataSet = true;

		FloatCContainer container = new FloatCContainer(certaintyData);
		hashCContainers.put(EDataRepresentation.CERTAINTY, container);
	}

	@Override
	public float getFloat(EDataRepresentation storageKind, int iIndex) {
		if (!hashCContainers.containsKey(storageKind))
			throw new IllegalArgumentException("Requested storage kind not produced");
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type float");

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	@Override
	public FloatCContainerIterator floatIterator(EDataRepresentation storageKind) {

		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type float");

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.iterator();
	}

	@Override
	public int getInt(EDataRepresentation storageKind, int iIndex) {
		if (!(hashCContainers.get(storageKind) instanceof IntCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type int");

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	@Override
	public IntCContainerIterator intIterator(EDataRepresentation storageKind) {
		if (!(hashCContainers.get(storageKind) instanceof IntCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type int");

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.iterator();
	}

	@Override
	public Number get(EDataRepresentation storageKind, int iIndex) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer<?>))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	@Override
	public Iterator<? extends Number> iterator(EDataRepresentation storageKind) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer<?>))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.iterator();
	}

	@Override
	public int size() {
		return hashCContainers.get(EDataRepresentation.RAW).size();
	}

	@Override
	public String toString() {
		return "Storage for " + getRawDataType() + ", size: " + size();
	}

}
