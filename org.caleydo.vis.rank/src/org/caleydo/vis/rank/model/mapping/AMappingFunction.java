/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.model.mapping;

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
