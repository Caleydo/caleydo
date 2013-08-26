/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;

/**
 * @author Samuel Gratzl
 *
 */
public interface IScriptedColumnMixin {
	String PROP_CODE = "code";

	String getCode();

	void editCode(GLElement summary, IGLElementContext context);
}
