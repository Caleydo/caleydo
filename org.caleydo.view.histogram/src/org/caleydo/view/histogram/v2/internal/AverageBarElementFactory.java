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
import org.caleydo.view.histogram.v2.AverageBarElement;

/**
 * element factory for creating average bars
 *
 * @author Samuel Gratzl
 *
 */
public class AverageBarElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "average";
	}

	@Override
	public boolean canCreate(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		return DataSupportDefinitions.numericalColumns.apply(data);
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.LOW);

		AverageBarElement elem = new AverageBarElement(data, detailLevel);
		return elem;
	}

}
