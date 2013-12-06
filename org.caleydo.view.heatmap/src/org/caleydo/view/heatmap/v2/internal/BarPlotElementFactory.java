/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.heatmap.v2.BarPlotElement;
import org.caleydo.view.heatmap.v2.BasicBlockColorer;
import org.caleydo.view.heatmap.v2.EScalingMode;
import org.caleydo.view.heatmap.v2.EShowLabels;
import org.caleydo.view.heatmap.v2.HeatMapElementBase;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;
import org.caleydo.view.heatmap.v2.SpacingStrategies;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class BarPlotElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "heatmap.bar";
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		return DataSupportDefinitions.numericalTables.apply(context.getData());
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		@SuppressWarnings("unchecked")
		Function2<Integer, Integer, Color> blockColorer = context.get(Function2.class,
				new BasicBlockColorer(data.getDataDomain()));
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		BarPlotElement elem = new BarPlotElement(data, blockColorer, detailLevel, context.get(EScalingMode.class,
				EScalingMode.GLOBAL));

		elem.setMinimumItemHeightFactor(context.getInt("minimumItemHeightFactor", elem.getMinimumItemHeightFactor()));
		setCommon(context, elem);

		return elem;
	}

	static void setCommon(GLElementFactoryContext context, HeatMapElementBase elem) {
		EShowLabels default_ = context.get(EShowLabels.class, EShowLabels.NONE);
		elem.setLabel(EDimension.DIMENSION, context.get("dimensionLabels", EShowLabels.class, default_));
		elem.setLabel(EDimension.RECORD, context.get("recordLabels", EShowLabels.class, default_));
		elem.setTextWidth(context.getInt("textWidth", elem.getTextWidth()));

		ISpacingStrategy defaults_ = context.get(ISpacingStrategy.class, SpacingStrategies.UNIFORM);
		elem.setSpacingStrategy(EDimension.DIMENSION,
				context.get("dimensionSpacingStrategy", ISpacingStrategy.class, defaults_));
		elem.setSpacingStrategy(EDimension.RECORD,
				context.get("recordSpacingStrategy", ISpacingStrategy.class, defaults_));

		elem.setRenderGroupHints(context.is("renderGroupHints", false));
	}

}
