/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;

/**
 * @author Samuel Gratzl
 *
 */
public class HeatMapRenderer implements IHeatMapRenderer {
	private final HeatMapTextureRenderer textureRenderer;

	private final Function2<Integer, Integer, Color> colorer;
	private List<Integer> dimensions;
	private List<Integer> records;

	public HeatMapRenderer(EDetailLevel detailLevel, boolean forceTextures, Function2<Integer, Integer, Color> colorer) {
		this.colorer = colorer;
		if (forceTextures || EDetailLevel.MEDIUM.compareTo(detailLevel) >= 0) {
			this.textureRenderer = new HeatMapTextureRenderer();
		} else {
			this.textureRenderer = null;
		}
	}

	@Override
	public void takeDown() {
		if (textureRenderer != null)
			textureRenderer.takeDown();
	}

	@Override
	public void update(IGLElementContext context, List<Integer> dimensions, List<Integer> records) {
		this.dimensions = dimensions;
		this.records = records;
		if (textureRenderer != null) {
			textureRenderer.create(context, dimensions, records, colorer);
		}

	}

	@Override
	public void render(GLGraphics g, float w, float h, ISpacingLayout recordSpacing, ISpacingLayout dimensionSpacing) {
		if (recordSpacing.isUniform() && dimensionSpacing.isUniform() && textureRenderer != null
				&& textureRenderer.render(g, w, h)) {
			return;
		}

		for (int i = 0; i < records.size(); ++i) {
			Integer recordID = records.get(i);
			float y = recordSpacing.getPosition(i);
			float fieldHeight = recordSpacing.getSize(i);

			if (fieldHeight <= 0)
				continue;

			for (int j = 0; j < dimensions.size(); ++j) {
				Integer dimensionID = dimensions.get(j);
				float x = dimensionSpacing.getPosition(j);
				float fieldWidth = dimensionSpacing.getSize(j);
				if (fieldWidth <= 0)
					continue;
				Color color = colorer.apply(recordID, dimensionID);
				g.color(color).fillRect(x, y, fieldWidth, fieldHeight);
			}
		}
	}
}
