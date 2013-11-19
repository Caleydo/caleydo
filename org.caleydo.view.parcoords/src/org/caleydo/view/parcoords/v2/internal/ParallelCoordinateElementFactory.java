/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.parcoords.v2.ParallelCoordinateElement;

/**
 * @author Samuel Gratzl
 *
 */
public class ParallelCoordinateElementFactory implements IGLElementFactory {

	@Override
	public String getId() {
		return "paco";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		EDetailLevel detailLevel = context.get(EDetailLevel.class, EDetailLevel.HIGH);
		return new ParallelCoordinateElement(context.getData(), detailLevel);
	}

	@Override
	public boolean canCreate(GLElementFactoryContext context) {
		if (context.getData() == null)
			return false;

		return true;
	}

}
