/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLElementSelector;

/**
 * visitor pattern for {@link GLElement}
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLElementVisitor<P, R> {
	R visit(GLElement elem, P para);

	R visit(GLElementContainer elem, P para);

	R visit(AnimatedGLElementContainer elem, P para);

	R visit(AGLElementDecorator elem, P para);

	R visit(GLElementSelector elem, P para);
}
