package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Implementation of the ISet interface
 * 
 * @author Alexander Lex
 */
public class Set
	extends AUniqueObject
	implements ISet
{

	private ESetType setType;

	private ArrayList<IStorage> alStorages;

	private String sLabel;

	private double dMin = Double.MAX_VALUE;

	private double dMax = Double.MIN_VALUE;

	private int iDepth = 0;

	private ERawDataType rawDataType;

	private boolean bIsNumerical;

	HashMap<Integer, IVirtualArray> hashStorageVAs;
	HashMap<Integer, IVirtualArray> hashSetVAs;

	HashMap<Integer, Boolean> hashIsVAEnabled;

	public Set()
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.SET));

		GeneralManager.get().getSetManager().registerItem(this);

		alStorages = new ArrayList<IStorage>();
		hashStorageVAs = new HashMap<Integer, IVirtualArray>();
		hashSetVAs = new HashMap<Integer, IVirtualArray>();
		hashIsVAEnabled = new HashMap<Integer, Boolean>();
	}

	@Override
	public void setSetType(ESetType setType)
	{
		this.setType = setType;
	}

	@Override
	public ESetType getSetType()
	{
		return setType;
	}

	@Override
	public void addStorage(int iStorageID)
	{
		IStorageManager storageManager = GeneralManager.get().getStorageManager();

		if (!storageManager.hasItem(iStorageID))
			throw new IllegalArgumentException("Requested Storage with ID " + iStorageID
					+ " does not exist.");

		addStorage(storageManager.getItem(iStorageID));
	}

	@Override
	public void addStorage(IStorage storage)
	{
		if (alStorages.isEmpty())
		{
			// iColumnLength = storage.size();
			// rawDataType = storage.getRawDataType();
			if (storage instanceof INumericalStorage)
				bIsNumerical = true;
			else
				bIsNumerical = false;

			rawDataType = storage.getRawDataType();
			iDepth = storage.size();
		}
		else
		{
			// if (storage.size() != iColumnLength)
			// throw new
			// CaleydoRuntimeException("Storages must be of the same length",
			// CaleydoRuntimeExceptionType.DATAHANDLING);
			// if (rawDataType != storage.getRawDataType())
			// throw new CaleydoRuntimeException(
			// "Storages in a set must have the same raw data type",
			// CaleydoRuntimeExceptionType.DATAHANDLING);
			if (!bIsNumerical && storage instanceof INumericalStorage)
				throw new IllegalArgumentException(
						"All storages in a set must be of the same basic type (nunmerical or nominal)");
			if (rawDataType != storage.getRawDataType())
				throw new IllegalArgumentException(
						"All storages in a set must have the same raw data type");
			if (iDepth != storage.size())
				throw new IllegalArgumentException(
						"All storages in a set must be of the same length");
		}
		alStorages.add(storage);
	}

	@Override
	public IStorage get(int iIndex)
	{

		return alStorages.get(iIndex);
	}

	@Override
	public SetIterator VAIterator(int uniqueID)
	{
		return new SetIterator(alStorages, hashSetVAs.get(iUniqueID));
	}

	@Override
	public IStorage getStorageFromVA(int iUniqueID, int iIndex)
	{
		if (hashSetVAs.containsKey(iUniqueID))
		{
			int iTmp = hashSetVAs.get(iUniqueID).get(iIndex);
			return alStorages.get(iTmp);
		}
		else
		{
			throw new IllegalArgumentException("No such virtual array " + iUniqueID + " registered for storages");
		}
	}

	@Override
	public int size()
	{
		return alStorages.size();
	}

	@Override
	public int sizeVA(int iUniqueID)
	{
		if (hashSetVAs.containsKey(iUniqueID))
			return hashSetVAs.get(iUniqueID).size();
		else if (hashStorageVAs.containsKey(iUniqueID))
			return hashStorageVAs.get(iUniqueID).size();
		else
			throw new IllegalArgumentException("No such virtual array has been registered:" + iUniqueID);

	}

	@Override
	public int depth()
	{
		return iDepth;
	}

	@Override
	public void normalize()
	{

		for (IStorage storage : alStorages)
		{
			storage.normalize();
		}
	}

	@Override
	public void normalizeGlobally()
	{

		for (IStorage storage : alStorages)
		{
			if (storage instanceof INumericalStorage)
			{
				INumericalStorage nStorage = (INumericalStorage) storage;

				nStorage.normalizeWithExternalExtrema(getMin(), getMax());

			}
			else
			{
				throw new UnsupportedOperationException(
						"Tried to normalize globally on a set wich has"
								+ "contains nominal storages, currently not supported!");
			}
		}
	}

	@Override
	public void setLabel(String sLabel)
	{
		this.sLabel = sLabel;
	}

	@Override
	public String getLabel()
	{
		return sLabel;
	}

	@Override
	public Iterator<IStorage> iterator()
	{
		return alStorages.iterator();
	}

	@Override
	public double getMin()
	{
		if (dMin == Double.MAX_VALUE)
			calculateGlobalExtrema();
		return dMin;
	}

	@Override
	public double getMax()
	{

		if (dMax == Double.MIN_VALUE)
			calculateGlobalExtrema();
		return dMax;
	}

	@Override
	public double getRawForNormalized(double dNormalized)
	{
		if(dNormalized == 0)
			return getMin();
	//	if(getMin() > 0)
			return getMin() + dNormalized * (getMax() - getMin());
	//	return (dNormalized) * (getMax() + getMin());
	}
	
	public double getNormalizedForRaw(double dRaw)
	{
		if(dRaw < getMin() || dRaw > getMax())
			throw new IllegalArgumentException("Value may not be smaller than min or larger than max");
		
		return (dRaw - getMin()) / (getMax() - getMin()) ;
	}

	@Override
	public void log10()
	{

		for (IStorage storage : alStorages)
		{
			if (storage instanceof INumericalStorage)
			{
				INumericalStorage nStorage = (INumericalStorage) storage;
				nStorage.log10();
			}
			else
			{
				throw new UnsupportedOperationException(
						"Tried to calcualte log values on a set wich has"
								+ "contains nominal storages. This is not possible!");
			}
		}
	}

	@Override
	public int createStorageVA()
	{
		VirtualArray virtualArray = new VirtualArray(depth());
		return doCreateStorageVA(virtualArray);

	}

	@Override
	public int createStorageVA(List<Integer> iAlSelections)
	{
		IVirtualArray virtualArray = new VirtualArray(depth(), iAlSelections);
		return doCreateStorageVA(virtualArray);
	}

	@Override
	public int createSetVA()
	{
		VirtualArray virtualArray = new VirtualArray(depth());
		int iUniqueID = virtualArray.getID();
		hashSetVAs.put(iUniqueID, virtualArray);
		hashIsVAEnabled.put(iUniqueID, false);
		return iUniqueID;
	}

	@Override
	public int createSetVA(ArrayList<Integer> iAlSelections)
	{
		VirtualArray virtualArray = new VirtualArray(depth(), iAlSelections);
		int iUniqueID = virtualArray.getID();
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			hashSetVAs.put(iUniqueID, virtualArray);
			hashIsVAEnabled.put(iUniqueID, false);
		}
		return iUniqueID;
	}

	// TODO obsolete?
	@Override
	@Deprecated
	public void enableVirtualArray(int iUniqueID)
	{
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			throw new IllegalStateException("No such virtual array exists, create it first");
		}
		else
		{
			hashIsVAEnabled.put(iUniqueID, true);
			if (hashStorageVAs.containsKey(iUniqueID))
			{
				for (IStorage storage : alStorages)
					storage.enableVirtualArray(iUniqueID);
			}
		}
	}

	@Override
	public void disableVirtualArray(int iUniqueID)
	{
		if (hashStorageVAs.get(iUniqueID) != null && hashSetVAs.get(iUniqueID) != null)
		{
			hashIsVAEnabled.put(iUniqueID, false);
			if (hashStorageVAs.containsKey(iUniqueID))
			{
				for (IStorage storage : alStorages)
					storage.disableVirtualArray(iUniqueID);
			}
		}
	}

	@Override
	public void resetVirtualArray(int iUniqueID)
	{
		if (hashSetVAs.containsKey(iUniqueID))
		{
			hashSetVAs.get(iUniqueID).reset();
			return;
		}

		if (hashStorageVAs.containsKey(iUniqueID))
			hashStorageVAs.get(iUniqueID).reset();
	}

	@Override
	public void removeVirtualArray(int iUniqueID)
	{
		hashSetVAs.remove(iUniqueID);
		for (IStorage storage : alStorages)
		{
			storage.removeVirtualArray(iUniqueID);
		}
		hashStorageVAs.remove(iUniqueID);
	}

	@Override
	public IVirtualArray getVA(int iUniqueID)
	{
		if (hashSetVAs.containsKey(iUniqueID))
			return hashSetVAs.get(iUniqueID);
		else if (hashStorageVAs.containsKey(iUniqueID))
			return hashStorageVAs.get(iUniqueID);
		else
			throw new IllegalArgumentException("No Virtual Array for the unique id: " + iUniqueID);
	}

	private void calculateGlobalExtrema()
	{
		double dTemp = 0.0;
		if (alStorages.get(0) instanceof INumericalStorage)
		{
			for (IStorage storage : alStorages)
			{
				INumericalStorage nStorage = (INumericalStorage) storage;
				dTemp = nStorage.getMin();
				if (dTemp < dMin)
					dMin = dTemp;
				dTemp = nStorage.getMax();
				if (dTemp > dMax)
					dMax = dTemp;
			}
		}
		else if (alStorages.get(0) instanceof INominalStorage)
		{
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
					+ "on nominal data");

		}
	}

	private int doCreateStorageVA(IVirtualArray virtualArray)
	{
		int iUniqueID = virtualArray.getID();
		hashStorageVAs.put(iUniqueID, virtualArray);
		hashIsVAEnabled.put(iUniqueID, false);
		for (IStorage storage : alStorages)
		{
			storage.setVirtualArray(iUniqueID, hashStorageVAs.get(iUniqueID));
		}
		return iUniqueID;
	}
}
