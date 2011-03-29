package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Renders a bordered area.
 * 
 * @author Christian Partl
 * 
 */
public class BorderedAreaRenderer extends LayoutRenderer {

	private GLBrick brick;

	public BorderedAreaRenderer(GLBrick brick) {
		this.brick = brick;
	}

	@Override
	public void render(GL2 gl) {

		SelectionManager selectionManager = brick
				.getContentGroupSelectionManager();

		gl.glColor3f(0.35f, 0.35f, 0.35f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(x, 0, 0);
		gl.glColor3f(0.65f, 0.65f, 0.65f);
		gl.glVertex3f(x, y, 0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		
		if (selectionManager.checkStatus(SelectionType.SELECTION, brick
				.getGroup().getID())) {
			float[] baseColor = SelectionType.SELECTION.getColor();

			gl.glColor3f(baseColor[0] + 0.3f, baseColor[1] + 0.3f,
					baseColor[2] + 0.3f);
			
			gl.glColor4f(baseColor[0] - 0.15f, baseColor[1]- 0.15f, baseColor[2]- 0.15f, 0.4f);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0);
			gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], 0.4f);
			gl.glVertex3f(x, 0, 0);
			gl.glColor4f(baseColor[0] + 0.15f, baseColor[1]+ 0.15f, baseColor[2]+ 0.15f, 0.4f);
			gl.glVertex3f(x, y, 0);
			gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], 0.4f);
			gl.glVertex3f(0, y, 0);
			gl.glEnd();
		}

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(2);

		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, y, 0);

		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);

		gl.glEnd();

		gl.glPopAttrib();
	}

}
