package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implementation of the ISet interface
 * 
 * @author Alexander Lex
 *
 */
public class Set
extends AUniqueManagedObject
implements ISet
{

	private ESetType setType;
	
	private ArrayList<IStorage> alStorages;
	
	private String sLabel;
	
	/**
	 * Constructor
	 * 
	 * @param iUniqueID
	 * @param generalManager
	 */
	public Set(int iUniqueID, IGeneralManager generalManager)
	{
		super(iUniqueID, generalManager);
		alStorages = new ArrayList<IStorage>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setSetType(org.caleydo.core.data.collection.ESetType)
	 */
	public void setSetType(ESetType setType)
	{
		this.setType = setType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getSetType()
	 */
	public ESetType getSetType()
	{
		return setType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueManagedObject#getBaseType()
	 */
	public ManagerObjectType getBaseType() 
	{
		return ManagerObjectType.SET;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#addStorage(int)
	 */
	public void addStorage(int iStorageID) 
	{
		if(!generalManager.getStorageManager().hasItem(iStorageID))
			throw new CaleydoRuntimeException("Requested Storage with ID " + iStorageID +
					" does not exist.", 
					CaleydoRuntimeExceptionType.DATAHANDLING);
		alStorages.add(generalManager.getStorageManager().getStorage(iStorageID));		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#addStorage(org.caleydo.core.data.collection.IStorage)
	 */
	public void addStorage(IStorage storage) 
	{
		alStorages.add(storage);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getStorage(int)
	 */
	public IStorage getStorage(int iIndex) 
	{
		return alStorages.get(iIndex);
	}
	
	public int getSize()
	{
		return alStorages.size();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#normalize()
	 */
	public void normalize()
	{
		for(IStorage storage : alStorages)
		{
			storage.normalize();
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#normalizeGlobally()
	 */
	public void normalizeGlobally()
	{
		double dMin = Double.MAX_VALUE;
		double dMax = Double.MIN_VALUE;
		double dTemp = 0.0;
		if(alStorages.get(0) instanceof INumericalStorage)
		{
			for(IStorage storage : alStorages)
			{
				INumericalStorage nStorage = (INumericalStorage)storage;
				dTemp = nStorage.getMin();
				if(dTemp < dMin)
					dMin = dTemp;
				dTemp = nStorage.getMax();
				if(dTemp > dMax)
					dMax = dTemp;
			}
			
			for(IStorage storage : alStorages)
			{
				INumericalStorage nStorage = (INumericalStorage)storage;
				nStorage.normalizeWithExternalExtrema(dMin, dMax);
			}
		}
		else if (alStorages.get(0) instanceof INominalStorage)
		{
			// TODO what makes sense here? probably OperationNotSupportedException?
		}
	}
	
	public void setLabel(String sLabel)
	{
		this.sLabel = sLabel;
	}
	
	public String getLabel()
	{
		return sLabel;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<IStorage> iterator() 
	{
		return alStorages.iterator();
	}
}
