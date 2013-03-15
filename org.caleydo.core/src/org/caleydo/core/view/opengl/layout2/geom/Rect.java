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
package org.caleydo.core.view.opengl.layout2.geom;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

/**
 * a custom implementation of a rect to avoid awt
 *
 * @author Samuel Gratzl
 *
 */
public final class Rect implements Cloneable {
	private float x, y, width, height;

	public Rect() {

	}

	public Rect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * @return the x, see {@link #x}
	 */
	public float x() {
		return x;
	}

	/**
	 * @param x
	 *            setter, see {@link x}
	 */
	public Rect x(float x) {
		this.x = x;
		return this;
	}

	/**
	 * @return the y, see {@link #y}
	 */
	public float y() {
		return y;
	}

	/**
	 * @param y
	 *            setter, see {@link y}
	 */
	public Rect y(float y) {
		this.y = y;
		return this;
	}

	/**
	 * @return the width, see {@link #width}
	 */
	public float width() {
		return width;
	}

	/**
	 * @param width
	 *            setter, see {@link width}
	 */
	public Rect width(float width) {
		this.width = width;
		return this;
	}

	/**
	 * @return the height, see {@link #height}
	 */
	public float height() {
		return height;
	}

	/**
	 * @param height
	 *            setter, see {@link height}
	 */
	public Rect height(float height) {
		this.height = height;
		return this;
	}

	public Vec2f xy() {
		return new Vec2f(x, y);
	}

	public Rect xy(Vec2f xy) {
		return xy(xy.x(), xy.y());
	}

	public Rect xy(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vec2f size() {
		return new Vec2f(width, height);
	}

	public Rect size(Vec2f wh) {
		return size(wh.x(), wh.y());
	}

	private Rect size(float w, float h) {
		this.width = w;
		this.height = h;
		return this;
	}

	public Vec4f bounds() {
		return new Vec4f(x, y, width, height);
	}

	public Rect bounds(Vec4f bounds) {
		return bounds(bounds.x(), bounds.y(), bounds.z(), bounds.w());
	}

	public Rect bounds(Rect bounds) {
		return bounds(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Rect bounds(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		return this;

	}

	@Override
	public Rect clone() {
		try {
			return (Rect) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(height);
		result = prime * result + Float.floatToIntBits(width);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rect other = (Rect) obj;
		if (Float.floatToIntBits(height) != Float.floatToIntBits(other.height))
			return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rect(").append(x).append(',');
		builder.append(y).append(',');
		builder.append(width).append(',');
		builder.append(height).append(')');
		return builder.toString();
	}
}
