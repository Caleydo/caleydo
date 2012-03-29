/**
 * 
 */
package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.visbricks.brick.layout.BrickColors;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;

/**
 * Background for dynamic toolbars
 * 
 * @author Alexander Lex
 * 
 */
public class ToolBarBackgroundRenderer extends LayoutRenderer {

	@Override
	public void render(GL2 gl) {

		float height = getPixelGLConverter().getGLHeightForPixelHeight(
				DefaultBrickLayoutTemplate.BUTTON_HEIGHT_PIXELS + 2);
		float spacing = getPixelGLConverter().getGLHeightForPixelHeight(2);

		gl.glColor3fv(BrickColors.BRICK_COLOR, 0);
		gl.glBegin(GL2.GL_QUADS);

		gl.glVertex3f(0, -spacing, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(x, -spacing, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(x, height, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);
		gl.glVertex3f(0, height, DefaultBrickLayoutTemplate.BUTTON_Z - 0.02f);

		gl.glEnd();

	}

}
