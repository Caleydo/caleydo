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
package org.caleydo.core.data.collection;

import java.util.Arrays;
import java.util.List;

/**
 * The classes of data that can be held in columns. The currently supported {@link EDataType}s for a class are passed as
 * constructor arguments.
 *
 * @author Alexander Lex
 */
public enum EDataClass {

	/** A natural number */
	NATURAL_NUMBER(EDataType.INTEGER),
	/** Single-precision numerical real value */
	REAL_NUMBER(EDataType.FLOAT),
	/** Ordered categorical */
	ORDINAL(EDataType.INTEGER, EDataType.STRING),
	/** Unordered categorical */
	NOMINAL(EDataType.INTEGER, EDataType.STRING),
	/**
	 * A unique object, such as a string, an image. No trivial relationship between objects of this class exist. E.g.,
	 * there is no order, no numerical size, etc.
	 */
	UNIQUE_OBJECT(EDataType.STRING);

	/** List of supported data types */
	List<EDataType> supportedDataTypes;

	/**
	 * Constructor setting the supported data types
	 */
	private EDataClass(EDataType... supportedDataTypes) {
		this.supportedDataTypes = Arrays.asList(supportedDataTypes);
	}

	/**
	 * Checks if this data class supports the passed dataType
	 *
	 * @param dataType
	 *            the data type to be checked
	 * @return true if this class supports this data type, else false
	 */
	public boolean supports(EDataType dataType) {
		return supportedDataTypes.contains(dataType);
	}

	/**
	 * Returns the data type for the data class if there is a direct one to one mapping (the enum has only one parameter
	 * in the constructor). If multiple data types are mapped returns null
	 *
	 * @return the data type if there is a 1:1 relationship, else null
	 */
	public EDataType getSupportedDataType() {
		if (supportedDataTypes.size() == 1)
			return supportedDataTypes.get(0);
		return null;
	}

	/**
	 * @return the supportedDataTypes, see {@link #supportedDataTypes}
	 */
	public List<EDataType> getSupportedDataTypes() {
		return supportedDataTypes;
	}
}
