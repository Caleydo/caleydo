/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	/** Categorical */
	CATEGORICAL(EDataType.INTEGER, EDataType.STRING),
	/**
	 * A unique object, such as a string, an image. No trivial relationship between objects of this class exist. E.g.,
	 * there is no order, no numerical size, etc.
	 */
	UNIQUE_OBJECT(EDataType.STRING);

	/** List of supported data types */
	private final List<EDataType> supportedDataTypes;

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
