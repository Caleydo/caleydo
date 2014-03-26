/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.view.heatmap.v2.internal.HeatMapRenderer;

/**
 * a generic heat map implemenation
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapElement extends HeatMapElementBase {
	public HeatMapElement(TablePerspective tablePerspective) {
		this(tablePerspective, new BasicBlockColorer(tablePerspective.getDataDomain()), EDetailLevel.HIGH, false);
	}

	public HeatMapElement(TablePerspective tablePerspective, Function2<Integer, Integer, Color> blockColorer,
			EDetailLevel detailLevel, boolean forceTextures) {
		super(new TablePerspectiveDataProvider(tablePerspective), new HeatMapRenderer(detailLevel, forceTextures,
				blockColorer), detailLevel);
	}

	@Override
	public String toString() {
		return "Heat map";
	}

}
