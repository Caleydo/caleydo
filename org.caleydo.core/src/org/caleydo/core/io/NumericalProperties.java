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
