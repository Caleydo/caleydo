package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.naming.OperationNotSupportedException;
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
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

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

	private ERawDataType rawDataType;

	private boolean bIsNumerical;

	HashMap<Integer, IVirtualArray> hashStorageVAs;
	HashMap<Integer, IVirtualArray> hashSetVAs;

	HashMap<Integer, Boolean> hashIsVAEnabled;

	/**
	 * Constructor.
	 * 
	 * @param iUniqueID
	 */
	public Set()
	{
		super(GeneralManager.get().getIDManager()
				.createID(EManagedObjectType.SET));
		
		alStorages = new ArrayList<IStorage>();
		hashStorageVAs = new HashMap<Integer, IVirtualArray>();
		hashSetVAs = new HashMap<Integer, IVirtualArray>();
		hashIsVAEnabled = new HashMap<Integer, Boolean>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.ISet#setSetType(org.caleydo.core.data
	 * .collection.ESetType)
	 */
	@Override
	public void setSetType(ESetType setType)
	{
		this.setType = setType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getSetType()
	 */
	@Override
	public ESetType getSetType()
	{
		return setType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#addStorage(int)
	 */
	@Override
	public void addStorage(int iStorageID)
	{
		IStorageManager storageManager = GeneralManager.get().getStorageManager();
		
		if (!storageManager.hasItem(iStorageID))
			throw new CaleydoRuntimeException("Requested Storage with ID " + iStorageID
					+ " does not exist.", CaleydoRuntimeExceptionType.DATAHANDLING);

		addStorage(storageManager.getItem(iStorageID));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.ISet#addStorage(org.caleydo.core.data
	 * .collection.IStorage)
	 */
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
				throw new CaleydoRuntimeException(
						"All storages in a set must be of the same basic type (nunmerical or nominal)",
						CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		alStorages.add(storage);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getStorage(int)
	 */
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
		int iTmp = hashSetVAs.get(iUniqueID).get(iIndex);
		return alStorages.get(iTmp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getSize()
	 */
	@Override
	public int size()
	{
		return alStorages.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#sizeVA(int)
	 */
	@Override
	public int sizeVA(int iUniqueID)
	{
		return hashSetVAs.get(iUniqueID).size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#depth()
	 */
	@Override
	public int depth()
	{
		return alStorages.get(0).size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#depthVA(int)
	 */
	@Override
	public int depthVA(int iUniqueID)
	{
		return hashStorageVAs.get(iUniqueID).size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#normalize()
	 */
	@Override
	public void normalize()
	{

		for (IStorage storage : alStorages)
		{
			storage.normalize();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#normalizeGlobally()
	 */
	@Override
	public void normalizeGlobally()
	{

		for (IStorage storage : alStorages)
		{
			if (storage instanceof INumericalStorage)
			{
				INumericalStorage nStorage = (INumericalStorage) storage;
				try
				{
					nStorage.normalizeWithExternalExtrema(getMin(), getMax());
				}
				catch (OperationNotSupportedException e)
				{
					throw new CaleydoRuntimeException(
							"Tried to normalize globally on a set wich has"
									+ "different storage types",
							CaleydoRuntimeExceptionType.DATAHANDLING);
				}
			}
			else
			{
				throw new CaleydoRuntimeException(
						"Tried to normalize globally on a set wich has"
								+ "different storage types",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.ICollection#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String sLabel)
	{
		this.sLabel = sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IStorage> iterator()
	{
		return alStorages.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getMin()
	 */
	@Override
	public double getMin() throws OperationNotSupportedException
	{
		if (dMin == Double.MAX_VALUE)
			calculateGlobalExtrema();
		return dMin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getMax()
	 */
	@Override
	public double getMax() throws OperationNotSupportedException
	{

		if (dMax == Double.MIN_VALUE)
			calculateGlobalExtrema();
		return dMax;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getRawForNormalized(double)
	 */
	@Override
	public double getRawForNormalized(double dNormalized)
			throws OperationNotSupportedException
	{
		return dNormalized * (getMax() - getMin());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#log10()
	 */
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
				throw new CaleydoRuntimeException(
						"Tried to normalize globally on a set wich has"
								+ "different storage types",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#createStorageVA(int)
	 */
	@Override
	public void createStorageVA(int iUniqueID)
	{
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			hashStorageVAs.put(iUniqueID, new VirtualArray(depth()));

			for (IStorage storage : alStorages)
			{
				storage.setVirtualArray(iUniqueID, hashStorageVAs.get(iUniqueID));
				storage.enableVirtualArray(iUniqueID);
			}
		}
		else
			throw new CaleydoRuntimeException(
					"Virtual array already exists, not creating a new one",
					CaleydoRuntimeExceptionType.DATAHANDLING);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#createStorageVA(int,
	 * java.util.ArrayList)
	 */
	@Override
	public void createStorageVA(int iUniqueID, ArrayList<Integer> iAlSelections)
	{
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			hashStorageVAs.put(iUniqueID, new VirtualArray(depth(), iAlSelections));
			hashIsVAEnabled.put(iUniqueID, false);
			for (IStorage storage : alStorages)
			{
				storage.setVirtualArray(iUniqueID, hashStorageVAs.get(iUniqueID));
				// storage.enableVirtualArray(iUniqueID);
			}
			// enableVirtualArray(iUniqueID);
		}
		else
			throw new CaleydoRuntimeException(
					"Virtual array already exists, not creating a new one",
					CaleydoRuntimeExceptionType.DATAHANDLING);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#createSetVA(int)
	 */
	@Override
	public void createSetVA(int iUniqueID)
	{
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			hashSetVAs.put(iUniqueID, new VirtualArray(depth()));
			hashIsVAEnabled.put(iUniqueID, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#createSetVA(int,
	 * java.util.ArrayList)
	 */
	@Override
	public void createSetVA(int iUniqueID, ArrayList<Integer> iAlSelections)
	{
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			hashSetVAs.put(iUniqueID, new VirtualArray(depth(), iAlSelections));
			hashIsVAEnabled.put(iUniqueID, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#enableVirtualArray(int)
	 * Creates one virtual array for the set and one for the storages
	 */
	@Override
	public void enableVirtualArray(int iUniqueID)
	{
		if (hashIsVAEnabled.get(iUniqueID) == null)
		{
			throw new CaleydoRuntimeException("No such virtual array exists, create it first",
					CaleydoRuntimeExceptionType.DATAHANDLING);
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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.ICollection#disableVirtualArray(int)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#resetVirtualArray()
	 */
	@Override
	public void resetVirtualArray(int iUniqueID)
	{
		hashSetVAs.get(iUniqueID).reset();
		hashStorageVAs.get(iUniqueID).reset();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#removeVirtualArray(int)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getVA(int)
	 */
	@Override
	public IVirtualArray getVA(int iUniqueID)
	{
		if (hashSetVAs.get(iUniqueID) != null)
			return hashSetVAs.get(iUniqueID);
		else if (hashStorageVAs.get(iUniqueID) != null)
			return hashStorageVAs.get(iUniqueID);
		else
			throw new CaleydoRuntimeException("No Virtual Array for that unique id",
					CaleydoRuntimeExceptionType.DATAHANDLING);
	}
	

	private void calculateGlobalExtrema() throws OperationNotSupportedException
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
			throw new OperationNotSupportedException(
					"No minimum or maximum can be calculated " + "on nominal data");

		}
	}



}
