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
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc.DescBuilder;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.view.histogram.v2.HistogramDistributionElement;

/**
 * element factory for creating distribution elements
 *
 * @author Samuel Gratzl
 *
 */
public class DistributionHistogramElementFactory extends ADistributionBarElementFactory {
	@Override
	public String getId() {
		return "distribution.hist";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		boolean vertical = context.is("vertical", context.get(EDimension.class, EDimension.DIMENSION).isRecord());
		return new HistogramDistributionElement(createData(context), EDimension.get(!vertical));
	}

	@Override
	public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
		final DescBuilder builder = GLElementDimensionDesc.newFix(dim.select(((IHasMinSize) elem).getMinSize()));
		if (dim == ((HistogramDistributionElement) elem).getDimension())
			builder.locateUsing(((HistogramDistributionElement) elem));
		return builder.build();
	}

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}
}
