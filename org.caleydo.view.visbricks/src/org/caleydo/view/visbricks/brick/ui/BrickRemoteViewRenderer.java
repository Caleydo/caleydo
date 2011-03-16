package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ViewLayoutRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * View renderer specifically for brick remote views.
 * 
 * @author Christian Partl
 * 
 */
public class BrickRemoteViewRenderer extends ViewLayoutRenderer {

	private GLBrick brick;

	public BrickRemoteViewRenderer(AGLView view, GLBrick brick) {
		super(view);
		this.brick = brick;
	}

	/**
	 * Calls the displayRemote of the view to be rendered plus pushes the ID of
	 * the Brick.
	 */
	@Override
	public void render(GL2 gl) {
		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				EPickingType.BRICK, brick.getID()));
		view.displayRemote(gl);
		gl.glPopName();
	}

}
