package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.AManagedObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implementation of the ISet interface
 * 
 * @author Alexander Lex
 *
 */
public class Set
extends AManagedObject
implements ISet
{

	private ESetType setType;
	
	private ArrayList<IStorage> alStorages;
	
	private String sLabel;
	
	private double dMin = Double.MAX_VALUE;
	private double dMax = Double.MIN_VALUE;
	
	
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
	public EManagerObjectType getBaseType() 
	{
		return EManagerObjectType.SET;
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
		for(IStorage storage : alStorages)
		{
			if(storage instanceof INumericalStorage)
			{
				INumericalStorage nStorage = (INumericalStorage)storage;
				try
				{
					nStorage.normalizeWithExternalExtrema(getMin(), getMax());
				}
				catch(OperationNotSupportedException e)
				{
					throw new CaleydoRuntimeException("Tried to normalize globally on a set wich has" +
							"different storage types",
							CaleydoRuntimeExceptionType.DATAHANDLING);
				}
			}
			else
			{
				throw new CaleydoRuntimeException("Tried to normalize globally on a set wich has" +
						"different storage types",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}
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
	
	public double getMin()
		throws OperationNotSupportedException
	{
		if (dMin == Double.MAX_VALUE)
			calculateGlobalExtrema();
		return dMin;
	}
	
	public double getMax()
		throws OperationNotSupportedException
	{
		if (dMax == Double.MIN_VALUE)
			calculateGlobalExtrema();
		return dMax;
	}
	
	private void calculateGlobalExtrema() 
		throws OperationNotSupportedException
	{	
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
		}
		else if (alStorages.get(0) instanceof INominalStorage)
		{
			throw new OperationNotSupportedException("No minimum or maximum can be calculated " +
					"on nominal data");
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#log10()
	 */
	public void log10()
	{
		for(IStorage storage : alStorages)
		{
			if(storage instanceof INumericalStorage)
			{
				INumericalStorage nStorage = (INumericalStorage)storage;				
				nStorage.log10();				
			}
			else
			{
				throw new CaleydoRuntimeException("Tried to normalize globally on a set wich has" +
						"different storage types",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}	
	}
	
}
