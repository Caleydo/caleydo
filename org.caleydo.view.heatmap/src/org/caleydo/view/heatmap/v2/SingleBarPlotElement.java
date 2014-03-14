/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * a generic heat map implemenation
 *
 * @author Samuel Gratzl
 *
 */
public class SingleBarPlotElement extends ASingleElement {
	/**
	 * convert an id to an value
	 */
	private final Function<? super Integer, Double> id2double;
	/**
	 * convert a value to a bar (offset, size)
	 */
	private final Function<? super Double, Vec2f> value2bar;

	/**
	 * converts an id to a color
	 */
	protected final Function<? super Integer, Color> id2color;


	private Color frameColor = null;

	public SingleBarPlotElement(IHeatMapDataProvider data, EDetailLevel detailLevel, EDimension dim,
			Function<? super Integer, Double> id2double, Function<? super Double, Vec2f> value2bar,
			Function<? super Integer, Color> id2color) {
		super(data, detailLevel, dim);
		this.id2double = id2double;
		this.value2bar = value2bar;
		this.id2color = id2color;
	}

	/**
	 * @param showFrame
	 *            setter, see {@link showFrame}
	 */
	public void setFrameColor(Color frameColor) {
		if (Objects.equal(this.frameColor, frameColor))
			return;
		this.frameColor = frameColor;
		repaint();
	}

	/**
	 * @return the frameColor, see {@link #frameColor}
	 */
	public Color getFrameColor() {
		return frameColor;
	}

	@Override
	protected void render(GLGraphics g, float w, float h, ISpacingLayout spacing) {
		if (frameColor != null)
			g.color(frameColor).drawRect(0, 0, w, h);
		final EDimension dim = getDimension();
		List<Integer> ids = data.getData(dim);
		for (int i = 0; i < ids.size(); ++i) {
			final Integer id = ids.get(i);
			Double v = id2double.apply(id);
			if (v == null || Double.isNaN(v))
				continue;
			float pos = spacing.getPosition(i);
			float size = spacing.getSize(i);
			Vec2f bar = value2bar.apply(v);
			g.color(id2color.apply(id));
			if (dim.isVertical()) {
				g.fillRect(bar.x() * w, pos, (bar.y()) * w, size);
			} else {
				g.fillRect(pos, bar.x() * h, size, (bar.y()) * h);
			}
		}
	}
}
