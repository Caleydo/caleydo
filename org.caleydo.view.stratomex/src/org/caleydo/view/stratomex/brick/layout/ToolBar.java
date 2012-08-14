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
package org.caleydo.view.stratomex.brick.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.ui.ToolBarBackgroundRenderer;

/**
 * Dynamically appearing tool-bar for bricks
 * 
 * @author Alexander Lex
 * 
 */
public class ToolBar extends Row {

	/** The brick for which this toolbar is rendered */
	private GLBrick brick;
	/** Flag indicating whether the toolbar should be hidden or is visible */
	private boolean hide = true;
	private APickingListener brickPickingListener;

	/**
	 * 
	 */
	public ToolBar(String layoutName, final GLBrick brick) {
		super(layoutName);
		this.brick = brick;
		addBackgroundRenderer(new ToolBarBackgroundRenderer());

		brickPickingListener = new APickingListener() {
			@Override
			public void mouseOver(Pick pick) {
				if (pick.getObjectID() == brick.getID())
					hide = false;
				else
					hide = true;
			}

			@Override
			public void mouseOut(Pick pick) {
				if (pick.getObjectID() == brick.getID())
					hide = true;
			}
		};

		brick.getBrickColumn().getStratomexView()
				.addTypePickingListener(brickPickingListener, EPickingType.BRICK.name());

	}

	@Override
	public void render(GL2 gl) {
		if (!hide) {
			float offset = layoutManager.getPixelGLConverter().getGLHeightForPixelHeight(
					DefaultBrickLayoutTemplate.BUTTON_HEIGHT_PIXELS + 2);
			gl.glTranslatef(0, -offset, 0);
			super.render(gl);
			gl.glTranslatef(0, offset, 0);

		}
	}

	@Override
	public void destroy() {
		brick.getBrickColumn()
				.getStratomexView()
				.removeTypePickingListener(brickPickingListener, EPickingType.BRICK.name());
		super.destroy();
	}
}
