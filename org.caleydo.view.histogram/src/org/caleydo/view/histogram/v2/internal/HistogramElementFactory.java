/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.histogram.v2.HistogramElement;

import com.google.common.base.Predicate;

/**
 * element factory for creating heatmaps
 *
 * @author Samuel Gratzl
 *
 */
public class HistogramElementFactory implements IGLElementFactory {
	private final static Predicate<TablePerspective> REAL_OR_NATURAL = DataSupportDefinitions.dataClass(
			EDataClass.REAL_NUMBER, EDataClass.NATURAL_NUMBER);
	@Override
	public String getId() {
		return "histogram";
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		return REAL_OR_NATURAL.apply(data);
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		HistogramElement elem = new HistogramElement(data, detailLevel);
		elem.setShowColorMapper(context.is("showColorMapper"));
		elem.setShowMarkerLabels(context.is("showMarkerLabels"));
		return elem;
	}

}
