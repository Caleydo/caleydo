/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column;

import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.INumericalContainer;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.util.math.MathHelper;

/**
 * INumericalDimension is a specialization of IDimension. It is meant for numerical data of a continuous range,
 * equivalent to the set of real numbers. In terms of scales it can be interpreted as a data structure for an absolute
 * scale. As a consequence raw data for a numerical set can only be of a number format, such as int or float
 *
 * @author Alexander Lex
 */

public class NumericalColumn<RawContainerType extends INumericalContainer<DataType>, DataType extends Number> extends
		AColumn<RawContainerType, DataType> {

	private double externalMin = Double.NaN;
	private double externalMax = Double.NaN;

	/**
	 * Constructor
	 */
	public NumericalColumn(DataDescription dataDescription) {
		super(dataDescription);
	}

	/**
	 * Same as {@link #normalizeWithExternalExtrema(double, double)}, but with an additional parameter letting you
	 * specify the source of the normalization
	 *
	 * @param sourceRep
	 * @param min
	 * @param max
	 */

	// private void normalize() {
	//
	// dataRepToContainerMap.put(EDataTransformation.NONE,
	// rawContainer.normalizeWithAtrificalExtrema(externalMin, externalMax));
	//
	// }

	/**
	 * @param externalMin
	 *            setter, see {@link externalMin}
	 */
	public void setExternalMin(double externalMin) {
		this.externalMin = externalMin;
	}

	/**
	 * @param externalMax
	 *            setter, see {@link externalMax}
	 */
	public void setExternalMax(double externalMax) {
		this.externalMax = externalMax;
	}

	/**
	 * <p>
	 * If you want to consider extremas for normalization which do not occur in this dimension (e.g., because the global
	 * extremas for the Table are used), use this method instead of normalize().
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
	@Override
	public void normalize() {
		dataRepToContainerMap.put(Table.Transformation.NONE,
				rawContainer.normalizeWithAtrificalExtrema(getMin(), getMax()));
		log2();
		log10();
	}

	/**
	 * Get the minimum of the raw data
	 *
	 * @return the minimum - a double since it can contain all values
	 */
	public double getMin() {
		if (Double.isNaN(externalMin))
			return rawContainer.getMin();
		else
			return externalMin;
	}

	/**
	 * Get the maximum of the raw data
	 *
	 * @return the maximum - a double since it can contain all values
	 */
	public double getMax() {
		if (Double.isNaN(externalMax))
			return rawContainer.getMax();
		else
			return externalMax;
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 *
	 * @param normalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	// public DataType getRawForNormalized(float normalized) {
	// return normalized * ((Number) getMax() - (Number) getMin());
	// }

	/**
	 * Calculates the log10 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result Normalize then
	 * uses the log data instead of the raw data
	 */
	private void log10() {
		FloatContainer logContainer = rawContainer.log(10);
		dataRepToContainerMap.put(NumericalTable.Transformation.LOG10,
				logContainer.normalizeWithAtrificalExtrema(MathHelper.log(getMin(), 10), MathHelper.log(getMax(), 10)));
	}

	/**
	 * Calculates the log2 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result Normalize then
	 * uses the log data instead of the raw data
	 */
	private void log2() {
		FloatContainer logContainer = rawContainer.log(2);
		dataRepToContainerMap.put(NumericalTable.Transformation.LOG2,
				logContainer.normalizeWithAtrificalExtrema(MathHelper.log(getMin(), 2), MathHelper.log(getMax(), 2)));
	}

	/**
	 * Creates an empty container for the given {@link DataRepresentation} and stores it
	 *
	 * @param dataRepresentation
	 */
	// public void setNewRepresentation(String dataRepresentation, float[] representation) {
	// if (representation.length != size())
	// throw new IllegalArgumentException("The size of the dimension (" + size()
	// + ") is not equal the size of the given new representation (" + representation.length + ")");
	// if (dataRepToContainerMap.containsKey(dataRepresentation))
	// throw new IllegalStateException("The data representation " + dataRepresentation + " already exists in "
	// + this);
	// FloatContainer container = new FloatContainer(representation);
	// dataRepToContainerMap.put(dataRepresentation, container);
	// }

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 *
	 * @param dataRep
	 */
	// public void setDataTransformation(EDataTransformation dataTransformation) {
	// this.dataTransformation = dataTransformation;
	// }

}
