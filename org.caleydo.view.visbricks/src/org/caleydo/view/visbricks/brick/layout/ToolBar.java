/**
 * 
 */
package org.caleydo.view.visbricks.brick.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.ToolBarBackgroundRenderer;

/**
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
	    public void clicked(Pick pick) {
		if (pick.getID() == brick.getID())
		    hide = false;
		else
		    hide = true;
	    }
	};

	brick.getDimensionGroup()
		.getVisBricksView()
		.addTypePickingListener(brickPickingListener,
			PickingType.BRICK.name());

    }

    @Override
    public void render(GL2 gl) {
	if (!hide) {
	    float offset = layoutManager
		    .getPixelGLConverter()
		    .getGLHeightForPixelHeight(
			    DefaultBrickLayoutTemplate.BUTTON_HEIGHT_PIXELS + 2);
	    gl.glTranslatef(0, -offset, 0);
	    super.render(gl);
	    gl.glTranslatef(0, offset, 0);

	}
    }

    @Override
    public void destroy() {
	brick.getDimensionGroup()
		.getVisBricksView()
		.removeTypePickingListener(brickPickingListener,
			PickingType.BRICK.name());
	super.destroy();
    }
}
