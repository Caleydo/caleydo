/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
/**
 *
 */
package org.caleydo.view.stratomex.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.BrickColors;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;

/**
 * Background for dynamic toolbars
 *
 * @author Alexander Lex
 *
 */
public class ToolBarBackgroundRenderer extends LayoutRenderer {

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

		gl.glColor3fv(BrickColors.BRICK_COLOR, 0);
		gl.glBegin(GL2.GL_QUADS);

		gl.glVertex3f(0, -spacing, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(x, -spacing, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(x, height, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(0, height, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);

		gl.glEnd();

		gl.glPopName();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
