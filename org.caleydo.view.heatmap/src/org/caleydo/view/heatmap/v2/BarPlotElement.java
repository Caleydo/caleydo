/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
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
	private final EScalingMode scalingMode;

	private float rawCenter;
	private IRowNormalizer normalizer;

	private int minimumItemHeightFactor = 10;

	public BarPlotElement(TablePerspective tablePerspective) {
		this(tablePerspective, BasicBlockColorer.INSTANCE, EDetailLevel.HIGH, EScalingMode.GLOBAL);
	}

	public BarPlotElement(TablePerspective tablePerspective, IBlockColorer blockColorer, EDetailLevel detailLevel,
			EScalingMode scalingMode) {
		super(tablePerspective, blockColorer, detailLevel);
		this.scalingMode = scalingMode;
	}

	/**
	 * @param minimumItemHeightFactor
	 *            setter, see {@link minimumItemHeightFactor}
	 */
	public void setMinimumItemHeightFactor(int minimumItemHeightFactor) {
		this.minimumItemHeightFactor = minimumItemHeightFactor;
	}

	/**
	 * @return the minimumItemHeightFactor, see {@link #minimumItemHeightFactor}
	 */
	public int getMinimumItemHeightFactor() {
		return minimumItemHeightFactor;
	}

	@Override
	protected Vec2f getMinSizeImpl() {
		Vec2f r = super.getMinSizeImpl();
		if (!recordLabels.show())
			r.setY(r.y() * minimumItemHeightFactor); // have a visible size also
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

			float normalizedCenter = normalizer.apply(i, rawCenter);
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
				float v = normalizer.apply(i, value);
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
			List<IDoubleFunction> functions = new ArrayList<>(tablePerspective.getNrRecords());
			final VirtualArray dimVA = tablePerspective.getDimensionPerspective().getVirtualArray();
			for (Integer recordID : tablePerspective.getRecordPerspective().getVirtualArray()) {
				double maxOffset = Double.NEGATIVE_INFINITY;
				for (Integer dimensionID : dimVA) {
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
