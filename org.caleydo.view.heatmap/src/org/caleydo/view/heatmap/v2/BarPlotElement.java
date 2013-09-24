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
import org.caleydo.core.data.perspective.table.TablePerspectiveDoubleList;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.ExpressionFunctions;
import org.caleydo.core.util.function.IDoubleFunction;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * a visualization of the elements similar to enroute linear heatmap
 *
 * @author Samuel Gratzl
 *
 */
public class BarPlotElement extends AHeatMapElement {
	/**
	 * whether the plot should be scaled to the local extreme or the global extreme (default)
	 */
	private final boolean scaleLocally;

	private float normalizedCenter;
	private IDoubleFunction normalize;

	public BarPlotElement(TablePerspective tablePerspective) {
		this(tablePerspective, BasicBlockColorer.INSTANCE, EDetailLevel.HIGH, false);
	}

	public BarPlotElement(TablePerspective tablePerspective, IBlockColorer blockColorer,
			EDetailLevel detailLevel, boolean scaleLocally) {
		super(tablePerspective, blockColorer, detailLevel);
		this.scaleLocally = scaleLocally;
	}

	@Override
	protected Vec2f getMinSizeImpl() {
		Vec2f r = super.getMinSizeImpl();

		if (!recordLabels.show())
			r.setY(r.y() * 10); // have a visible size also
		return r;
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

			if (i % 2 == 1) // alternate shading
				g.color(0.95f).fillRect(0, y, w, fieldHeight);

			float center = fieldHeight * (1 - normalizedCenter);

			g.color(Color.GRAY).drawLine(0, y + center, w, y + center);
			for (int j = 0; j < dimensionVA.size(); ++j) {
				Integer dimensionID = dimensionVA.get(j);
				float x = dimensionSpacing.getPosition(j);
				float fieldWidth = dimensionSpacing.getSize(j);
				if (fieldWidth <= 0)
					continue;
				fieldWidth = Math.max(fieldWidth, 1); // overplotting
				boolean deSelected = isDeselected(recordID);
				// get value
				Float value = table.getNormalizedValue(dimensionID, recordID);
				if (value == null)
					continue;
				Color color = blockColorer.apply(recordID, dimensionID, dataDomain, deSelected);
				g.color(color);
				float v = (float) normalize.apply(value);
				v = (v - normalizedCenter) * fieldHeight;
				g.fillRect(x, y + center, fieldWidth, -v);
			}
		}
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		super.onVAUpdate(tablePerspective);
		Table table = tablePerspective.getDataDomain().getTable();
		assert table instanceof NumericalTable;
		this.normalizedCenter = (float) ((NumericalTable) table).getNormalizedForRaw(Table.Transformation.LINEAR, 0);

		if (scaleLocally) {
			DoubleStatistics stats = DoubleStatistics.of(new TablePerspectiveDoubleList(tablePerspective));
			double max = Math.max(-stats.getMin(), stats.getMax());
			normalize = DoubleFunctions.normalize(-max, max);
		} else {
			normalize = ExpressionFunctions.IDENTITY;
		}
		this.normalizedCenter = (float) normalize.apply(this.normalizedCenter);
	}

}
