/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.histogram.v2.HistogramElement;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class HistogramElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "histogram";
	}

	@Override
	public boolean canCreate(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		if (DataSupportDefinitions.homogenousTables.asTablePerspectivePredicate().apply(data))
			return true;
		//inhomogenous just a single column
		return data.getDimensionPerspective().getVirtualArray().size() == 1;
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		HistogramElement elem = new HistogramElement(data, detailLevel);
		elem.setShowColorMapper(context.is("showColorMapper"));
		return elem;
	}

}
