/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.dimension;

import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.container.FloatContainer;
import org.caleydo.core.data.collection.container.IContainer;
import org.caleydo.core.data.collection.container.INumericalContainer;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;

/**
 * INumericalDimension is a specialization of IDimension. It is meant for
 * numerical data of a continuous range, equivalent to the set of real numbers.
 * In terms of scales it can be interpreted as a data structure for an absolute
 * scale. As a consequence raw data for a numerical set can only be of a number
 * format, such as int or float
 * 
 * @author Alexander Lex
 */

public class NumericalColumn extends AColumn {

	/**
	 * Constructor
	 */
	public NumericalColumn() {
		super(GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.COLUMN_NUMERICAL));
	}

	/**
	 * Constructor that takes a dimension ID. This is needed for
	 * de-serialization.
	 * 
	 * @param dimensionID
	 */
	public NumericalColumn(int dimensionID) {
		super(dimensionID);
	}

	@Override
	public void normalize() {

		INumericalContainer iRawContainer = (INumericalContainer) hashCContainers
				.get(dataRep);
		hashCContainers.put(DataRepresentation.NORMALIZED, iRawContainer.normalize());
	}

	public void normalizeUncertainty(float invalidThreshold, float validThreshold) {

		FloatContainer certainties = (FloatContainer) hashCContainers
				.get(DataRepresentation.UNCERTAINTY_RAW);
		FloatContainer normalizedCertainties = certainties.normalizeWithExternalExtrema(
				invalidThreshold, validThreshold);
		hashCContainers.put(DataRepresentation.UNCERTAINTY_NORMALIZED,
				normalizedCertainties);
	}

	/**
	 * Same as {@link #normalizeWithExternalExtrema(double, double)}, but with
	 * an additional parameter letting you specify the source of the
	 * normalization
	 * 
	 * @param sourceRep
	 * @param dMin
	 * @param dMax
	 */
	public void normalizeWithExternalExtrema(DataRepresentation sourceRep,
			DataRepresentation targetRep, double dMin, double dMax) {
		INumericalContainer rawDimension = (INumericalContainer) hashCContainers
				.get(sourceRep);

		INumericalContainer numericalContainer = rawDimension
				.normalizeWithExternalExtrema(dMin, dMax);

		hashCContainers.put(targetRep, numericalContainer);
	}

	/**
	 * <p>
	 * If you want to consider extremas for normalization which do not occur in
	 * this dimension (e.g., because the global extremas for the DataTable are
	 * used), use this method instead of normalize().
	 * </p>
	 * Values that are bigger or smaller then the extrema specified are set to 0
	 * (minimum) or 1 (maximum) in the normalized data. The raw data is
	 * untouched. Therefore elements with values 0 or one can have different raw
	 * values associated.
	 * <p>
	 * Normalize operates on the raw data, except if you previously called log,
	 * then the logarithmized data is used.
	 * 
	 * @param dMin
	 *            the minimum
	 * @param dMax
	 *            the maximum
	 * @throws IlleagalAttributeStateException
	 *             if dMin >= dMax
	 */
	public void normalizeWithExternalExtrema(double dMin, double dMax) {
		normalizeWithExternalExtrema(dataRep, DataRepresentation.NORMALIZED, dMin, dMax);
	}

	@Override
	public RawDataType getRawDataType() {
		return rawDataType;
	}

	/**
	 * Get the minimum of the raw data, respectively the logarithmized data if
	 * log was applied
	 * 
	 * @return the minimum - a double since it can contain all values
	 */
	public double getMin() {
		if (!hashCContainers.containsKey(dataRep))
			throw new IllegalStateException(
					"The requested data representation was not produced.");
		return ((INumericalContainer) hashCContainers.get(dataRep)).getMin();
	}

	/**
	 * Get the maximum of the raw data, respectively the logarithmized data if
	 * log was applied
	 * 
	 * @return the maximum - a double since it can contain all values
	 */
	public double getMax() {
		return ((INumericalContainer) hashCContainers.get(dataRep)).getMax();
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 * 
	 * @param dNormalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized) {
		return dNormalized * (getMax() - getMin());
	}

	/**
	 * Calculates the log10 of the raw data. Log data can be retrieved by using
	 * the get methods with EDataRepresentation.LOG10. Call normalize after this
	 * operation if you want to display the result Normalize then uses the log
	 * data instead of the raw data
	 */
	public void log10() {
		hashCContainers.put(DataRepresentation.LOG10,
				((INumericalContainer) hashCContainers.get(DataRepresentation.RAW))
						.log(10));
	}

	/**
	 * Calculates the log2 of the raw data. Log data can be retrieved by using
	 * the get methods with EDataRepresentation.LOG10. Call normalize after this
	 * operation if you want to display the result Normalize then uses the log
	 * data instead of the raw data
	 */
	public void log2() {
		hashCContainers.put(DataRepresentation.LOG2,
				((INumericalContainer) hashCContainers.get(DataRepresentation.RAW))
						.log(2));
	}

	/**
	 * Remove log and normalized data. Normalize has to be called again.
	 */
	public void reset() {
		hashCContainers.remove(DataRepresentation.LOG2);
		hashCContainers.remove(DataRepresentation.LOG10);
		hashCContainers.remove(DataRepresentation.NORMALIZED);
	}

	@Override
	public void setExternalDataRepresentation(EDataTransformation externalDataRep) {
		switch (externalDataRep) {
		case NONE:
			dataRep = DataRepresentation.RAW;
			break;
		case LOG10:
			dataRep = DataRepresentation.LOG10;
			break;
		case LOG2:
			dataRep = DataRepresentation.LOG2;
			break;
		}

	}

	/**
	 * Returns a histogram of the values in the dimension for all values (not
	 * considering VAs). The number of the bins is sqrt(numberOfElements)
	 * 
	 * @return
	 */
	public Histogram getHistogram() {

		int iNumberOfBuckets = (int) Math.sqrt(size());
		Histogram histogram = new Histogram(iNumberOfBuckets);

		FloatContainer container = (FloatContainer) hashCContainers
				.get(DataRepresentation.NORMALIZED);
		for (int count = 0; count < container.size(); count++) {
			// this works because the values in the container are already
			// noramlized
			int iIndex = (int) (container.get(count) * iNumberOfBuckets);
			if (iIndex == iNumberOfBuckets)
				iIndex--;

			histogram.add(iIndex, count);
		}

		return histogram;
	}

	/**
	 * Returns a histogram of the values in the dimension for all values
	 * considering the specified VA. The number of the bins is sqrt(VA size)
	 * 
	 * @param recordVA
	 *            VA to consider for the histogram
	 * @return
	 */
	@Deprecated
	public Histogram getHistogram(RecordVirtualArray recordVA) {

		int iNumberOfBuckets = (int) Math.sqrt(recordVA.size());
		Histogram histogram = new Histogram(iNumberOfBuckets);

		// FloatCContainerIterator iterator =
		// ((FloatCContainer)
		// hashCContainers.get(DataRepresentation.NORMALIZED)).iterator(recordVA);
		// while (iterator.hasNext()) {
		FloatContainer container = (FloatContainer) hashCContainers
				.get(DataRepresentation.NORMALIZED);

		for (Integer recordID : recordVA) {

			// this works because the values in the container are already
			// normalized
			int iIndex = (int) (container.get(recordID) * iNumberOfBuckets);
			if (iIndex == iNumberOfBuckets)
				iIndex--;
			histogram.add(iIndex, recordID);
		}

		return histogram;
	}

	/**
	 * Creates an empty container for the given {@link DataRepresentation} and
	 * stores it
	 * 
	 * @param dataRepresentation
	 */
	public void setNewRepresentation(DataRepresentation dataRepresentation,
			float[] representation) {
		if (representation.length != size())
			throw new IllegalArgumentException("The size of the dimension (" + size()
					+ ") is not equal the size of the given new representation ("
					+ representation.length + ")");
		if (hashCContainers.containsKey(dataRepresentation))
			throw new IllegalStateException("The data representation "
					+ dataRepresentation + " already exists in " + this);
		IContainer container = new FloatContainer(representation);
		hashCContainers.put(dataRepresentation, container);
	}

}
