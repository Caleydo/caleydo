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
package org.caleydo.view.tourguide.v2.r.model;


/**
 * @author Samuel Gratzl
 *
 */
public class Selection {
	private float min = 0;
	private float max = 1;
	private boolean normalizedSelection;

	/**
	 * @return the min, see {@link #min}
	 */
	public float getMin() {
		return min;
	}

	public boolean hasDefinedMin() {
		return min != Float.NEGATIVE_INFINITY;
	}

	/**
	 * @param min
	 *            setter, see {@link min}
	 */
	public void setMin(float min) {
		this.min = min;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public float getMax() {
		return max;
	}

	/**
	 * @param max
	 *            setter, see {@link max}
	 */
	public void setMax(float max) {
		this.max = max;
	}

	public boolean hasDefinedMax() {
		return max != Float.POSITIVE_INFINITY;
	}

	/**
	 * @return the normalizedSelection, see {@link #normalizedSelection}
	 */
	public boolean isNormalizedSelection() {
		return normalizedSelection;
	}

	/**
	 * @param normalizedSelection
	 *            setter, see {@link normalizedSelection}
	 */
	public void setNormalizedSelection(boolean normalizedSelection) {
		this.normalizedSelection = normalizedSelection;
	}

	public boolean isSelected(float raw, float normalized) {
		if (Float.isNaN(raw) || Float.isNaN(normalized))
			return false;

		float value = normalizedSelection ? normalized : raw;
		return value >= min && value <= max;
	}

}
