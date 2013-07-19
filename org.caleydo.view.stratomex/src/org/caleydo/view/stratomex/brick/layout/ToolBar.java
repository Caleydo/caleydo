/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
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
		addBackgroundRenderer(new ToolBarBackgroundRenderer(brick));
		//
		// brickPickingListener = new ATimedMouseOutPickingListener() {
		//
		// @Override
		// public void mouseOver(Pick pick) {
		// super.mouseOver(pick);
		// if (pick.getObjectID() == brick.getID())
		// hide = false;
		// else
		// hide = true;
		// }
		//
		// @Override
		// protected void timedMouseOut(Pick pick) {
		// // TODO Auto-generated method stub
		// if (pick.getObjectID() == brick.getID())
		// hide = true;
		// }
		// };
		//
		// brick.getBrickColumn().getStratomexView()
		// .addTypePickingListener(brickPickingListener, EPickingType.BRICK_PENETRATING.name());

	}

	@Override
	public void render(GL2 gl) {
		if (!hide) {
			float offset = layoutManager.getPixelGLConverter().getGLHeightForPixelHeight(
					DefaultBrickLayoutTemplate.BUTTON_HEIGHT_PIXELS + 2);
			gl.glTranslatef(0, -offset, 0);
			gl.glPushName(brick.getStratomex().getPickingManager()
					.getPickingID(brick.getStratomex().getID(), EPickingType.BRICK_PENETRATING.name(), brick.getID()));
			super.render(gl);
			gl.glPopName();
			gl.glTranslatef(0, offset, 0);

		}
	}

	@Override
	public void destroy(GL2 gl) {
		// brick.getBrickColumn().getStratomexView()
		// .removeTypePickingListener(brickPickingListener, EPickingType.BRICK_PENETRATING.name());
		remove(brick.getViewSwitchingBar());
		super.destroy(gl);
	}

	/**
	 * @param hide
	 *            setter, see {@link hide}
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}
}
