/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;

import com.google.common.base.Function;

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

	private Color outline = null;


	public SingleBarPlotElement(IHeatMapDataProvider data, EDetailLevel detailLevel, EDimension dim,
			Function<? super Integer, Double> id2double, Function<? super Double, Vec2f> value2bar,
			Function<? super Integer, Color> id2color) {
		super(data, detailLevel, dim);
		this.id2double = id2double;
		this.value2bar = value2bar;
		this.id2color = id2color;
	}

	/**
	 * @return
	 */
	public Color getOutline() {
		return outline;
	}

	/**
	 * @param outline
	 *            setter, see {@link outline}
	 */
	public void setOutline(Color outline) {
		if (Objects.equals(outline, this.outline))
			return;
		this.outline = outline;
		repaint();
	}

	@Override
	protected void render(GLGraphics g, float w, float h, ISpacingLayout spacing) {
		final EDimension dim = getDimension();
		List<Integer> ids = data.getData(dim);
		Color o = dim.select(w, h) / ids.size() > 2 ? outline : null;

		for (int i = 0; i < ids.size(); ++i) {
			final Integer id = ids.get(i);
			Double v = id2double.apply(id);
			if (v == null || Double.isNaN(v))
				continue;
			float pos = spacing.getPosition(i);
			float size = spacing.getSize(i) - (o != null ? 1 : 0);
			Vec2f bar = value2bar.apply(v);
			g.color(id2color.apply(id));
			if (dim.isVertical()) {
				g.fillRect(bar.x() * w, pos, (bar.y()) * w, size);
			} else {
				g.fillRect(pos, bar.x() * h, size, (bar.y()) * h);
			}
			if (o != null) {
				g.color(o);
				if (dim.isVertical()) {
					g.drawLine(bar.x() * w, pos + size, bar.x() * w + (bar.y()) * w + 1, pos + size);
				} else {
					g.drawLine(pos + size, bar.x() * h, pos + size, bar.x() * h + (bar.y()) * h + 1);
				}
			}
		}
	}

}
