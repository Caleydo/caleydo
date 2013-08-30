/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;


import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.lineup.model.CategoricalRankRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public class CategoricalScoreBarElement extends ScoreBarElement {

	public CategoricalScoreBarElement(CategoricalRankRankColumnModel<?> model) {
		super(model);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final IRow r = getLayoutDataAs(IRow.class, null); // current row
		float v = model.applyPrimitive(r);
		boolean inferred = model.isValueInferred(r);
		Color color = ((CategoricalRankRankColumnModel<?>) model).getColor(r);
		renderValue(g, w, h, r, v, inferred, false, color, color);
	}
}
