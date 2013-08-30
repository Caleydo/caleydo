/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.histogram.v2.DistributionElement;
import org.caleydo.view.histogram.v2.DistributionElement.EDistributionMode;

/**
 * element factory for creating distribution elements
 *
 * @author Samuel Gratzl
 *
 */
public class DistributionHistogramElementFactory implements IGLElementFactory {
	@Override
	public String getId() {
		return "distribution.hist";
	}

	@Override
	public boolean canCreate(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		return DataSupportDefinitions.categoricalColumns.apply(data);
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		TablePerspective data = context.getData();
		DistributionElement elem = new DistributionElement(data, EDistributionMode.HISTOGRAM);
		return elem;
	}

}
