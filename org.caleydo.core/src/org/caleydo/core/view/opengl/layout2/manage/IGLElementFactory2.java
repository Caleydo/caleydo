/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * advanced version of a {@link IGLElementFactory} with an additional support for describing the element
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLElementFactory2 extends IGLElementFactory {
	/**
	 * returns a {@link GLElementDimensionDesc} describing the created element in a given dimension
	 */
	GLElementDimensionDesc getDesc(EDimension dim, GLElement elem);

	/**
	 * returns a GLElement containing the ui elements for setting the parameter so this element
	 *
	 * @param elem
	 * @return an element or null if no parameters can be set
	 */
	GLElement createParameters(GLElement elem);
}
