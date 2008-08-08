package org.caleydo.core.data.collection.storage;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ccontainer.NominalCContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implementation of INominalStorage
 * 
 * @author Alexander Lex
 * 
 * @param <T> the type, anything is ok
 */
public class NominalStorage<T>
	extends AStorage
	implements INominalStorage<T>
{

	/**
	 * Constructor
	 */
	public NominalStorage()
	{
		super(GeneralManager.get().getIDManager()
				.createID(EManagedObjectType.STORAGE_NOMINAL));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.INominalSet#setRawData(java.util.ArrayList
	 * )
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setRawNominalData(ArrayList<T> alData)
	{
		if (bRawDataSet)
			throw new CaleydoRuntimeException("Raw data was already set, tried to set again.",
					CaleydoRuntimeExceptionType.DATAHANDLING);

		bRawDataSet = true;

		if (alData.isEmpty())
		{
			throw new CaleydoRuntimeException("Raw Data is empty",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		else
		{
			if (alData.get(0) instanceof String)
				rawDataType = ERawDataType.STRING;

			else
				rawDataType = ERawDataType.OBJECT;

			NominalCContainer sStorage = new NominalCContainer(alData);
			hashCContainers.put(EDataRepresentation.RAW, sStorage);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.INominalSet#setPossibleValues(java.util
	 * .ArrayList)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setPossibleValues(ArrayList<T> alPossibleValues)
	{
		if (alPossibleValues.isEmpty())
		{
			throw new CaleydoRuntimeException("Raw Data is empty",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		else
		{
			if (hashCContainers.get(EDataRepresentation.RAW) instanceof NominalCContainer)
			{
				throw new CaleydoRuntimeException("Raw data format does not correspond to"
						+ "specified value list.", CaleydoRuntimeExceptionType.DATAHANDLING);
			}
			else
			{
				((NominalCContainer) hashCContainers.get(EDataRepresentation.RAW))
						.setPossibleValues(alPossibleValues);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.INominalStorage#get(org.caleydo.core
	 * .data.collection.ccontainer.EDataKind, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getRaw(int index)
	{
		return ((NominalCContainer<T>) (hashCContainers.get(EDataRepresentation.RAW)))
				.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<T, Float> getHistogram()
	{
		return ((NominalCContainer<T>)hashCContainers.get(EDataRepresentation.RAW)).getHistogram();
	}
	
}
