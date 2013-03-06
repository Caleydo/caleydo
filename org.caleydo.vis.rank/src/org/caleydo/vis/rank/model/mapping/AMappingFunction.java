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

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMappingFunction extends AFloatFunction implements IMappingFunction {
	private final float fromMin;
	private final float fromMax;

	private float actMin;
	private float actMax;

	public AMappingFunction(float fromMin, float fromMax) {
		this.fromMin = fromMin;
		this.fromMax = fromMax;
		this.actMin = 0;
		if (!Float.isNaN(fromMin)) {
			this.actMin = fromMin;
		}
		this.actMax = 1;
		if (!Float.isNaN(fromMax)) {
			this.actMax = fromMax;
		}
	}

	public AMappingFunction(AMappingFunction copy) {
		this.fromMin = copy.fromMin;
		this.fromMax = copy.fromMax;
		this.actMin = copy.actMin;
		this.actMax = copy.actMax;
	}

	@Override
	public abstract IMappingFunction clone();

	@Override
	public void setAct(float min, float max) {
		if (Float.isNaN(fromMin))
			actMin = min;
		if (Float.isNaN(fromMax))
			actMax = max;
	}

	protected final boolean isDefaultMin(float k) {
		boolean id = !isMinDefined();
		boolean ad = !isMaxDefined();
		if (id && ad && (Math.abs(k - actMin) < Math.abs(k - actMax)))
			return true;
		return id;
	}

	/**
	 * @return the actMin, see {@link #actMin}
	 */
	@Override
	public final float getActMin() {
		return actMin;
	}

	/**
	 * @return the actMax, see {@link #actMax}
	 */
	@Override
	public final float getActMax() {
		return actMax;
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
