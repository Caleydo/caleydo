/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;

/**
 * Rendering the average similar to enroute.
 *
 * @author Samuel Gratzl
 */
public class AverageBarElement extends ASingleTablePerspectiveElement {
	private final EDetailLevel detailLevel;

	private FloatStatistics stats;

	public AverageBarElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH);
	}

	public AverageBarElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		super(tablePerspective);
		this.detailLevel = detailLevel;
		setPicker(null);
		onVAUpdate(tablePerspective);
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		this.stats = FloatStatistics.of(new TablePerspectiveFloatList(tablePerspective));
		super.onVAUpdate(tablePerspective);
	}
	/**
	 * @param renderBackground
	 *            setter, see {@link renderBackground}
	 */
	public void setRenderBackground(boolean renderBackground) {
		if (renderBackground)
			setRenderer(GLRenderers.fillRect(Color.WHITE));
		else
			setRenderer(null);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		float hi = Math.min(h * 0.5f, 30);
		float ys = hi * 0.1f;
		float y = (h - hi) * 0.5f;
		float v = w * stats.getMean();
		g.color(getTablePerspective().getDataDomain().getColor());
		g.fillRect(0, y, v, hi);
		g.color(Color.DARK_GRAY).drawRect(0, y, v, hi);

		float vd = w * stats.getSd();
		g.color(Color.BLACK);
		float center = h * 0.5f;
		g.drawLine(v - vd, center, v + vd, center);
		g.drawLine(v - vd, y + ys, v - vd, y + hi - ys);
		g.drawLine(v + vd, y + ys, v + vd, y + hi - ys);
	}


	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}

	@Override
	public final Vec2f getMinSize() {
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(300, 90);
		case MEDIUM:
			return new Vec2f(100, 30);
		default:
			return new Vec2f(40, 15);
		}
	}
}
