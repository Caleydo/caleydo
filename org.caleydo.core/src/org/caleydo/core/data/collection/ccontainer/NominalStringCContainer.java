package org.caleydo.core.data.collection.ccontainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.collection.ICContainer;
import org.caleydo.core.data.collection.INominalCContainer;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * 
 * @author Alexander Lex
 * 
 * Container for nominal string values. Provides access to the values, 
 * can create discrete values for the nominal values 
 * 
 * You can provide a list of all possible values, otherwise such a list will be
 * constructed when you call normalize, or request a mapping for the first time.
 *
 */
public class NominalStringCContainer 
implements INominalCContainer 
{

	private ArrayList<String> sAlContainer;
	private HashMap<String, Float> hashNominalToDiscrete = new HashMap<String,	Float>();
	private HashMap<Float, String> hashDiscreteToNominal = new HashMap<Float, String>();
	private boolean bHashMapsInitialized = false;
	
	/**
	 * Constructor
	 * @param sAlContainer The complete list of all Strings in the dataset
	 */
	public NominalStringCContainer(ArrayList<String> sAlContainer) 
	{
		this.sAlContainer = sAlContainer;
	//	this.sAlContainer = (ArrayList<String>)Collections.unmodifiableList(this.sAlContainer);
		hashNominalToDiscrete = new HashMap<String,	Float>();
		hashDiscreteToNominal = new HashMap<Float, String>();
	}
	
	/**
	 * Get String at index
	 * @param iIndex the index
	 * @return the String
	 */
	public String get(int iIndex)
	{
		return sAlContainer.get(iIndex);
	}
	
	
	/**
	 * Provide a list with all possible values on the nominal scale. Useful
	 * when the data set does not contain all values by itself.
	 * 
	 * Take care that every value in the data set is also in this list, otherwise
	 * an exception will occur
	 *  
	 * @param sAlPossibleValues the List
	 */
	public void setPossibleValues(ArrayList<String> sAlPossibleValues)
	{
		//TODO: check if all values in the raw list are also in the other list
		setUpMapping(sAlPossibleValues);
	}
	
	/**
	 * Creates a float array of discrete data values for every nominal string value. 
	 * The same string always has the same value.   
	 * 
	 * If no list of possible values has been specified beforehand, a list is created.
	 */
	public ICContainer normalize() 
	{
		if(!bHashMapsInitialized)
			setUpMapping(sAlContainer);
		
		float[] fArNormalized = new float[sAlContainer.size()];
		
		int iCount = 0;
		for(String sContent : sAlContainer)
		{
			Float fTemp = hashNominalToDiscrete.get(sContent);
			if (fTemp == null)
				throw new CaleydoRuntimeException(
						"Requested string is not in the possible list of strings. " +
						"This happens if you have set the possible list with setPossibleValues " +
						"but a Value in the data set is not in this list.",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			fArNormalized[iCount] = fTemp.floatValue();
			iCount++;
		}
		
		return new PrimitiveFloatCContainer(fArNormalized);		
	}


	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#size()
	 */
	public int size() 
	{	
		return sAlContainer.size();
	}
	
	/**
	 * Returns an iterator on the data
	 * 
	 * Do not use the iterators remove, add or set function, since it will cause an
	 * UnsupportedOperationException.
	 * @return the Iterator
	 */
	public Iterator<String> iterator()
	{
		return sAlContainer.iterator();
	}
	
	/**
	 * When providing a float value following the rules of the normalization (0 >= x <= 1)
	 * the associated raw nominal value is returned
	 * 
	 * @param fDiscrete
	 * @return the string associated with the discrete value, or null if no such value exists
	 */
	public String getNominalForDiscreteValue(Float fDiscrete)
	{
		if(!bHashMapsInitialized)
			setUpMapping(sAlContainer);
		return hashDiscreteToNominal.get(fDiscrete);
	}
	
	/**
	 * When providing a nominal value that is in the initially provided list, the assoziated 
	 * normalized value is returned
	 * @param sNominal
	 * @return
	 */
	public Float getDiscreteForNominalValue(String sNominal)
	{
		if(!bHashMapsInitialized)
			setUpMapping(sAlContainer);
		return hashNominalToDiscrete.get(sNominal);
	}
	
	/**
	 * Initialize the mapping of nominal to discrete values.
	 * Call it either with the member sAlStorage, or with a list
	 * provided externally
	 * 
	 * @param sAlStorage
	 */
	private void setUpMapping(ArrayList<String> sAlStorage)
	{		
		for(String sContent : sAlStorage)
		{
			hashNominalToDiscrete.put(sContent, new Float(0));
		}
		
		float fDivisor = 1.0f/(hashNominalToDiscrete.size() - 1);
		
		//float[] fArNormalized = new float[sAlStorage.size()];
		
		int iCount = 0;
		for(String sContent : hashNominalToDiscrete.keySet())
		{
			Float fDiscrete = hashNominalToDiscrete.get(sContent);
			fDiscrete = fDivisor * iCount;
			hashNominalToDiscrete.put(sContent, fDiscrete);
			hashDiscreteToNominal.put(fDiscrete, sContent);
			
			iCount++;
		}
		bHashMapsInitialized = true;		
	}

}
