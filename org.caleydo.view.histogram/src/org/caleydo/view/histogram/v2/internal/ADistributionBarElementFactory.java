/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.List;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;

/**
 * element factory for creating distribution elements
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ADistributionBarElementFactory implements IGLElementFactory2 {
	@Override
	public boolean apply(GLElementFactoryContext context) {
		// first using table perspective
		if (DataSupportDefinitions.categoricalColumns.apply(context.getData()))
			return true;
		// second using va
		if (context.get(VirtualArray.class, null) != null)
			return true;
		// third using perspective
		if (context.get(Perspective.class, null) != null)
			return true;
		// forth using a histogram
		if (context.get(Histogram.class, null) != null)
			return true;
		return false;
	}

	/**
	 * @param context
	 * @param eDistributionMode
	 * @return
	 */
	protected IDistributionData createData(GLElementFactoryContext context) {
		if (context.getData() != null)
			return new TablePerspectiveDistributionData(context.getData());
		else {
			Color[] colors = context.get("colors", Color[].class,
					context.get("distribution.colors", Color[].class, null));
			String[] labels = context.get("labels", String[].class,
					context.get("distribution.labels", String[].class, null));
			@SuppressWarnings("unchecked")
			List<Integer> ids = context.get("data", List.class, null);
			Histogram hist = context.get(Histogram.class,null);
			IDType idType = context.get("idType", IDType.class,null);

			VirtualArray va = context.get(VirtualArray.class, null);
			if (va == null) {
				Perspective p = context.get(Perspective.class, null);
				va = p == null ? null : p.getVirtualArray();
			}
			if (va != null) { // extract data from va
				idType = va.getIdType();
				hist = createHist(va);
				labels = createLabels(va.getGroupList());
				ids = va.getIDs();
			}
			assert hist != null;
			if (hist instanceof CategoricalHistogram) { // extract colors and labels from advanced va
				labels = toLabels((CategoricalHistogram) hist);
				colors = toColors((CategoricalHistogram) hist);
			}
			int largestValue = context.getInt("distribution.largestBin", hist.getLargestValue());
			int total = context.getInt("distribution.total", -1);
			return new HistDistributionData(hist, largestValue, idType, labels, colors, ids, total);
		}
	}


	private static Color[] toColors(CategoricalHistogram hist) {
		Color[] r = new Color[hist.size()];
		for (int i = 0; i < r.length; ++i)
			r[i] = hist.getColor(i);
		return r;
	}


	private static String[] toLabels(CategoricalHistogram hist) {
		String[] r = new String[hist.size()];
		for (int i = 0; i < r.length; ++i)
			r[i] = hist.getName(i);
		return r;
	}

	private static String[] createLabels(GroupList groups) {
		String[] r = new String[groups.size()];
		for (int i = 0; i < r.length; ++i)
			r[i] = groups.get(i).getLabel();
		return r;
	}

	private static Histogram createHist(VirtualArray va) {
		GroupList groups = va.getGroupList();
		Histogram h = new Histogram(groups.size());
		int cc = 0;
		for (int i = 0; i < groups.size(); ++i) {
			Group g = groups.get(i);
			for (int j = g.getStartIndex(); j < g.getEndIndex(); ++j)
				h.add(i, va.get(j));
			cc += g.getSize();
		}
		if (cc < va.size())
			for (int j = cc; j < va.size(); ++j)
				h.addNAN(va.get(j));
		return h.optimize();
	}
}
