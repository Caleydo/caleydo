package org.caleydo.core.data.collection.storage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.ccontainer.FloatCContainer;
import org.caleydo.core.data.collection.ccontainer.FloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.ICContainer;
import org.caleydo.core.data.collection.ccontainer.IntCContainer;
import org.caleydo.core.data.collection.ccontainer.IntCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.NumericalCContainer;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Abstact Storage class. Implements all of the methods the different IStorages share
 * 
 * @author Alexander lex
 */
public abstract class AStorage
	extends AUniqueObject
	implements IStorage {
	protected EnumMap<EDataRepresentation, ICContainer> hashCContainers;
	protected HashMap<Integer, IVirtualArray> hashVirtualArrays;

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
		hashVirtualArrays = new HashMap<Integer, IVirtualArray>();
		sLabel = new String("Not specified");
	}

	@Override
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

	@Override
	public void setRawData(float[] fArRawData) {

		if (bRawDataSet)
			throw new IllegalStateException("Raw data was already set in Storage " + iUniqueID
				+ " , tried to set again.");

		rawDataType = ERawDataType.FLOAT;
		bRawDataSet = true;

		FloatCContainer container = new FloatCContainer(fArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
	}

	@Override
	public void setRawData(int[] iArRawData) {

		if (bRawDataSet)
			throw new IllegalStateException("Raw data was already set, tried to set again.");

		rawDataType = ERawDataType.INT;
		bRawDataSet = true;

		IntCContainer container = new IntCContainer(iArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
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
	public float getFloatVA(EDataRepresentation storageKind, int iIndex, int iUniqueID) {
		return getFloat(storageKind, hashVirtualArrays.get(iUniqueID).get(iIndex));
	}

	@Override
	public FloatCContainerIterator floatVAIterator(EDataRepresentation storageKind, int iUniqueID) {
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type float");

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.iterator(hashVirtualArrays.get(iUniqueID));
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
	public int getIntVA(EDataRepresentation storageKind, int iIndex, int iUniqueID) {
		return getInt(storageKind, hashVirtualArrays.get(iUniqueID).get(iIndex));
	}

	@Override
	public IntCContainerIterator intVAIterator(EDataRepresentation storageKind, int iUniqueID) {
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new IllegalArgumentException("Requested storage kind is not of type float");

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.iterator(hashVirtualArrays.get(iUniqueID));
	}

	@Override
	public Number get(EDataRepresentation storageKind, int iIndex) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	@Override
	public Iterator<? extends Number> iterator(EDataRepresentation storageKind) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.iterator();
	}

	@Override
	public Number getNumberVA(EDataRepresentation storageKind, int iIndex, int iUniqueID) {
		int iContainerIndex = hashVirtualArrays.get(iUniqueID).get(iIndex);
		return get(storageKind, iContainerIndex);
	}

	@Override
	public Iterator<? extends Number> iteratorVA(EDataRepresentation storageKind, int iUniqueID) {
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer<?>))
			throw new IllegalArgumentException("Requested storage kind is not a subtype of Number");

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers.get(storageKind);
		return container.iterator();
	}

	@Override
	public void setRawData(ArrayList<? super Number> alNumber) {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public int size() {
		return hashCContainers.get(EDataRepresentation.RAW).size();
	}

	@Override
	public void setVirtualArray(int iUniqueID, IVirtualArray virtualArray) {
		hashVirtualArrays.put(iUniqueID, virtualArray);
	}

	@Override
	public void removeVirtualArray(int iUniqueID) {
		hashVirtualArrays.remove(iUniqueID);
	}

	@Override
	public void resetVirtualArray(int uniqueID) {
		hashVirtualArrays.get(iUniqueID).reset();
	}

}
