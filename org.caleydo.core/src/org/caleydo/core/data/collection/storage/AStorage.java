package org.caleydo.core.data.collection.storage;

import java.util.ArrayList;
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
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Abstact Storage class. Implements all of the methods the different IStorages
 * share
 * 
 * @author Alexander lex
 */
public abstract class AStorage
	extends AUniqueObject
	implements IStorage
{
	protected HashMap<EDataRepresentation, ICContainer> hashCContainers;
	protected HashMap<Integer, IVirtualArray> hashVirtualArrays;
	protected HashMap<Integer, Boolean> hashIsVirtualArrayEnabled;

	protected String sLabel;

	boolean bRawDataSet = false;

	ERawDataType rawDataType = ERawDataType.UNDEFINED;

	/**
	 * Constructor Initializes objects
	 */
	public AStorage(int iUniqueID, IGeneralManager generalManager)
	{
		super(iUniqueID);
		hashCContainers = new HashMap<EDataRepresentation, ICContainer>();
		hashVirtualArrays = new HashMap<Integer, IVirtualArray>();
		hashIsVirtualArrayEnabled = new HashMap<Integer, Boolean>();
		sLabel = new String("Not specified");
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#getRawDataType()
	 */
	@Override
	public ERawDataType getRawDataType()
	{
		return rawDataType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String sLabel)
	{
		this.sLabel = sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#setRawData(float[])
	 */
	@Override
	public void setRawData(float[] fArRawData)
	{

		if (bRawDataSet)
			throw new CaleydoRuntimeException("Raw data was already set, tried to set again.",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		rawDataType = ERawDataType.FLOAT;
		bRawDataSet = true;

		FloatCContainer container = new FloatCContainer(fArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#setRawData(int[])
	 */
	@Override
	public void setRawData(int[] iArRawData)
	{

		if (bRawDataSet)
			throw new CaleydoRuntimeException("Raw data was already set, tried to set again.",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		rawDataType = ERawDataType.INT;
		bRawDataSet = true;

		IntCContainer container = new IntCContainer(iArRawData);
		hashCContainers.put(EDataRepresentation.RAW, container);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.NISet#getFloat(org.caleydo.core.data
	 * .collection.nstorage.EStorageKind, int)
	 */
	@Override
	public float getFloat(EDataRepresentation storageKind, int iIndex)
	{

		if (!hashCContainers.containsKey(storageKind))
			throw new CaleydoRuntimeException("Requested storage kind not produced",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type float",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.NISet#floatIterator(org.caleydo.core
	 * .data.collection.nstorage.EStorageKind)
	 */
	@Override
	public FloatCContainerIterator floatIterator(EDataRepresentation storageKind)
	{

		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type float",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#getFloatVA(org.caleydo.core
	 * .data.collection.storage.EDataRepresentation, int, int)
	 */
	@Override
	public float getFloatVA(EDataRepresentation storageKind, int iIndex, int iUniqueID)
	{
		return getFloat(storageKind, hashVirtualArrays.get(iUniqueID).get(iIndex));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#floatVAIterator(org.caleydo
	 * .core.data.collection.storage.EDataRepresentation, int)
	 */
	@Override
	public FloatCContainerIterator floatVAIterator(EDataRepresentation storageKind,
			int iUniqueID)
	{
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type float",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		FloatCContainer container = (FloatCContainer) hashCContainers.get(storageKind);
		return container.iterator(hashVirtualArrays.get(iUniqueID));
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.data.collection.NISet#getInt(org.caleydo.core.data.
	 * collection.nstorage.EStorageKind, int)
	 */
	@Override
	public int getInt(EDataRepresentation storageKind, int iIndex)
	{
		if (!(hashCContainers.get(storageKind) instanceof IntCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type int",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.get(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.NISet#intIterator(org.caleydo.core.data
	 * .collection.nstorage.EStorageKind)
	 */
	@Override
	public IntCContainerIterator intIterator(EDataRepresentation storageKind)
	{
		if (!(hashCContainers.get(storageKind) instanceof IntCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type int",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#getIntVA(org.caleydo.core.data
	 * .collection.storage.EDataRepresentation, int, int)
	 */
	@Override
	public int getIntVA(EDataRepresentation storageKind, int iIndex, int iUniqueID)
	{
		return getInt(storageKind, hashVirtualArrays.get(iUniqueID).get(iIndex));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#intVAIterator(org.caleydo.core
	 * .data.collection.storage.EDataRepresentation, int)
	 */
	@Override
	public IntCContainerIterator intVAIterator(EDataRepresentation storageKind, int iUniqueID)
	{
		if (!(hashCContainers.get(storageKind) instanceof FloatCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type float",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		IntCContainer container = (IntCContainer) hashCContainers.get(storageKind);
		return container.iterator(hashVirtualArrays.get(iUniqueID));
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.data.collection.IStorage#get(org.caleydo.core.data.
	 * collection.ccontainer.EDataKind, int)
	 */
	@Override
	public Number get(EDataRepresentation storageKind, int iIndex)
	{
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer))
			throw new CaleydoRuntimeException(
					"Requested storage kind is not a subtype of Number",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers
				.get(storageKind);
		return container.get(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#iterator(org.caleydo.core.data
	 * .collection.ccontainer.EDataKind)
	 */
	@Override
	public Iterator<? extends Number> iterator(EDataRepresentation storageKind)
	{
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer))
			throw new CaleydoRuntimeException(
					"Requested storage kind is not a subtype of Number",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers
				.get(storageKind);
		return container.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#getNumberVA(org.caleydo.core
	 * .data.collection.storage.EDataRepresentation, int, int)
	 */
	@Override
	public Number getNumberVA(EDataRepresentation storageKind, int iIndex, int iUniqueID)
	{
		int iContainerIndex = hashVirtualArrays.get(iUniqueID).get(iIndex);
		return get(storageKind, iContainerIndex);		
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#iteratorVA(org.caleydo.core
	 * .data.collection.storage.EDataRepresentation, int)
	 */
	@Override
	public Iterator<? extends Number> iteratorVA(EDataRepresentation storageKind, int iUniqueID)
	{
		if (!(hashCContainers.get(storageKind) instanceof NumericalCContainer))
			throw new CaleydoRuntimeException(
					"Requested storage kind is not a subtype of Number",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		NumericalCContainer<?> container = (NumericalCContainer<?>) hashCContainers
				.get(storageKind);
		return container.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.IStorage#setRawData(java.util.ArrayList)
	 */
	@Override
	public void setRawData(ArrayList<? super Number> alNumber)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#normalize()
	 */
	@Override
	public void normalize()
	{

		EDataRepresentation srcDataKind = EDataRepresentation.RAW;
		if (hashCContainers.containsKey(EDataRepresentation.LOG10))
			srcDataKind = EDataRepresentation.LOG10;

		hashCContainers.put(EDataRepresentation.NORMALIZED, hashCContainers.get(srcDataKind)
				.normalize());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#size()
	 */
	@Override
	public int size()
	{
		return hashCContainers.get(EDataRepresentation.RAW).size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#enableVirtualArray(int)
	 */
	@Override
	public void enableVirtualArray(int iUniqueID)
	{
		if (hashIsVirtualArrayEnabled.get(iUniqueID) == null)
		{
			throw new CaleydoRuntimeException("Virtual array for ID: " + iUniqueID
					+ " is not set", CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		else if (!hashIsVirtualArrayEnabled.get(iUniqueID))
			hashIsVirtualArrayEnabled.put(iUniqueID, true);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.ICollection#disableVirtualArray(int)
	 */
	@Override
	public void disableVirtualArray(int iUniqueID)
	{
		if (hashIsVirtualArrayEnabled.get(iUniqueID) != null)
			hashIsVirtualArrayEnabled.put(iUniqueID, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IStorage#setVirtualArray(int,
	 * org.caleydo.core.data.selection.VirtualArray)
	 */
	@Override
	public void setVirtualArray(int iUniqueID, IVirtualArray virtualArray)
	{
		hashVirtualArrays.put(iUniqueID, virtualArray);
		hashIsVirtualArrayEnabled.put(iUniqueID, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#removeVirtualArray(int)
	 */
	@Override
	public void removeVirtualArray(int iUniqueID)
	{
		hashVirtualArrays.remove(iUniqueID);
		hashIsVirtualArrayEnabled.remove(iUniqueID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#resetVirtualArray(int)
	 */
	@Override
	public void resetVirtualArray(int uniqueID)
	{
		hashVirtualArrays.get(iUniqueID).reset();
	}

}
