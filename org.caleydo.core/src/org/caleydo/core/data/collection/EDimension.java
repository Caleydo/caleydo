/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.data.collection;

/**
 * @author Samuel Gratzl
 *
 */
public enum EDimension {
	RECORD, DIMENSION;

	public boolean isRecord() {
		return this == RECORD;
	}

	public boolean isDimension() {
		return !this.isRecord();
	}

	public static EDimension get(boolean dimension) {
		return dimension ? DIMENSION : RECORD;
	}

	public boolean isHorizontal() {
		return isDimension();
	}

	public boolean isVertical() {
		return isRecord();
	}

	public boolean select(boolean dim, boolean rec) {
		return this == DIMENSION ? dim : rec;
	}

	public float select(float dim, float rec) {
		return this == DIMENSION ? dim : rec;
	}

	public double select(double dim, double rec) {
		return this == DIMENSION ? dim : rec;
	}

	public <T> T select(T dim, T rec) {
		return this == DIMENSION ? dim : rec;
	}

	public <T> T selectZL(T z, T l) {
		return this == DIMENSION ? z : l;
	}
}
