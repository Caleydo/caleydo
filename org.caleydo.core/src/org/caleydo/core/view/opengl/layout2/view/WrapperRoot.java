/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.view;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 *
 * @author Samuel Gratzl
 * 
 */
final class WrapperRoot extends AGLElementDecorator {
	@Override
	protected void layoutContent(IGLLayoutElement content) {
		Vec2f size = getSize();
		content.setBounds(0, 0, size.x(), size.y());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (content != null)
			content.render(g);
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (content != null)
			content.renderPick(g);
		super.renderPickImpl(g, w, h);
	}
}
