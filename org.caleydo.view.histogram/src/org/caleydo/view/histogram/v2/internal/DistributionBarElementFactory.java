/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.view.histogram.v2.ADistributionElement.EDistributionMode;

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
		vertical = context.is("vertical", vertical);
		return create(context, vertical ? EDistributionMode.VERTICAL_BAR : EDistributionMode.HORIZONTAL_BAR);
	}

}
