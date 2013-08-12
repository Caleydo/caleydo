/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.v2.internal.ConstantBlockColorer;

/**
 * a visualization of the elements similar to enroute linear heatmap
 *
 * @author Samuel Gratzl
 *
 */
public class LinearBarHeatMapElement extends AHeatMapElement {
	private float normalizedCenter;

	public LinearBarHeatMapElement(TablePerspective tablePerspective) {
		this(tablePerspective, new ConstantBlockColorer(Color.NEUTRAL_GREY), EDetailLevel.HIGH);
	}

	public LinearBarHeatMapElement(TablePerspective tablePerspective, IBlockColorer blockColorer,
			EDetailLevel detailLevel) {
		super(tablePerspective, blockColorer, detailLevel);
	}

	@Override
	protected Vec2f getMinSizeImpl() {
		TablePerspective tablePerspective = selections.getTablePerspective();
		float w = tablePerspective.getNrDimensions() * (dimensionLabels.show() ? 16 : 1);
		float h = tablePerspective.getNrRecords() * (recordLabels.show() ? 16 : 10);
		return new Vec2f(w, h);
	}

	@Override
	protected void render(GLGraphics g, float w, float h) {
		final TablePerspective tablePerspective = selections.getTablePerspective();
		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();
		Table table = dataDomain.getTable();

		for (int i = 0; i < recordVA.size(); ++i) {
			Integer recordID = recordVA.get(i);
			if (isHidden(recordID)) {
				continue;
			}
			float y = recordSpacing.getPosition(i);
			float fieldHeight = recordSpacing.getSize(i);

			if (fieldHeight <= 0)
				continue;

			float center = fieldHeight * (1 - normalizedCenter);
			g.color(Color.GRAY).drawLine(0, y + center, w, y + center);
			for (int j = 0; j < dimensionVA.size(); ++j) {
				Integer dimensionID = dimensionVA.get(j);
				float x = dimensionSpacing.getPosition(j);
				float fieldWidth = dimensionSpacing.getSize(j);
				if (fieldWidth <= 0)
					continue;
				boolean deSelected = isDeselected(recordID);
				// get value
				Float value = table.getNormalizedValue(dimensionID, recordID);
				if (value == null)
					continue;
				Color color = blockColorer.apply(recordID, dimensionID, dataDomain, deSelected);
				g.color(color);
				float v = (value.floatValue() - normalizedCenter) * fieldHeight;
				g.fillRect(x, y + center, fieldWidth, -v);
			}
		}
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		super.onVAUpdate(tablePerspective);
		Table table = tablePerspective.getDataDomain().getTable();
		assert table instanceof NumericalTable;
		this.normalizedCenter = (float) ((NumericalTable) table).getNormalizedForRaw(Table.Transformation.NONE, 0);
	}

}
