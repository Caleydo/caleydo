/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.geom;

import gleem.linalg.Vec2f;

import java.awt.geom.Line2D;

/**
 * a custom implementation of a line to avoid awt
 *
 * @author Christian
 *
 */
public class Line implements Cloneable {
	private float x1, y1, x2, y2;

	public Line() {

	}

	public Line(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Line(Vec2f point1, Vec2f point2) {
		this(point1.x(), point1.y(), point2.x(), point2.y());
	}

	public float x1() {
		return x1;
	}

	public float x2() {
		return x2;
	}

	public float y1() {
		return y1;
	}

	public float y2() {
		return y2;
	}

	public Line x1(float x1) {
		this.x1 = x1;
		return this;
	}

	public Line x2(float x2) {
		this.x2 = x2;
		return this;
	}

	public Line y1(float y1) {
		this.y1 = y1;
		return this;
	}

	public Line y2(float y2) {
		this.y2 = y2;
		return this;
	}

	public Vec2f point1() {
		return new Vec2f(x1, y1);
	}

	public Vec2f point2() {
		return new Vec2f(x2, y2);
	}

	public Line point1(float x1, float y1) {
		this.x1 = x1;
		this.y1 = y1;
		return this;
	}

	public Line point2(float x2, float y2) {
		this.x2 = x2;
		this.y2 = y2;
		return this;
	}

	public Line point1(Vec2f point1) {
		this.x1 = point1.x();
		this.y1 = point1.y();
		return this;
	}

	public Line point2(Vec2f point2) {
		this.x2 = point2.x();
		this.y2 = point2.y();
		return this;
	}

	public Line2D asLine2D() {
		return new Line2D.Float(x1, y1, x2, y2);
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
		result = prime * result + Float.floatToIntBits(x1);
		result = prime * result + Float.floatToIntBits(x2);
		result = prime * result + Float.floatToIntBits(y1);
		result = prime * result + Float.floatToIntBits(y2);
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
		Line other = (Line) obj;
		if (Float.floatToIntBits(x1) != Float.floatToIntBits(other.x1))
			return false;
		if (Float.floatToIntBits(x2) != Float.floatToIntBits(other.x2))
			return false;
		if (Float.floatToIntBits(y1) != Float.floatToIntBits(other.y1))
			return false;
		if (Float.floatToIntBits(y2) != Float.floatToIntBits(other.y2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Line(").append(x1).append(',');
		builder.append(y1).append(',');
		builder.append(x2).append(',');
		builder.append(y2).append(')');
		return builder.toString();
	}
}
