/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;
import org.caleydo.view.histogram.v2.ADistributionElement.EDistributionMode;

/**
 * element factory for creating distribution elements
 *
 * @author Samuel Gratzl
 *
 */
public class DistributionPieElementFactory extends ADistributionBarElementFactory implements IGLElementFactory2 {
	@Override
	public String getId() {
		return "distribution.pie";
	}

	@Override
	public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
		return GLElementDimensionDesc.newBuilder().fix(dim.select(((IHasMinSize) elem).getMinSize())).build();
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		return create(context, EDistributionMode.PIE);
	}

}
