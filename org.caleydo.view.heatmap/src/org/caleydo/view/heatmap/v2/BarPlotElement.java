/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.view.heatmap.v2.internal.BarPlotRenderer;

/**
 * a visualization of the elements similar to enroute linear heatmap
 *
 * @author Samuel Gratzl
 *
 */
public class BarPlotElement extends HeatMapElementBase {
	private int minimumItemHeightFactor = 10;

	public BarPlotElement(TablePerspective tablePerspective) {
		this(tablePerspective, new BasicBlockColorer(tablePerspective.getDataDomain()), EDetailLevel.HIGH,
				EScalingMode.GLOBAL);
	}

	public BarPlotElement(TablePerspective tablePerspective, Function2<Integer, Integer, Color> blockColorer,
			EDetailLevel detailLevel,
 EScalingMode scalingMode) {
		super(new TablePerspectiveDataProvider(tablePerspective), new BarPlotRenderer(scalingMode, tablePerspective, blockColorer),
 detailLevel);
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
		if (!record.getLabel().show())
			r.setY(r.y() * minimumItemHeightFactor); // have a visible size also
		return r;
	}
}
