/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.column;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleSummaryHeaderUI extends AColumnHeaderUI {
	public SimpleSummaryHeaderUI(final ARankColumnModel model, IRankTableUIConfig config) {
		super(model, config, true, false);
	}

	@Override
	protected void renderOrderGlyph(GLGraphics g, float w, float h) {
		//else handled by my parent
	}
}

