/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * mixin that the column has a annotation
 *
 * @author Samuel Gratzl
 *
 */
public interface IAnnotatedColumnMixin {
	String PROP_DESCRIPTION = "description";
	String PROP_TITLE = "title";

	void editAnnotation(GLElement summary);

	String getDescription();

	String getLabel();

	/**
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * @param description
	 */
	void setDescription(String description);
}
