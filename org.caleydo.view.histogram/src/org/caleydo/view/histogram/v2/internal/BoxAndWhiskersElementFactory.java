/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;


import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.histogram.v2.BoxAndWhiskersElement;
import org.caleydo.view.histogram.v2.BoxAndWhiskersMultiElement;
import org.caleydo.view.histogram.v2.ListBoxAndWhiskersElement;

/**
 * element factory for creating average bars
 *
 * @author Samuel Gratzl
 *
 */
public class BoxAndWhiskersElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "boxandwhiskers";
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		// 1. table perspective data
		if (DataSupportDefinitions.dataClass(EDataClass.REAL_NUMBER, EDataClass.NATURAL_NUMBER)
				.apply(context.getData()))
			return true;
		// 2. double list
		if (context.get(IDoubleList.class, null) != null)
			return true;
		// 3. just stats
		if (context.get(AdvancedDoubleStatistics.class, null) != null)
			return true;
		return false;
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		boolean showOutliers = context.is("showOutliers");
		EDimension split = context.get("splitGroups", EDimension.class, null);
		EDimension direction = context.get("direction", EDimension.class, EDimension.RECORD);

		boolean showScale = context.is("showScale");
		boolean showMinMax = context.is("showMinMax");

		if (context.getData() != null) {
			TablePerspective data = context.getData();
			if ((split == EDimension.DIMENSION && getGroupsSize(data.getDimensionPerspective()) > 1)
					|| (split == EDimension.RECORD && getGroupsSize(data.getRecordPerspective()) > 1)) {
				BoxAndWhiskersMultiElement b = new BoxAndWhiskersMultiElement(data, detailLevel, split, showOutliers,
						showMinMax);
				b.setShowScale(showScale);
				return b;
			} else {
				BoxAndWhiskersElement b = new BoxAndWhiskersElement(data, detailLevel, direction, showOutliers,
						showMinMax);
				b.setShowScale(showScale);
				return b;
			}
		} else {
			IDoubleList list = context.get(IDoubleList.class, null);
			final String labels = context.get("label", String.class, "<Unnamed>");
			final Color color = context.get("color", Color.class, Color.LIGHT_GRAY);
			if (list != null) {
				return new ListBoxAndWhiskersElement(list, detailLevel, direction, showOutliers, showMinMax, labels,
						color);
			} else {
				AdvancedDoubleStatistics stats = context.get(AdvancedDoubleStatistics.class, null);
				assert stats != null;
				return new ListBoxAndWhiskersElement(stats, detailLevel, direction, showMinMax, labels, color);
			}
		}
	}

	/**
	 * @param dimensionPerspective
	 * @return
	 */
	private static int getGroupsSize(Perspective p) {
		return p.getVirtualArray().getGroupList().size();
	}

}
