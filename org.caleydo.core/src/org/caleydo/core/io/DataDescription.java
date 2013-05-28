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

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.table.Table;

/**
 * Container for all meta-data on the data. Can be set on a whole-dataset level or on a column-by-column level.
 *
 * @author Alexander
 *
 */
public class DataDescription {

	/**
	 * The dataClass of the column, must be one equivalent to those listed in {@link EDataClass}. Defaults to real
	 * numbers.
	 */
	private EDataClass dataClass = EDataClass.REAL_NUMBER;

	/**
	 * The data type of the {@link #dataClass}. If the dataClass has only one possible rawDataType (see
	 * {@link EDataClass}) this is automatically set. Defaults to float.
	 */
	private EDataType rawDataType = EDataType.FLOAT;

	/**
	 * The data transformation that should be used by default.
	 */
	private String defaultDataTransformation = Table.Transformation.NONE;

	/**
	 * Set this if your data is homogeneous (i.e. all the columns in the file are of the same semantic data type, i.e.
	 * they have the same value ranges, etc.) and numerical. If this is set, the data scale used is the same for all
	 * columns. This member can not be set at the same time as {@link #categoricalClassDescription}. Defaults to null.
	 */
	private NumericalProperties numericalProperties = null;

	/**
	 * Set this if your data is homogeneous (i.e. all the columns in the file are of the same semantic data type, i.e.
	 * they have the same value ranges, etc.) and categorical. If this is set, the data scale used is the same for all
	 * columns. This member can not be set at the same time as {@link #numericalProperties}. Defaults to null.
	 */
	private CategoricalClassDescription<?> categoricalClassDescription = null;

	/**
	 *
	 */
	public DataDescription() {

	}

	/**
	 * Constructor specifying the class of column and infers the data type from the class if possible. *
	 *
	 * @param dataClass
	 * @throws IllegalArgumentException
	 *             if inference is not obvious.
	 */
	public DataDescription(EDataClass dataClass) {
		if (dataClass.getSupportedDataType() == null)
			throw new IllegalArgumentException("Cannot infer data type for data class " + dataClass);
		this.dataClass = dataClass;
		this.rawDataType = dataClass.getSupportedDataType();
	}

	public DataDescription(EDataClass dataClass, EDataType dataType) {
		init(dataClass, dataType);
	}

	public DataDescription(EDataClass dataClass, EDataType dataType, NumericalProperties numericalProperties) {
		init(dataClass, dataType);
		this.numericalProperties = numericalProperties;

	}

	public DataDescription(EDataClass dataClass, EDataType dataType,
			CategoricalClassDescription<?> categoricalClassDescription) {
		this.categoricalClassDescription = categoricalClassDescription;
		init(dataClass, dataType);

	}

	private void init(EDataClass dataClass, EDataType dataType) {
		this.dataClass = dataClass;
		if (!dataClass.supports(dataType))
			throw new IllegalArgumentException("DataClass " + dataClass + " doesn't support rawDataType " + dataType);
		this.rawDataType = dataType;
	}

	/**
	 * @param dataClass
	 *            setter, see {@link dataClass}
	 */
	public void setDataClass(EDataClass dataClass) {
		this.dataClass = dataClass;
		if (dataClass.getSupportedDataType() != null)
			rawDataType = dataClass.getSupportedDataType();
	}

	/**
	 * @return the dataClass, see {@link #dataClass}
	 */
	public EDataClass getDataClass() {
		return dataClass;
	}

	/**
	 * @param rawDataType
	 *            setter, see {@link rawDataType}
	 */
	public void setRawDataType(EDataType rawDataType) {
		this.rawDataType = rawDataType;
	}

	/**
	 * @return the rawDataType, see {@link #rawDataType}
	 */
	public EDataType getRawDataType() {
		return rawDataType;
	}

	/**
	 * @param defaultDataTransformation
	 *            setter, see {@link defaultDataTransformation}
	 */
	public void setDefaultDataTransformation(String defaultDataTransformation) {
		this.defaultDataTransformation = defaultDataTransformation;
	}

	/**
	 * @return the defaultDataTransformation, see {@link #defaultDataTransformation}
	 */
	public String getDefaultDataTransformation() {
		return defaultDataTransformation;
	}

	/**
	 * @param numericalProperties
	 *            setter, see {@link numericalProperties}
	 */
	public void setNumericalProperties(NumericalProperties numericalProperties) {
		if (categoricalClassDescription != null)
			throw new IllegalStateException(
					"Cannot set both numerical and categorical data set description at the same time");
		this.numericalProperties = numericalProperties;
	}

	/**
	 * @return the numericalProperties, see {@link #numericalProperties}
	 */
	public NumericalProperties getNumericalProperties() {
		return numericalProperties;
	}

	/**
	 * @param categoricalClassDescription
	 *            setter, see {@link categoricalClassDescription}
	 */
	public void setCategoricalClassDescription(CategoricalClassDescription<?> categoricalClassDescription) {
		if (numericalProperties != null)
			throw new IllegalStateException(
					"Cannot set both numerical and categorical data set description at the same time");
		this.categoricalClassDescription = categoricalClassDescription;
	}

	/**
	 * @return the categoricalClassDescription, see {@link #categoricalClassDescription}
	 */
	public CategoricalClassDescription<?> getCategoricalClassDescription() {
		return categoricalClassDescription;
	}

	@Override
	public String toString() {
		String data = "";
		if (numericalProperties != null)
			data += "(numerical)";
		else if (categoricalClassDescription != null)
			data += "(categorical)";
		else
			data += "(hybrid/inhomogeneous)";

		return data;
	}

}
