/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import org.caleydo.core.data.collection.table.Table;

/**
 * <p>
 * Properties for a homogeneous numerical dataset. In an homogeneous numerical dataset all the columns in the file are
 * of the same semantic data type and have the same value ranges. The data set can be interpreted as a unit, i.e. the
 * overall distribution, etc. is meaningful.
 * </p>
 * <p>
 * This class provides various properties to modify the dataset, such as a maximum, minimum, a "data center", etc. You
 * have to specify a {@link NumericalProperties} in the {@link DataSetDescription} even if you don't want to
 * change these parameters to convey that the dataset is indeed numerical and homogeneous.
 * </p>
 * <p>
 * See also {@link CategoricalProperties}, this classes pendant for categorical data.
 * </p>
 *
 * @author Alexander Lex
 *
 */
public class NumericalProperties {
	/**
	 * <p>
	 * Value that, if set, determines a neutral center point of the data, A common example is that 0 is the neutral
	 * value, lower values are in the negative and larger values are in the positive range. If this value is set it is
	 * be assumed that the extend into both, positive and negative direction is the same. E.g., for a dataset [-0.5,
	 * 0.7] with a center set at 0, the value range will be set to -0.7 to 0.7.
	 * </p>
	 * <p>
	 * Defaults to null, i.e., no external data center is defined.
	 * </p>
	 */
	private Double dataCenter = null;

	/**
	 * An artificial min value used for normalization in the {@link Table}. Defaults to null.
	 */
	private Float min = null;

	/**
	 * An artificial max value used for normalization in the {@link Table}. Defaults to null.
	 */
	private Float max = null;

	/**
	 * Determines whether and if so which transformation should be applied to the data (e.g. log2 transformation). This
	 * is mapped to values of {@link EDataTransformation}.
	 */
	private String dataTransformation = Table.Transformation.NONE;


	/**
	 *
	 */
	public NumericalProperties() {
	}

	/**
	 * @param dataCenter
	 *            setter, see {@link #dataCenter}
	 */
	public void setDataCenter(Double dataCenter) {
		this.dataCenter = dataCenter;
	}

	/**
	 * @return the dataCenter, see {@link #dataCenter}
	 */
	public Double getDataCenter() {
		return dataCenter;
	}

	/**
	 * @param min
	 *            setter, see {@link #min}
	 */
	public void setMin(Float min) {
		this.min = min;
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public Float getMin() {
		return min;
	}

	/**
	 * @param max
	 *            setter, see {@link #max}
	 */
	public void setMax(Float max) {
		this.max = max;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public Float getMax() {
		return max;
	}

	/**
	 * @return the dataTransformation, see {@link #dataTransformation}
	 */
	public String getDataTransformation() {
		return dataTransformation;
	}

	/**
	 * @param dataTransformation
	 *            setter, see {@link #dataTransformation}
	 */
	public void setDataTransformation(String mathFilterMode) {
		this.dataTransformation = mathFilterMode;
	}

}
