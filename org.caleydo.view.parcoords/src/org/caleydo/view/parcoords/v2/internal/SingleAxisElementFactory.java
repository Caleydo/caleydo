/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;
import org.caleydo.view.parcoords.v2.SingleAxisElement;

import com.google.common.base.Function;

/**
 * render a single element as an axis, e.g. to represent a PCP
 *
 * @author Samuel Gratzl
 *
 */
public class SingleAxisElementFactory implements IGLElementFactory2 {

	@Override
	public String getId() {
		return "axis";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		SingleAxisElement elem = null;

		if (hasTablePerspective(context))
			elem = new SingleAxisElement(context.getData());
		else {
			EDimension dim = context.get(EDimension.class, EDimension.RECORD);
			double min = context.getDouble("min", Double.NaN);
			double max = context.getDouble("max", Double.NaN);

			if (context.get(List.class, null) != null && context.get("id2double", Function.class, null) != null) {
				@SuppressWarnings("unchecked")
				List<Integer> data = context.get(List.class, null);
				IDType idType = context.get(IDType.class, null);
				@SuppressWarnings("unchecked")
				Function<Integer, Double> f = context.get("id2double", Function.class, null);
				elem = new SingleAxisElement(dim, data, idType, f, min, max);
			} else if (context.get(IDoubleList.class, null) != null) {
				IDoubleList data = context.get(IDoubleList.class, null);
				elem = new SingleAxisElement(dim, data, min, max);
			}
		}
		if (elem == null)
			return null;
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.MEDIUM);
		String[] markers = context.get("markers", String[].class, null);
		if (markers != null)
			elem.setMarker(markers);
		else if (detailLevel == EDetailLevel.HIGH)
			elem.setNumberMarkers();
		return elem;

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
		if (context.get(List.class, null) != null && context.get("id2double", Function.class, null) != null)
			return true;
		if (context.get(IDoubleList.class, null) != null)
			return true;
		return hasTablePerspective(context);
	}

	@Override
	public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
		SingleAxisElement l = (SingleAxisElement) elem;
		return l.getDesc(dim);
	}

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}
}
