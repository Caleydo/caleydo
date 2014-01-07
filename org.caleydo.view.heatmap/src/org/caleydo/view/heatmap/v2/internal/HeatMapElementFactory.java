/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;
import org.caleydo.view.heatmap.v2.BasicBlockColorer;
import org.caleydo.view.heatmap.v2.HeatMapElement;
import org.caleydo.view.heatmap.v2.HeatMapElementBase;
import org.caleydo.view.heatmap.v2.ListDataProvider;
import org.caleydo.view.heatmap.v2.ListDataProvider.DimensionData;
import org.caleydo.view.heatmap.v2.internal.BarPlotElementFactory.SpacingStrategyLocator;

import com.google.common.base.Function;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapElementFactory implements IGLElementFactory2 {
	@Override
	public String getId() {
		return "heatmap";
	}

	@Override
	public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
		return GLElementDimensionDesc.newBuilder().linear(1)
				.locateUsing(new SpacingStrategyLocator(dim, (HeatMapElementBase) elem)).build();
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		if (DataSupportDefinitions.homogenousColumns.apply(context.getData()))
			return true;

		boolean hasColorer = context.get(Function2.class, null) != null;
		boolean hasRecords = context.get("records", List.class, null) != null;
		boolean hasDimension = context.get("dimensions", List.class, null) != null;
		return hasColorer && hasRecords && hasDimension;
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);
		boolean blurNotSelected = context.is("blurNotSelected");
		boolean forceTextures = context.is("forceTextures");
		@SuppressWarnings("unchecked")
		Function2<Integer, Integer, Color> colorer = context.get(Function2.class, null);

		HeatMapElementBase elem;
		TablePerspective data = context.getData();
		if (data != null) {
			if (colorer == null)
				colorer = new BasicBlockColorer(data.getDataDomain());
			elem = new HeatMapElement(data, colorer, detailLevel, forceTextures, blurNotSelected);
		} else {
			IHeatMapDataProvider datap = new ListDataProvider(toData(context, "records"), toData(context, "dimensions"));
			IHeatMapRenderer renderer = new HeatMapRenderer(detailLevel, forceTextures, colorer);
			elem = new HeatMapElementBase(datap, renderer, detailLevel, blurNotSelected);
		}

		BarPlotElementFactory.setCommon(context, elem);

		return elem;
	}


	private static DimensionData toData(GLElementFactoryContext context, String key) {
		@SuppressWarnings("unchecked")
		List<Integer> data = context.get(key, List.class, null);
		IDType idType = context.get(key + ".idType", IDType.class, null);
		@SuppressWarnings("unchecked")
		Function<Integer, String> labels = context.get(key + ".labels", Function.class, null);
		if (labels == null)
			labels = new Function<Integer, String>() {
				@Override
				public String apply(Integer input) {
					return Objects.toString(input);
				}
			};
		@SuppressWarnings("unchecked")
		List<Group> groups = context.get(key + ".groups", List.class, Collections.emptyList());

		return new DimensionData(data, labels, groups, idType);
	}

}
