/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import gleem.linalg.Vec2f;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.Function2;
import org.caleydo.core.util.function.Functions2;
import org.caleydo.core.util.function.IDoubleFunction;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc.DescBuilder;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;
import org.caleydo.view.heatmap.v2.ListDataProvider;
import org.caleydo.view.heatmap.v2.ListDataProvider.DimensionData;
import org.caleydo.view.heatmap.v2.SingleBarPlotElement;
import org.caleydo.view.heatmap.v2.TablePerspectiveDataProvider;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleBarElementFactory implements IGLElementFactory2 {

	@Override
	public String getId() {
		return "hbar";
	}

	@SuppressWarnings("unchecked")
	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);
		boolean blurNotSelected = context.is("blurNotSelected");

		IHeatMapDataProvider data;
		EDimension dim;
		Function<? super Integer, Double> id2double;
		Function<? super Double, Vec2f> value2bar;
		Function<? super Integer, Color> id2color;

		if (hasTablePerspective(context)) {
			final TablePerspectiveDataProvider d = new TablePerspectiveDataProvider(context.getData());
			data = d;
			dim = EDimension.get(context.getData().getNrRecords() == 1);
			final Integer id = d.getData(dim.opposite()).get(0);
			final Function2<Integer, Integer, Double> getter = dim.isDimension() ? d : Functions2.swap(d);
			id2double = new Function<Integer, Double>() {
				@Override
				public Double apply(Integer input) {
					return getter.apply(input, id);
				}
			};
		} else {
			dim = context.get(EDimension.class, EDimension.RECORD);
			IDType idType = context.get("idType", IDType.class, null);
			List<Integer> list = context.get(List.class, null);
			DimensionData d = new DimensionData(list, Functions.constant("UnNamed"), Collections.<Group> emptyList(),
					idType);
			//define data
			data = new ListDataProvider(dim.select(null, d), dim.select(d, null));
			id2double = context.get("id2double", Function.class, null);
		}
		id2color = context.get("id2Color", Function.class,
				Functions.constant(context.get("color", Color.class, Color.BLACK)));
		value2bar = extractValue2Bar(context);

		return new SingleBarPlotElement(data, detailLevel, blurNotSelected, dim, id2double, value2bar, id2color);
	}

	/**
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Function<? super Double, Vec2f> extractValue2Bar(GLElementFactoryContext context) {
		IDoubleFunction normalize = context.get("normalize", IDoubleFunction.class,
				DoubleFunctions.normalize(context.getDouble("min", 0), context.getDouble("max", 0)));
		final Value2BarConverter b;
		double center = context.getDouble("bar.center", Double.NaN);
		if (!Double.isNaN(center))
			b = new Value2BarConverter(center, normalize);
		else
			b = new Value2BarConverter(context.is("bar.left", true), normalize);

		return context.get("value2bar", Function.class, b);
	}

	/**
	 * @param context
	 * @return
	 */
	private boolean hasTablePerspective(GLElementFactoryContext context) {
		TablePerspective d = context.getData();
		if (d == null)
			return false;
		if (!DataSupportDefinitions.homogenousColumns.apply(d))
			return false;
		return d.getNrDimensions() == 1 || d.getNrRecords() == 1;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		if (context.get(List.class, null) != null && context.get("id2double", Function.class, null) != null && context.get("idType",IDType.class,null) != null)
			return true;
		return hasTablePerspective(context);
	}

	@Override
	public GLElementDimensionDesc getDesc(final EDimension dim, GLElement elem) {
		final SingleBarPlotElement l = (SingleBarPlotElement) elem;
		DescBuilder b;
		if (l.getDimension() == dim) {
			b = GLElementDimensionDesc.newCountDependent(1).locateUsing(new GLLocation.ALocator() {
				@Override
				public GLLocation apply(int dataIndex) {
					return l.getLocation(dim, dataIndex);
				}

				@Override
				public Set<Integer> unapply(GLLocation location) {
					return l.forLocation(dim, location);
				}
			});
		} else {
			b = GLElementDimensionDesc.newFix(50).minimum(10);
		}
		return b.build();
	}

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}
}

