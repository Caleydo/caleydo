/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.column;

import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.IContainer;
import org.caleydo.core.data.collection.column.container.INumericalContainer;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;

/**
 * INumericalDimension is a specialization of IDimension. It is meant for numerical data of a continuous range,
 * equivalent to the set of real numbers. In terms of scales it can be interpreted as a data structure for an absolute
 * scale. As a consequence raw data for a numerical set can only be of a number format, such as int or float
 *
 * @author Alexander Lex
 */

public class NumericalColumn extends AColumn {

	/**
	 * Constructor
	 */
	public NumericalColumn() {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.COLUMN_NUMERICAL));
	}

	/**
	 * Constructor that takes a dimension ID. This is needed for de-serialization.
	 *
	 * @param dimensionID
	 */
	public NumericalColumn(int dimensionID) {
		super(dimensionID);
	}

	@Override
	public void normalize() {

		INumericalContainer iRawContainer = (INumericalContainer) dataRepToContainerMap.get(dataRep);
		dataRepToContainerMap.put(DataRepresentation.NORMALIZED, iRawContainer.normalize());
	}


	/**
	 * Same as {@link #normalizeWithExternalExtrema(double, double)}, but with an additional parameter letting you
	 * specify the source of the normalization
	 *
	 * @param sourceRep
	 * @param min
	 * @param max
	 */
	public void normalizeWithExternalExtrema(String sourceRep, String targetRep, double externalMin,
			double externalMax) {
		INumericalContainer rawDimension = (INumericalContainer) dataRepToContainerMap.get(sourceRep);

		INumericalContainer numericalContainer = rawDimension.normalizeWithExternalExtrema(externalMin, externalMax);

		dataRepToContainerMap.put(targetRep, numericalContainer);
	}

	/**
	 * <p>
	 * If you want to consider extremas for normalization which do not occur in this dimension (e.g., because the global
	 * extremas for the DataTable are used), use this method instead of normalize().
	 * </p>
	 * Values that are bigger or smaller then the extrema specified are set to 0 (minimum) or 1 (maximum) in the
	 * normalized data. The raw data is untouched. Therefore elements with values 0 or one can have different raw values
	 * associated.
	 * <p>
	 * Normalize operates on the raw data, except if you previously called log, then the logarithmized data is used.
	 *
	 * @param min
	 *            the minimum
	 * @param max
	 *            the maximum
	 * @throws IlleagalAttributeStateException
	 *             if min >= max
	 */
	public void normalizeWithExternalExtrema(double min, double max) {
		normalizeWithExternalExtrema(dataRep, DataRepresentation.NORMALIZED, min, max);
	}

	@Override
	public RawDataType getRawDataType() {
		return rawDataType;
	}

	/**
	 * Get the minimum of the raw data, respectively the logarithmized data if log was applied
	 *
	 * @return the minimum - a double since it can contain all values
	 */
	public double getMin() {
		if (!dataRepToContainerMap.containsKey(dataRep))
			throw new IllegalStateException("The requested data representation was not produced.");
		return ((INumericalContainer) dataRepToContainerMap.get(dataRep)).getMin();
	}

	/**
	 * Get the maximum of the raw data, respectively the logarithmized data if log was applied
	 *
	 * @return the maximum - a double since it can contain all values
	 */
	public double getMax() {
		return ((INumericalContainer) dataRepToContainerMap.get(dataRep)).getMax();
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
	 * Calculates the log10 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result Normalize then
	 * uses the log data instead of the raw data
	 */
	public void log10() {
		dataRepToContainerMap.put(DataRepresentation.LOG10,
				((INumericalContainer) dataRepToContainerMap.get(DataRepresentation.RAW)).log(10));
	}

	/**
	 * Calculates the log2 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result Normalize then
	 * uses the log data instead of the raw data
	 */
	public void log2() {
		dataRepToContainerMap.put(DataRepresentation.LOG2,
				((INumericalContainer) dataRepToContainerMap.get(DataRepresentation.RAW)).log(2));
	}

	/**
	 * Remove log and normalized data. Normalize has to be called again.
	 */
	public void reset() {
		dataRepToContainerMap.remove(DataRepresentation.LOG2);
		dataRepToContainerMap.remove(DataRepresentation.LOG10);
		dataRepToContainerMap.remove(DataRepresentation.NORMALIZED);
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
	 * Creates an empty container for the given {@link DataRepresentation} and stores it
	 *
	 * @param dataRepresentation
	 */
	public void setNewRepresentation(String dataRepresentation, float[] representation) {
		if (representation.length != size())
			throw new IllegalArgumentException("The size of the dimension (" + size()
					+ ") is not equal the size of the given new representation (" + representation.length + ")");
		if (dataRepToContainerMap.containsKey(dataRepresentation))
			throw new IllegalStateException("The data representation " + dataRepresentation + " already exists in "
					+ this);
		IContainer container = new FloatContainer(representation);
		dataRepToContainerMap.put(dataRepresentation, container);
	}

}
