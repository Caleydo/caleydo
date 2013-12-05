/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2.internal;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.kaplanmeier.v2.KaplanMeierElement;
import org.caleydo.view.kaplanmeier.v2.ListKaplanMeierElement;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class KaplanMaierElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "kaplanmaier";
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
		return false;
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.MEDIUM);
		TablePerspective data = context.getData();
		if (data != null)
			return new KaplanMeierElement(data, detailLevel, context.is("useParentMaxTimeIfPossible",
				true));
		IDoubleList l = context.get(IDoubleList.class, null);
		assert l != null;
		ListKaplanMeierElement elem = new ListKaplanMeierElement(l, detailLevel);
		elem.setColor(context.get("color", Color.class, elem.getColor()));
		elem.setXAxis(context.get("xAxis", String.class, elem.getXAxis()));
		elem.setXMaxValue(context.getFloat("xMaxValue", elem.getXMaxValue()));
		elem.setYAxis(context.get("yAxis", String.class, elem.getYAxis()));

		return elem;
	}

}
