/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import org.caleydo.core.util.function.ADoubleFunction;
import org.caleydo.core.util.function.DoubleStatistics;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMappingFunction extends ADoubleFunction implements IMappingFunction {
	private final double fromMin;
	private final double fromMax;

	protected DoubleStatistics actStats;

	public AMappingFunction(double fromMin, double fromMax) {
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
	public void setActStatistics(DoubleStatistics stats) {
		this.actStats = stats;
	}

	protected final boolean isDefaultMin(double k) {
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
	public double getActMin() {
		if (!Double.isNaN(fromMin))
			return fromMin;
		return actStats == null ? 0 : actStats.getMin();
	}

	/**
	 * @return
	 */
	@Override
	public double getActMax() {
		if (!Double.isNaN(fromMax))
			return fromMax;
		return actStats == null ? 1 : actStats.getMax();
	}

	/**
	 * @return the fromMin, see {@link #fromMin}
	 */
	public final double getFromMin() {
		return fromMin;
	}

	/**
	 * @return the fromMax, see {@link #fromMax}
	 */
	public final double getFromMax() {
		return fromMax;
	}

	@Override
	public final boolean hasDefinedMappingBounds() {
		return !Double.isNaN(fromMin) && !Double.isNaN(fromMax);
	}

	@Override
	public final boolean isMinDefined() {
		return !Double.isNaN(fromMin);
	}

	@Override
	public final boolean isMaxDefined() {
		return !Double.isNaN(fromMax);
	}
}
