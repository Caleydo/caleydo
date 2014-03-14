/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.util.function.Functions2;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc.DescBuilder;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.view.heatmap.v2.BasicBlockColorer;
import org.caleydo.view.heatmap.v2.ListDataProvider;
import org.caleydo.view.heatmap.v2.ListDataProvider.DimensionData;
import org.caleydo.view.heatmap.v2.SingleHeatMapPlotElement;
import org.caleydo.view.heatmap.v2.TablePerspectiveDataProvider;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleHeatMapElementFactory extends ASingleElementFactory {

	@Override
	public String getId() {
		return "sheatmap";
	}

	@SuppressWarnings("unchecked")
	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		IHeatMapDataProvider data;
		EDimension dim;
		Function<? super Integer, Color> id2color;

		if (hasTablePerspective(context)) {
			final TablePerspectiveDataProvider d = new TablePerspectiveDataProvider(context.getData());
			data = d;
			dim = EDimension.get(context.getData().getNrRecords() == 1);
			final Integer id = d.getData(dim.opposite()).get(0);
			BasicBlockColorer c = new BasicBlockColorer(d.getDataDomain());
			final Function2<Integer, Integer, Color> getter = dim.isDimension() ? c : Functions2.swap(c);
			id2color = new Function<Integer, Color>() {
				@Override
				public Color apply(Integer input) {
					return getter.apply(input, id);
				}
			};
		} else {
			dim = context.get(EDimension.class, EDimension.RECORD);
			IDType idType = context.get(IDType.class, null);
			List<Integer> list = context.get(List.class, null);
			DimensionData d = new DimensionData(list, Functions.constant("UnNamed"), Collections.<Group> emptyList(),
					idType);
			//define data
			data = new ListDataProvider(dim.select(null, d), dim.select(d, null));
			id2color = context.get("id2color", Function.class, null);
		}
		boolean filledSelection = context.is("blurNotSelected");
		return new SingleHeatMapPlotElement(data, detailLevel, dim, id2color, filledSelection);
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		if (context.get(List.class, null) != null && context.get("id2color", Function.class, null) != null
				&& context.get(IDType.class, null) != null)
			return true;
		return hasTablePerspective(context);
	}

	@Override
	protected DescBuilder fixDesc() {
		return GLElementDimensionDesc.newFix(20).inRange(20, 20);
	}

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}
}

