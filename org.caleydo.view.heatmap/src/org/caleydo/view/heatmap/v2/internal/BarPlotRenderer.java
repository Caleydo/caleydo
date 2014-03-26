/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.util.function.IDoubleFunction;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.heatmap.v2.EScalingMode;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;

/**
 * a visualization of the elements similar to enroute linear heatmap
 *
 * @author Samuel Gratzl
 *
 */
public class BarPlotRenderer implements IHeatMapRenderer {
	/**
	 * whether the plot should be scaled to the local extreme or the global extreme (default)
	 */
	private final EScalingMode scalingMode;
	private final TablePerspective tablePerspective;

	private float rawCenter;
	private IRowNormalizer normalizer;

	private final Function2<Integer, Integer, Color> blockColorer;
	private List<Integer> dimensions;
	private List<Integer> records;


	public BarPlotRenderer(EScalingMode scalingMode, TablePerspective tablePerspective,
			Function2<Integer, Integer, Color> blockColorer) {
		this.scalingMode = scalingMode;
		this.tablePerspective = tablePerspective;
		this.blockColorer = blockColorer;
	}

	@Override
	public void takeDown() {

	}

	@Override
	public void render(GLGraphics g, float w, float h, ISpacingLayout recordSpacing, ISpacingLayout dimensionSpacing) {
		final Table table = tablePerspective.getDataDomain().getTable();

		for (int i = 0; i < records.size(); ++i) {
			Integer recordID = records.get(i);
			float y = recordSpacing.getPosition(i);
			float fieldHeight = recordSpacing.getSize(i);

			if (fieldHeight <= 0)
				continue;

			if (i % 2 == 1) // alternate shading
				g.color(0.95f).fillRect(0, y, w, fieldHeight);

			float normalizedCenter = normalizer.apply(i, rawCenter);
			float center = fieldHeight * (1 - normalizedCenter);

			g.color(Color.GRAY).drawLine(0, y + center, w, y + center);
			for (int j = 0; j < dimensions.size(); ++j) {
				Integer dimensionID = dimensions.get(j);
				float x = dimensionSpacing.getPosition(j);
				float fieldWidth = dimensionSpacing.getSize(j);
				if (fieldWidth <= 0)
					continue;
				fieldWidth = Math.max(fieldWidth, 1); // overplotting
				// get value
				Float value = table.getNormalizedValue(dimensionID, recordID);
				if (value == null)
					continue;
				Color color = blockColorer.apply(recordID, dimensionID);
				if (color == null)
					return;
				g.color(color);
				float v = normalizer.apply(i, value);
				v = (v - normalizedCenter) * fieldHeight;
				g.fillRect(x, y + center, fieldWidth, -v);
			}
		}
	}

	@Override
	public void update(IGLElementContext context, List<Integer> dimensions, List<Integer> records) {
		this.dimensions = dimensions;
		this.records = records;

		Table table = tablePerspective.getDataDomain().getTable();
		assert table instanceof NumericalTable;
		this.rawCenter = (float) ((NumericalTable) table).getNormalizedForRaw(Table.Transformation.LINEAR, 0);

		switch (this.scalingMode) {
		case GLOBAL:
			this.normalizer = IDENTITY_NORMALZIZER;
			break;
		case LOCAL:
			DoubleStatistics stats = DoubleStatistics.of(TableDoubleLists.asNormalizedList(tablePerspective));
			{
				double maxOffset = Math.max(this.rawCenter - stats.getMin(), stats.getMax() - this.rawCenter);
				this.normalizer = new UniformNormalizer(DoubleFunctions.normalize(this.rawCenter - maxOffset,
						this.rawCenter + maxOffset));
			}
			break;
		case LOCAL_ROW:
			List<IDoubleFunction> functions = new ArrayList<>(records.size());
			for (Integer recordID : records) {
				double maxOffset = Double.NEGATIVE_INFINITY;
				for (Integer dimensionID : dimensions) {
					float v = table.getNormalizedValue(dimensionID, recordID);
					maxOffset = Math.max(maxOffset, Math.max(this.rawCenter - v, v - this.rawCenter));
				}
				functions.add(DoubleFunctions.normalize(this.rawCenter - maxOffset, this.rawCenter + maxOffset));
			}
			this.normalizer = new RowNormalizer(functions.toArray(new IDoubleFunction[0]));
			break;
		}
	}

	private static interface IRowNormalizer {
		float apply(int row, float value);
	}

	private static final IRowNormalizer IDENTITY_NORMALZIZER = new IRowNormalizer() {
		@Override
		public float apply(int row, float value) {
			return value;
		}
	};

	private static final class UniformNormalizer implements IRowNormalizer {
		private final IDoubleFunction normalize;

		/**
		 * @param normalize
		 */
		public UniformNormalizer(IDoubleFunction normalize) {
			this.normalize = normalize;
		}

		@Override
		public float apply(int row, float value) {
			return (float) normalize.apply(value);
		}
	}

	private static final class RowNormalizer implements IRowNormalizer {
		private final IDoubleFunction[] normalize;

		/**
		 * @param normalize
		 */
		public RowNormalizer(IDoubleFunction[] normalize) {
			this.normalize = normalize;
		}

		@Override
		public float apply(int row, float value) {
			return (float) normalize[row].apply(value);
		}
	}
}
