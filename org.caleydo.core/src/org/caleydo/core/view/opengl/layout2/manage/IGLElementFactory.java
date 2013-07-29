/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGLElementFactory {
	String getId();

	GLElement create(GLElementFactoryContext context);

	boolean canCreate(GLElementFactoryContext context);
}
