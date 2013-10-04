/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainHeader extends GLElement {

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.drawText("Map onto Pathway", 43, 1, w - 43, 12);
		g.drawText("Map in enRoute", 43, 16, w - 43, 12);
		g.incZ(1);
		g.drawPath(false, new Vec2f(0, h), new Vec2f(20, 0), new Vec2f(w, 0));
		g.drawPath(false, new Vec2f(20, h), new Vec2f(32, 14), new Vec2f(w, 14));
		g.drawPath(false, new Vec2f(40, h), new Vec2f(44, 30), new Vec2f(w, 30));
		g.incZ(-1);
		super.renderImpl(g, w, h);
	}
}
