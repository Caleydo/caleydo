/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import org.caleydo.core.view.opengl.layout2.GLElement;

import com.google.common.base.Predicate;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGLElementFactory extends Predicate<GLElementFactoryContext> {
	/**
	 * returns a unique id
	 *
	 * @return
	 */
	String getId();

	/**
	 * create the element for the given context
	 *
	 * @param context
	 * @return
	 */
	GLElement create(GLElementFactoryContext context);

	/**
	 * whether the element can be created
	 */
	@Override
	boolean apply(GLElementFactoryContext context);
}
