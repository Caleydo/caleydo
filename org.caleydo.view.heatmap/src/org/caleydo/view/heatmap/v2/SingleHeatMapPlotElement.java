/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.internal.HeatMapRenderer;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;
import org.caleydo.view.heatmap.v2.internal.IHeatMapRenderer;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * a generic heat map implemenation
 *
 * @author Samuel Gratzl
 *
 */
public class SingleHeatMapPlotElement extends ASingleElement implements Function2<Integer, Integer, Color> {
	private final IHeatMapRenderer hRenderer;
	private final Function<? super Integer, Color> id2color;

	public SingleHeatMapPlotElement(IHeatMapDataProvider data, EDetailLevel detailLevel, boolean blurNotSelected,
			EDimension dim, Function<? super Integer, Color> id2color) {
		super(data, detailLevel, blurNotSelected, dim);
		this.id2color = id2color;
		this.hRenderer = new HeatMapRenderer(detailLevel, true, this);
	}

	@Override
	public Color apply(Integer recordID, Integer dimensionID) {
		Integer in = getDimension().select(dimensionID, recordID);
		return id2color.apply(in);
	}

	@Override
	protected void render(GLGraphics g, float w, float h, ISpacingLayout spacing) {
		EDimension dim = getDimension();
		ISpacingLayout simple = new SimpleSpacing(dim.opposite().select(w, h));
		hRenderer.render(g, w, h, dim.select(spacing, simple), dim.select(simple, spacing));
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		updateRenderer();
	}

	@Override
	protected void takeDown() {
		hRenderer.takeDown();
		super.takeDown();
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		updateRenderer();
		repaint();
	}

	@Override
	public void onDataUpdate() {
		updateRenderer();
		super.onDataUpdate();
	}

	/**
	 *
	 */
	protected final void updateRenderer() {
		if (context == null)
			return;
		EDimension dim = getDimension();
		List<Integer> data = getData();
		List<Integer> simple = ImmutableList.of(-1);
		hRenderer.update(context, dim.select(data, simple), dim.select(simple, data));
	}

	private static final class SimpleSpacing implements ISpacingLayout {
		private final float max;

		private SimpleSpacing(float max) {
			this.max = max;
		}

		@Override
		public float getPosition(int index) {
			return 0;
		}

		@Override
		public float getSize(int index) {
			return max;
		}

		@Override
		public int getIndex(float position) {
			return 0;
		}

		@Override
		public boolean isUniform() {
			return true;
		}

	}
}
