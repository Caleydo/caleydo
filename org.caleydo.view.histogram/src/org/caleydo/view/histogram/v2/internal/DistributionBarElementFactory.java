/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc.DescBuilder;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.view.histogram.v2.BarDistributionElement;

/**
 * element factory for creating distribution elements
 *
 * @author Samuel Gratzl
 *
 */
public class DistributionBarElementFactory extends ADistributionBarElementFactory {
	@Override
	public String getId() {
		return "distribution.bar";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		boolean vertical = true;
		if (context.getData() != null) {
			TablePerspective data = context.getData();
			vertical = data.getDimensionPerspective().getVirtualArray().size() == 1;
		}
		vertical = context.is("vertical", context.get(EDimension.class, EDimension.get(vertical)).isRecord());
		return new BarDistributionElement(createData(context), vertical);
	}

	@Override
	public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
		BarDistributionElement bar = (BarDistributionElement) elem;

		final DescBuilder b;
		if (dim == bar.getDimension()) {
			b = GLElementDimensionDesc.newCountDependent(1).locateUsing(bar);
		} else {
			b = GLElementDimensionDesc.newFix(dim.select(((IHasMinSize) elem).getMinSize()));
		}
		return b.build();
	}

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}

}
