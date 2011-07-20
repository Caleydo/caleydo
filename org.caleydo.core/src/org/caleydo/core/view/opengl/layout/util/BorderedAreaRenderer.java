package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Renders a bordered area.
 * 
 * @author Christian Partl
 */
public class BorderedAreaRenderer
	extends LayoutRenderer {

	public final static float[] DEFAULT_COLOR = { 0.5f, 0.5f, 0.5f, 1.0f };

	private float[] color;
	private PickingType pickingType;
	private int id;
	private AGLView view;
	private boolean isPickable;

	public BorderedAreaRenderer() {
		color = DEFAULT_COLOR;
		isPickable = false;
	}

	public BorderedAreaRenderer(AGLView view, PickingType pickingType, int id) {
		color = DEFAULT_COLOR;
		this.pickingType = pickingType;
		this.id = id;
		this.view = view;
		isPickable = true;
	}

	@Override
	public void render(GL2 gl) {

		if (isPickable) {
			int pickingID = view.getPickingManager().getPickingID(view.getID(), pickingType, id);

			gl.glPushName(pickingID);
		}
		gl.glColor4f(color[0] - 0.15f, color[1] - 0.15f, color[2] - 0.15f, color[3]);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		gl.glVertex3f(x, 0, 0);
		gl.glColor4f(color[0] + 0.15f, color[1] + 0.15f, color[2] + 0.15f, color[3]);
		gl.glVertex3f(x, y, 0);
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		// SelectionManager selectionManager = brick
		// .getContentGroupSelectionManager();
		//
		// gl.glColor3f(0.35f, 0.35f, 0.35f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0);
		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		// gl.glVertex3f(x, 0, 0);
		// gl.glColor3f(0.65f, 0.65f, 0.65f);
		// gl.glVertex3f(x, y, 0);
		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		// gl.glVertex3f(0, y, 0);
		// gl.glEnd();
		//
		// if (selectionManager.checkStatus(SelectionType.SELECTION, brick
		// .getGroup().getID())) {
		// float[] baseColor = SelectionType.SELECTION.getColor();
		//
		// gl.glColor3f(baseColor[0] + 0.3f, baseColor[1] + 0.3f,
		// baseColor[2] + 0.3f);
		//
		// gl.glColor4f(baseColor[0] - 0.15f, baseColor[1]- 0.15f, baseColor[2]-
		// 0.15f, 0.4f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0);
		// gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], 0.4f);
		// gl.glVertex3f(x, 0, 0);
		// gl.glColor4f(baseColor[0] + 0.15f, baseColor[1]+ 0.15f, baseColor[2]+
		// 0.15f, 0.4f);
		// gl.glVertex3f(x, y, 0);
		// gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], 0.4f);
		// gl.glVertex3f(0, y, 0);
		// gl.glEnd();
		// }

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
		if (isPickable) {
			gl.glPopName();
		}
	}

	/**
	 * @param color
	 *            Color with rgba values.
	 */
	public void setColor(float[] color) {
		this.color = color;
	}

	public float[] getColor() {
		return color;
	}

}
