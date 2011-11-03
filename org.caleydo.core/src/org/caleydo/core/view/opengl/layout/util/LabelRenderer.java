package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Renders a text within the given bounds of the ElementLayout. The text is trunkated if necessary.
 * 
 * @author Partl
 */
public class LabelRenderer
	extends LayoutRenderer {

	private AGLView view;
	private String text;
	private PickingType pickingType;
	private int id;
	private boolean isPickable;

	/**
	 * @param view
	 *            Rendering view.
	 * @param text
	 *            Text to render.
	 * @param pickingType
	 *            PickingType for the text.
	 * @param id
	 *            ID for picking.
	 */
	public LabelRenderer(AGLView view, String text, PickingType pickingType, int id) {
		this.view = view;
		this.text = text;
		this.pickingType = pickingType;
		this.id = id;
		this.isPickable = true;
	}

	
	public LabelRenderer(AGLView view, String text) {
		this.view = view;
		this.text = text;
		this.isPickable = false;
	}
	
	@Override
	public void render(GL2 gl) {

		if (isPickable) {
			int pickingID = view.getPickingManager().getPickingID(view.getID(), pickingType, id);

			gl.glPushName(pickingID);
			gl.glColor4f(1, 1, 1, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(x, 0, 0.1f);
			gl.glVertex3f(x, y, 0.1f);
			gl.glVertex3f(0, y, 0.1f);
			gl.glEnd();
			gl.glPopName();
		}

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float ySpacing = view.getPixelGLConverter().getGLHeightForPixelHeight(1);

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, text, 0, ySpacing, 0.1f, x, y - 2 * ySpacing);

	}
}
