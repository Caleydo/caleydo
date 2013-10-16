/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2.internal;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.kaplanmeier.v2.KaplanMeierElement;

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
	public boolean canCreate(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		// exact one column
		if (data.getDimensionPerspective().getVirtualArray().size() != 1)
			return false;
		if (data.getRecordPerspective().getVirtualArray().size() == 0)
			return false;
		Integer firstDim = data.getDimensionPerspective().getVirtualArray().get(0);
		Integer firstRec = data.getRecordPerspective().getVirtualArray().get(0);
		EDataType rawDataType = data.getDataDomain().getTable().getRawDataType(firstDim, firstRec);
		// have integers
		return rawDataType == EDataType.INTEGER;
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.MEDIUM);
		KaplanMeierElement elem = new KaplanMeierElement(data, detailLevel, context.is("useParentMaxTimeIfPossible",
				true));
		return elem;
	}

}
