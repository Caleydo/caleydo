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
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.histogram.v2.BoxAndWhiskersElement;
import org.caleydo.view.histogram.v2.BoxAndWhiskersMultiElement;

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
	public boolean canCreate(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		return DataSupportDefinitions.dataClass(EDataClass.REAL_NUMBER, EDataClass.NATURAL_NUMBER).apply(data);
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		EDimension split = context.get("splitGroups", EDimension.class, null);
		EDimension direction = context.get("direction", EDimension.class, EDimension.RECORD);
		if ((split == EDimension.DIMENSION && getGroupsSize(data.getDimensionPerspective()) > 1)
				|| (split == EDimension.RECORD && getGroupsSize(data.getRecordPerspective()) > 1)) {
			BoxAndWhiskersMultiElement b = new BoxAndWhiskersMultiElement(data, detailLevel, split);
			b.setShowScale(context.is("showScale"));
			return b;
		} else {
			BoxAndWhiskersElement b = new BoxAndWhiskersElement(data, detailLevel, direction);
			b.setShowScale(context.is("showScale"));
			return b;
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
