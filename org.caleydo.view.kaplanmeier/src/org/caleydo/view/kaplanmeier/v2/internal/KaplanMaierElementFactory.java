/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2.internal;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.util.function.MappedDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc.DescBuilder;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;
import org.caleydo.view.kaplanmeier.v2.AKaplanMeierElement;
import org.caleydo.view.kaplanmeier.v2.KaplanMeierElement;
import org.caleydo.view.kaplanmeier.v2.ListKaplanMeierElement;

import com.google.common.base.Function;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class KaplanMaierElementFactory implements IGLElementFactory2 {
	@Override
	public String getId() {
		return "kaplanmaier";
	}

	@Override
	public GLElementDimensionDesc getDesc(final EDimension dim, GLElement elem) {
		final AKaplanMeierElement k = (AKaplanMeierElement) elem;
		final DescBuilder b = GLElementDimensionDesc.newFix(dim.select(k.getMinSize()));
		b.locateUsing(new GLLocation.ALocator() {
			@Override
			public GLLocation apply(int dataIndex) {
				return k.getLocations(dim, Collections.singleton(dataIndex)).get(0);
			}

			@Override
			public Set<Integer> unapply(GLLocation location) {
				return k.forLocation(dim, location);
			}
		});
		return b.build();
	}

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		if (data != null) {
			//not an integer
			if (!DataSupportDefinitions.dataClass(EDataClass.NATURAL_NUMBER).apply(data))
				return false;
			// exact one column
			if (data.getDimensionPerspective().getVirtualArray().size() != 1)
				return false;
			return true;
		}

		if (context.get(IDoubleList.class, null) != null)
			return true;
		if (context.get(List.class, null) != null && context.get("id2double", Function.class, null) != null)
			return true;
		return false;
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.MEDIUM);
		TablePerspective data = context.getData();
		if (data != null)
			return new KaplanMeierElement(data, detailLevel, context.is("useParentMaxTimeIfPossible",
				true));

		IDType idType = context.get(IDType.class, null);
		@SuppressWarnings("unchecked")
		List<Integer> ids = context.get(List.class, null);
		@SuppressWarnings("unchecked")
		Function<Integer, Double> id2double = context.get("id2double", Function.class, null);

		IDoubleList l = context.get(IDoubleList.class, new MappedDoubleList<>(ids, id2double));
		assert l != null;
		ListKaplanMeierElement elem = new ListKaplanMeierElement(l, ids, idType, detailLevel);
		elem.setColor(context.get("color", Color.class, elem.getColor()));
		elem.setXAxis(context.get("xAxis", String.class, elem.getXAxis()));
		elem.setXMaxValue(context.getFloat("xMaxValue", elem.getXMaxValue()));
		elem.setYAxis(context.get("yAxis", String.class, elem.getYAxis()));
		elem.setFillCurve(context.is("fillCurve"));

		return elem;
	}

}
