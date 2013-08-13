/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.v2.internal.HeatMapTextureRenderer;

/**
 * a generic heat map implemenation
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapElement extends AHeatMapElement {
	private final HeatMapTextureRenderer textureRenderer;

	public HeatMapElement(TablePerspective tablePerspective) {
		this(tablePerspective, BasicBlockColorer.INSTANCE, EDetailLevel.HIGH, false);
	}

	public HeatMapElement(TablePerspective tablePerspective, IBlockColorer blockColorer, EDetailLevel detailLevel,
			boolean forceTextures) {
		super(tablePerspective, blockColorer, detailLevel);

		// force texture or low details
		if (forceTextures || EDetailLevel.MEDIUM.compareTo(detailLevel) >= 0) {
			this.textureRenderer = new HeatMapTextureRenderer(tablePerspective, blockColorer);
		} else {
			this.textureRenderer = null;
		}
	}

	@Override
	protected void takeDown() {
		if (textureRenderer != null)
			textureRenderer.takeDown();
		super.takeDown();
	}

	@Override
	protected Vec2f getMinSizeImpl() {
		TablePerspective tablePerspective = selections.getTablePerspective();
		float w = tablePerspective.getNrDimensions() * (dimensionLabels.show() ? 16 : 1);
		float h = tablePerspective.getNrRecords() * (recordLabels.show() ? 16 : 1);
		return new Vec2f(w, h);
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		if (textureRenderer != null) {
			textureRenderer.create(context);
		}
		super.onVAUpdate(tablePerspective);
	}

	/**
	 * render the heatmap as blocks
	 *
	 * @param g
	 * @param w
	 * @param h
	 */
	@Override
	protected void render(GLGraphics g, float w, float h) {
		if (textureRenderer != null && isUniform()) {
			textureRenderer.render(g, w, h);
			return;
		}

		final TablePerspective tablePerspective = selections.getTablePerspective();
		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		for (int i = 0; i < recordVA.size(); ++i) {
			Integer recordID = recordVA.get(i);
			if (isHidden(recordID)) {
				continue;
			}
			float y = recordSpacing.getPosition(i);
			float fieldHeight = recordSpacing.getSize(i);

			if (fieldHeight <= 0)
				continue;

			for (int j = 0; j < dimensionVA.size(); ++j) {
				Integer dimensionID = dimensionVA.get(j);
				float x = dimensionSpacing.getPosition(j);
				float fieldWidth = dimensionSpacing.getSize(j);
				if (fieldWidth <= 0)
					continue;
				boolean deSelected = isDeselected(recordID);
				Color color = blockColorer.apply(recordID, dimensionID, dataDomain, deSelected);
				g.color(color).fillRect(x, y, fieldWidth, fieldHeight);
			}
		}
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		if (textureRenderer != null && context != null)
			textureRenderer.create(context);
	}

	@Override
	public String toString() {
		return "Heat map for " + selections;
	}

}
