/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io;

/**
 * @author Samuel Gratzl
 *
 */
public enum EDimension {
	RECORD, ROW, DIMENSION, COLUMN;

	public boolean isRecord() {
		return this == RECORD || this == ROW;
	}

	public boolean isDimension() {
		return !this.isRecord();
	}
}
