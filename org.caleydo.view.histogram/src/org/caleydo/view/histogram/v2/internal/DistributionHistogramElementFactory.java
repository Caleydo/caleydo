/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.view.histogram.v2.ADistributionElement.EDistributionMode;

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
		return create(context, EDistributionMode.HISTOGRAM);
	}

}
