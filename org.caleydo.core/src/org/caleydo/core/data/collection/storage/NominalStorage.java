package org.caleydo.core.data.collection.storage;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ccontainer.NominalCContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

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
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.STORAGE_NOMINAL));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRawNominalData(ArrayList<T> alData)
	{
		if (bRawDataSet)
			throw new IllegalStateException("Raw data was already set, tried to set again.");

		bRawDataSet = true;

		if (alData.isEmpty())
		{
			throw new IllegalStateException("Raw Data is empty");
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

	@SuppressWarnings("unchecked")
	@Override
	public void setPossibleValues(ArrayList<T> alPossibleValues)
	{
		if (alPossibleValues.isEmpty())
		{
			throw new IllegalStateException("Raw Data is empty");
		}
		else
		{
			if (hashCContainers.get(EDataRepresentation.RAW) instanceof NominalCContainer)
			{
				throw new IllegalStateException("Raw data format does not correspond to"
						+ "specified value list.");
			}
			else
			{
				((NominalCContainer) hashCContainers.get(EDataRepresentation.RAW))
						.setPossibleValues(alPossibleValues);
			}
		}
	}

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
		return ((NominalCContainer<T>) hashCContainers.get(EDataRepresentation.RAW))
				.getHistogram();
	}

}
