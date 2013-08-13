/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.heatmap.v2.EShowLabels;
import org.caleydo.view.heatmap.v2.IBlockColorer;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;
import org.caleydo.view.heatmap.v2.LinearBarHeatMapElement;
import org.caleydo.view.heatmap.v2.SpacingStrategies;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class LinearBarHeatMapElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "heatmap.linearBar";
	}

	@Override
	public boolean canCreate(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		return DataSupportDefinitions.numericalTables.apply(data);
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		IBlockColorer blockColorer = context.get(IBlockColorer.class, ConstantBlockColorer.NEUTRAL_GREY);
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		LinearBarHeatMapElement elem = new LinearBarHeatMapElement(data, blockColorer, detailLevel);

		EShowLabels default_ = context.get(EShowLabels.class, EShowLabels.NONE);
		elem.setDimensionLabels(context.get("dimensionLabels", EShowLabels.class, default_));
		elem.setRecordLabels(context.get("recordLabels", EShowLabels.class, default_));
		elem.setTextWidth(context.getInt("textWidth", elem.getTextWidth()));

		ISpacingStrategy defaults_ = context.get(ISpacingStrategy.class, SpacingStrategies.UNIFORM);
		elem.setDimensionSpacingStrategy(context.get("dimensionSpacingStrategy", ISpacingStrategy.class, defaults_));
		elem.setRecordSpacingStrategy(context.get("recordSpacingStrategy", ISpacingStrategy.class, defaults_));

		return elem;
	}

}
