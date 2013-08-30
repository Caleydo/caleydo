/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.IRow;

/**
 * contract that the column has an underlying mapping, which transform the values
 *
 * @author Samuel Gratzl
 *
 */
public interface IMappedColumnMixin extends IRankColumnModel {
	String PROP_MAPPING = "mapping";

	/**
	 * triggers to open the edit dialog given its summary element
	 *
	 * @param summary
	 * @param context
	 */
	void editMapping(GLElement summary, IGLElementContext context, IRankTableUIConfig config);

	/**
	 * returns a representation of the raw value
	 *
	 * @param row
	 * @return
	 */
	String getRawValue(IRow row);

	/**
	 * @return whether an different style should be used for renderign the values to indicate that the mapping is
	 *         complex
	 */
	boolean isComplexMapping();
}
