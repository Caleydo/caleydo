package org.caleydo.core.data.collection.ccontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Container for nominal string values. Provides access to the values, can
 * create discrete values for the nominal values You can provide a list of all
 * possible values, otherwise such a list will be constructed when you call
 * normalize, or request a mapping for the first time.
 * 
 * @author Alexander Lex
 */
public class NominalCContainer<T>
	extends ATypedCContainer<T>
	implements INominalCContainer<T>
{


	private HashMap<T, Float> hashNominalToDiscrete = new HashMap<T, Float>();

	private HashMap<Float, T> hashDiscreteToNominal = new HashMap<Float, T>();

	private boolean bHashMapsInitialized = false;

	/**
	 * Constructor
	 * 
	 * @param sAlContainer The complete list of all Strings in the dataset
	 */
	public NominalCContainer(ArrayList<T> tAlContainer)
	{

		this.alContainer = tAlContainer;
		hashNominalToDiscrete = new HashMap<T, Float>();
		hashDiscreteToNominal = new HashMap<Float, T>();
	}
	

	/**
	 * Provide a list with all possible values on the nominal scale. Useful when
	 * the data set does not contain all values by itself. Take care that every
	 * value in the data set is also in this list, otherwise an exception will
	 * occur
	 * 
	 * @param sAlPossibleValues the List
	 */
	public void setPossibleValues(ArrayList<T> alPossibleValues)
	{
		// TODO: check if all values in the raw list are also in the other list
		setUpMapping(alPossibleValues);
	}

	/**
	 * Creates a float array of discrete data values for every nominal value.
	 * The same string always has the same value. If no list of possible values
	 * has been specified beforehand, a list is created.
	 */
	@Override
	public FloatCContainer normalize()
	{

		if (!bHashMapsInitialized)
			setUpMapping(alContainer);

		float[] fArNormalized = new float[alContainer.size()];

		int iCount = 0;
		for (T tContent : alContainer)
		{
			Float fTemp = hashNominalToDiscrete.get(tContent);
			if (fTemp == null)
				throw new CaleydoRuntimeException(
						"Requested string is not in the possible list of strings. "
								+ "This happens if you have set the possible list with setPossibleValues "
								+ "but a Value in the data set is not in this list.",
						CaleydoRuntimeExceptionType.DATAHANDLING);
			fArNormalized[iCount] = fTemp.floatValue();
			iCount++;
		}

		return new FloatCContainer(fArNormalized);
	}

	/**
	 * When providing a float value following the rules of the normalization (0
	 * >= x <= 1) the associated raw nominal value is returned
	 * 
	 * @param fDiscrete
	 * @return the string associated with the discrete value, or null if no such
	 *         value exists
	 */
	public T getNominalForDiscreteValue(Float fDiscrete)
	{
		if (!bHashMapsInitialized)
			setUpMapping(alContainer);
		return hashDiscreteToNominal.get(fDiscrete);
	}

	/**
	 * When providing a nominal value that is in the initially provided list,
	 * the assoziated normalized value is returned
	 * 
	 * @param sNominal
	 * @return
	 */
	public Float getDiscreteForNominalValue(T tNominal)
	{
		if (!bHashMapsInitialized)
			setUpMapping(alContainer);
		return hashNominalToDiscrete.get(tNominal);
	}

	/**
	 * Initialize the mapping of nominal to discrete values. Call it either with
	 * the member sAlStorage, or with a list provided externally
	 * 
	 * @param sAlStorage
	 */
	private void setUpMapping(ArrayList<T> tAlStorage)
	{
		for (T tContent : tAlStorage)
		{
			hashNominalToDiscrete.put(tContent, new Float(0));
		}

		float fDivisor = 1.0f / (hashNominalToDiscrete.size() - 1);

		// float[] fArNormalized = new float[sAlStorage.size()];

		int iCount = 0;
		for (T tContent : hashNominalToDiscrete.keySet())
		{
			Float fDiscrete = hashNominalToDiscrete.get(tContent);
			fDiscrete = fDivisor * iCount;
			hashNominalToDiscrete.put(tContent, fDiscrete);
			hashDiscreteToNominal.put(fDiscrete, tContent);

			iCount++;
		}
		bHashMapsInitialized = true;
	}

}
