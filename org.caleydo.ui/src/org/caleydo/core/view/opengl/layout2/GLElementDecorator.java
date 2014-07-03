/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * simple implementation of a {@link AGLElementDecorator}, that uses the whole size for the decorated element
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementDecorator extends AGLElementDecorator {

	public GLElementDecorator() {
		super();
	}

	public GLElementDecorator(GLElement content) {
		super(content);
	}

	@Override
	public void repaint() {
		super.repaint();
		if (content != null)
			GLElementAccessor.repaintDown(content);
	}

	@Override
	public void repaintPick() {
		super.repaintPick();
		if (content != null)
			GLElementAccessor.repaintPickDown(content);
	}

	@Override
	protected void layoutContent(IGLLayoutElement content, float w, float h, int deltaTimeMs) {
		content.setBounds(0, 0, w, h);
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
