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
	}

	public void setRaw(int index, DataType rawValue) {
		rawContainer.set(index, rawValue);
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
	 * Calculates the log10 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result Normalize then
	 * uses the log data instead of the raw data
	 */
	public void log10() {
		FloatContainer logContainer = rawContainer.log(10);
		dataRepToContainerMap.put(NumericalTable.Transformation.LOG10,
				logContainer.normalizeWithAtrificalExtrema(MathHelper.log(getMin(), 10), MathHelper.log(getMax(), 10)));
	}

	/**
	 * Calculates the log2 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result Normalize then
	 * uses the log data instead of the raw data
	 */
	public void log2() {
		FloatContainer logContainer = rawContainer.log(2);
		dataRepToContainerMap.put(NumericalTable.Transformation.LOG2,
				logContainer.normalizeWithAtrificalExtrema(MathHelper.log(getMin(), 2), MathHelper.log(getMax(), 2)));
	}

}
