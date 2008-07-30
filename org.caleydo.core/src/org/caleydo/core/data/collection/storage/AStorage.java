package org.caleydo.core.data.collection.storage;

import java.util.HashMap;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ICContainer;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.ccontainer.EDataKind;
import org.caleydo.core.data.collection.ccontainer.PrimitiveFloatCContainer;
import org.caleydo.core.data.collection.ccontainer.PrimitiveFloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.PrimitiveIntCContainer;
import org.caleydo.core.data.collection.ccontainer.PrimitiveIntCContainerIterator;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * @author Alexander lex Abstact Storage class. Implements all of the methods
 *         the different IStorages share
 */
public abstract class AStorage
	extends AUniqueObject
	implements IStorage
{

	protected HashMap<EDataKind, ICContainer> hashCContainers;

	protected String sLabel;

	boolean bRawDataSet = false;

	ERawDataType rawDataType = ERawDataType.UNDEFINED;

	/**
	 * Constructor Initializes objects
	 */
	public AStorage(int iUniqueID, IGeneralManager generalManager)
	{

		super(iUniqueID);
		hashCContainers = new HashMap<EDataKind, ICContainer>();
		sLabel = new String("Not specified");
	}

	public ERawDataType getRawDataType()
	{

		return rawDataType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#setLabel(java.lang.String)
	 */
	public void setLabel(String sLabel)
	{

		this.sLabel = sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#getLabel()
	 */
	public String getLabel()
	{

		return sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#setRawData(float[])
	 */
	public void setRawData(float[] fArRawData)
	{

		if (bRawDataSet)
			throw new CaleydoRuntimeException("Raw data was already set, tried to set again.",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		rawDataType = ERawDataType.FLOAT;
		bRawDataSet = true;

		PrimitiveFloatCContainer rawStorage = new PrimitiveFloatCContainer(fArRawData);
		hashCContainers.put(EDataKind.RAW, rawStorage);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#setRawData(int[])
	 */
	public void setRawData(int[] iArRawData)
	{

		if (bRawDataSet)
			throw new CaleydoRuntimeException("Raw data was already set, tried to set again.",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		rawDataType = ERawDataType.INT;
		bRawDataSet = true;

		PrimitiveIntCContainer rawStorage = new PrimitiveIntCContainer(iArRawData);
		hashCContainers.put(EDataKind.RAW, rawStorage);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.NISet#getFloat(org.caleydo.core.data
	 * .collection.nstorage.EStorageKind, int)
	 */
	public float getFloat(EDataKind storageKind, int iIndex)
	{

		if (!hashCContainers.containsKey(storageKind))
			throw new CaleydoRuntimeException("Requested storage kind not produced",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		if (!(hashCContainers.get(storageKind) instanceof PrimitiveFloatCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type float",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		PrimitiveFloatCContainer fStorage = (PrimitiveFloatCContainer) hashCContainers
				.get(storageKind);
		return fStorage.get(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.NISet#floatIterator(org.caleydo.core
	 * .data.collection.nstorage.EStorageKind)
	 */
	public PrimitiveFloatCContainerIterator floatIterator(EDataKind storageKind)
	{

		if (!(hashCContainers.get(storageKind) instanceof PrimitiveFloatCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type float",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		PrimitiveFloatCContainer fStorage = (PrimitiveFloatCContainer) hashCContainers
				.get(storageKind);
		return fStorage.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.data.collection.NISet#getInt(org.caleydo.core.data.
	 * collection.nstorage.EStorageKind, int)
	 */
	public int getInt(EDataKind storageKind, int iIndex)
	{

		if (!(hashCContainers.get(storageKind) instanceof PrimitiveIntCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type int",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		PrimitiveIntCContainer iStorage = (PrimitiveIntCContainer) hashCContainers
				.get(storageKind);
		return iStorage.get(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.NISet#intIterator(org.caleydo.core.data
	 * .collection.nstorage.EStorageKind)
	 */
	public PrimitiveIntCContainerIterator intIterator(EDataKind storageKind)
	{

		if (!(hashCContainers.get(storageKind) instanceof PrimitiveIntCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type int",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		PrimitiveIntCContainer iStorage = (PrimitiveIntCContainer) hashCContainers
				.get(storageKind);
		return iStorage.iterator();
	}

	// // TODO
	//
	// public void getMinFloat()
	// {
	// //hashCContainers.get(EDataKind.RAW).getMinFloat();
	// }
	//	
	// public void getMaxFloat()
	// {
	//		
	// }
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.NISet#normalize()
	 */
	public void normalize()
	{

		EDataKind srcDataKind = EDataKind.RAW;
		if (hashCContainers.containsKey(EDataKind.LOG10))
			srcDataKind = EDataKind.LOG10;

		hashCContainers
				.put(EDataKind.NORMALIZED, hashCContainers.get(srcDataKind).normalize());
	}

	public int size()
	{

		return hashCContainers.get(EDataKind.RAW).size();
	}

}
