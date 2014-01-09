/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.geom;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.geom.Rectangle2D;

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

	public Rect(Vec4f xywh) {
		this(xywh.x(), xywh.y(), xywh.z(), xywh.w());
	}

	public Rect(Rectangle2D r) {
		this((float) r.getX(), (float) r.getY(), (float) r.getWidth(), (float) r.getHeight());
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
	 * @return the x, see {@link #x}
	 */
	public float x2() {
		return x + width;
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
	 * @return the y, see {@link #y}
	 */
	public float y2() {
		return y + height;
	}

	/**
	 * @param max
	 */
	public Rect y2(float y2) {
		this.height = y2 - y;
		return this;
	}

	/**
	 * @param max
	 */
	public Rect x2(float x2) {
		this.width = x2 - x;
		return this;
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

	public Vec2f xy2() {
		return new Vec2f(x, y2());
	}

	public Vec2f x2y2() {
		return new Vec2f(x2(), y2());
	}

	public Vec2f x2y() {
		return new Vec2f(x2(), y);
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

	public Rectangle2D asRectangle2D() {
		return new Rectangle2D.Float(x, y, width, height);
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
