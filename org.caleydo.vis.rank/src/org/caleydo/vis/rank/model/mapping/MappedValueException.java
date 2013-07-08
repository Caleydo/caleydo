/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mapping;


/**
 * @author Samuel Gratzl
 *
 */
public class MappedValueException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1770569330402730830L;
	private final float value;

	public MappedValueException(float value) {
		this.value = value;
	}

	/**
	 * @return the value, see {@link #value}
	 */
	public float getValue() {
		return value;
	}
}
