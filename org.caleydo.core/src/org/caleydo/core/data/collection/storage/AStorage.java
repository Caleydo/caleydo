package org.caleydo.core.data.collection.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ICContainer;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.ccontainer.NumericalCContainer;
import org.caleydo.core.data.collection.ccontainer.PrimitiveFloatCContainer;
import org.caleydo.core.data.collection.ccontainer.PrimitiveFloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.PrimitiveIntCContainer;
import org.caleydo.core.data.collection.ccontainer.PrimitiveIntCContainerIterator;
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

		PrimitiveFloatCContainer rawStorage = new PrimitiveFloatCContainer(fArRawData);
		hashCContainers.put(EDataRepresentation.RAW, rawStorage);
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

		PrimitiveIntCContainer rawStorage = new PrimitiveIntCContainer(iArRawData);
		hashCContainers.put(EDataRepresentation.RAW, rawStorage);
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
	@Override
	public PrimitiveFloatCContainerIterator floatIterator(EDataRepresentation storageKind)
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
	@Override
	public int getInt(EDataRepresentation storageKind, int iIndex)
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
	@Override
	public PrimitiveIntCContainerIterator intIterator(EDataRepresentation storageKind)
	{
		if (!(hashCContainers.get(storageKind) instanceof PrimitiveIntCContainer))
			throw new CaleydoRuntimeException("Requested storage kind is not of type int",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		PrimitiveIntCContainer iStorage = (PrimitiveIntCContainer) hashCContainers
				.get(storageKind);
		return iStorage.iterator();
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

		NumericalCContainer<?> iStorage = (NumericalCContainer<?>) hashCContainers
				.get(storageKind);
		return iStorage.get(iIndex);
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

		NumericalCContainer<?> iStorage = (NumericalCContainer<?>) hashCContainers
				.get(storageKind);
		return iStorage.iterator();
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

}
