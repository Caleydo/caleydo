/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import org.caleydo.core.util.function.AFloatFunction;
import org.caleydo.core.util.function.FloatStatistics;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMappingFunction extends AFloatFunction implements IMappingFunction {
	private final float fromMin;
	private final float fromMax;

	protected FloatStatistics actStats;

	public AMappingFunction(float fromMin, float fromMax) {
		this.fromMin = fromMin;
		this.fromMax = fromMax;
	}

	public AMappingFunction(AMappingFunction copy) {
		this.fromMin = copy.fromMin;
		this.fromMax = copy.fromMax;
		this.actStats = copy.actStats;
	}

	@Override
	public abstract IMappingFunction clone();

	@Override
	public void setActStatistics(FloatStatistics stats) {
		this.actStats = stats;
	}

	protected final boolean isDefaultMin(float k) {
		boolean id = !isMinDefined();
		boolean ad = !isMaxDefined();
		if (id && ad && (Math.abs(k - getActMin()) < Math.abs(k - getActMax())))
			return true;
		return id;
	}

	/**
	 * @return
	 */
	@Override
	public float getActMin() {
		if (!Float.isNaN(fromMin))
			return fromMin;
		return actStats == null ? 0 : actStats.getMin();
	}

	/**
	 * @return
	 */
	@Override
	public float getActMax() {
		if (!Float.isNaN(fromMax))
			return fromMax;
		return actStats == null ? 1 : actStats.getMax();
	}

	/**
	 * @return the fromMin, see {@link #fromMin}
	 */
	public final float getFromMin() {
		return fromMin;
	}

	/**
	 * @return the fromMax, see {@link #fromMax}
	 */
	public final float getFromMax() {
		return fromMax;
	}

	@Override
	public final boolean hasDefinedMappingBounds() {
		return !Float.isNaN(fromMin) && !Float.isNaN(fromMax);
	}

	@Override
	public final boolean isMinDefined() {
		return !Float.isNaN(fromMin);
	}

	@Override
	public final boolean isMaxDefined() {
		return !Float.isNaN(fromMax);
	}
}
