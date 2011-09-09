package org.caleydo.core.data.collection.dimension;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.ccontainer.NominalCContainer;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;

/**
 * The NominalDimension is an extension of the ADimension interface. It is meant for data which has no discrete
 * numerical values, such as nominal or ordinal data. Example cases are illness classifications, ratings such
 * as good, OK, bad etc. Normalization converts the entities into evenly spaced numerical values between 0 and
 * 1. One can provide a list of possible values, which is useful, if a list does not contain all possible
 * values, but you want to have the others represented anyway. If no such list is provided it is generated
 * from the available values.
 * 
 * @author Alexander Lex
 */

public class NominalDimension<T>
	extends ADimension {

	/**
	 * Constructor
	 */
	public NominalDimension() {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.DIMENSION_NOMINAL));
	}
	
	/**
	 * Constructor that takes a dimension ID. This is needed for de-serialization.
	 * 
	 * @param dimensionID
	 */
	public NominalDimension(int dimensionID) {
		super(dimensionID);
	}	

	/**
	 * Set the raw data Currently supported: String
	 * 
	 * @param alData
	 *            the ArrayList containing the data
	 */
	public void setRawNominalData(ArrayList<T> alData) {
		if (isRawDataSet)
			throw new IllegalStateException("Raw data was already set, tried to set again.");

		isRawDataSet = true;

		if (alData.isEmpty())
			throw new IllegalStateException("Raw Data is empty");
		else {
			if (alData.get(0) instanceof String) {
				rawDataType = RawDataType.STRING;
			}
			else {
				rawDataType = RawDataType.OBJECT;
			}

			NominalCContainer<T> sDimension = new NominalCContainer<T>(alData);
			hashCContainers.put(DataRepresentation.RAW, sDimension);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Provide a list of possible values, which must include all values specified in the raw data
	 * 
	 * @param sAlPossibleValues
	 */
	public void setPossibleValues(ArrayList<T> alPossibleValues) {
		if (alPossibleValues.isEmpty())
			throw new IllegalStateException("Raw Data is empty");
		else {
			if (hashCContainers.get(DataRepresentation.RAW) instanceof NominalCContainer)
				throw new IllegalStateException("Raw data format does not correspond to"
					+ "specified value list.");
			else {
				((NominalCContainer<T>) hashCContainers.get(DataRepresentation.RAW))
					.setPossibleValues(alPossibleValues);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T getRaw(int index) {
		return ((NominalCContainer<T>) hashCContainers.get(DataRepresentation.RAW)).get(index);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Create a histogram off all elements that actually occur in the dimension The values in the histogram are
	 * normalized between 0 and 1, where 0 means one occurrence and 1 corresponds to the maximum number of
	 * occurrences
	 * 
	 * @return a hash map mapping the nominal value to it's histogram value
	 */
	public HashMap<T, Float> getHistogram() {
		return ((NominalCContainer<T>) hashCContainers.get(DataRepresentation.RAW)).getHistogram();
	}

	@Override
	public void setExternalDataRepresentation(ExternalDataRepresentation externalDataRep) {

		if (externalDataRep != ExternalDataRepresentation.NORMAL)
			throw new IllegalArgumentException("Nominal dimensions support only raw representations");

		dataRep = DataRepresentation.RAW;

	}

	@Override
	public void normalize() {

		hashCContainers.put(DataRepresentation.NORMALIZED, hashCContainers.get(DataRepresentation.RAW)
			.normalize());
	}

}
