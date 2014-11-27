/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.util.gleem.ColoredVec2f;
import org.caleydo.core.view.opengl.util.spline.TesselatedPolygons;

import com.google.common.collect.Iterators;

/**
 * @author Samuel Gratzl
 *
 */
public class PathBuilder implements Iterable<Vec2f> {
	private final List<Vec2f> l = new ArrayList<>(4);

	private PathBuilder() {

	}

	public static PathBuilder empty() {
		return new PathBuilder();
	}

	public static PathBuilder moveTo(float x, float y) {
		return empty().add(x, y);
	}

	public static PathBuilder moveTo(float x, float y, Color c) {
		return empty().add(x, y, c);
	}

	public PathBuilder lineTo(float x, float y) {
		return add(x, y);
	}

	public PathBuilder add(float x, float y) {
		return add(new Vec2f(x, y));
	}

	public PathBuilder add(Vec2f v) {
		l.add(v);
		return this;
	}

	public PathBuilder lineTo(float x, float y, Color c) {
		return add(x, y, c);
	}

	public PathBuilder add(float x, float y, Color c) {
		return add(new ColoredVec2f(x, y, c));
	}

	public PathBuilder rect(float x, float y, float w, float h) {
		return add(x, y).add(x + w, y).add(x + w, y + h).add(x, h);
	}

	public PathBuilder rect(float x, float y, float w, float h, Color topColor, Color bottomColor) {
		add(x, y, topColor).add(x + w, y, topColor);
		add(x + w, y + h, bottomColor).add(x, h, bottomColor);
		return this;
	}

	@Override
	public Iterator<Vec2f> iterator() {
		return Iterators.unmodifiableIterator(l.iterator());
	}

	public void fill(GLGraphics g) {
		g.fillPolygon(this);
	}

	public void fillTesselated(GLGraphics g) {
		g.fillPolygon(TesselatedPolygons.polygon2(l));
	}

	public void drawLoop(GLGraphics g) {
		g.drawPath(this, true);
	}

	public void drawLine(GLGraphics g) {
		g.drawPath(this, false);
	}

	public void drawPoints(GLGraphics g) {
		g.drawPoints(this);
	}
}
