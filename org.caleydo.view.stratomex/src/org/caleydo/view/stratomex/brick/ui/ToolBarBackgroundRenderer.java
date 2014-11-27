/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.stratomex.brick.ui;


import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;

/**
 * Background for dynamic toolbars
 *
 * @author Alexander Lex
 *
 */
public class ToolBarBackgroundRenderer extends ALayoutRenderer {

	private GLBrick brick;

	public ToolBarBackgroundRenderer(GLBrick brick) {
		this.brick = brick;
	}

	@Override
	public void renderContent(GL2 gl) {

		float height = brick.getPixelGLConverter().getGLHeightForPixelHeight(
				DefaultBrickLayoutTemplate.BUTTON_HEIGHT_PIXELS + 2);
		float spacing = brick.getPixelGLConverter().getGLHeightForPixelHeight(2);

		gl.glPushName(brick.getStratomex().getPickingManager()
				.getPickingID(brick.getStratomex().getID(), EPickingType.BRICK.name(), brick.getID()));
		gl.glPushName(brick.getStratomex().getPickingManager()
				.getPickingID(brick.getStratomex().getID(), EPickingType.BRICK_PENETRATING.name(), brick.getID()));

		gl.glColor3fv(Color.GRAY.getRGBA(), 0);
		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glVertex3f(0, -spacing, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(x, -spacing, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(x, height, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(0, height, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);

		gl.glEnd();

		gl.glPopName();
		gl.glPopName();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
