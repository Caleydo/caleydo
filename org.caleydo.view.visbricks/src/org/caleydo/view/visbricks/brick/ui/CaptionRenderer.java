package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class CaptionRenderer extends LayoutRenderer {

	private AGLView view;
	private String caption;
	private EPickingType pickingType;
	private int id;

	public CaptionRenderer(AGLView view, String caption,
			EPickingType pickingType, int id) {
		this.view = view;
		this.caption = caption;
		this.pickingType = pickingType;
		this.id = id;
	}

	@Override
	public void render(GL2 gl) {

		int pickingID = view.getPickingManager().getPickingID(view.getID(),
				pickingType, id);

		gl.glPushName(pickingID);
		gl.glColor4f(1, 1, 1, 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(x, 0);
		gl.glVertex2f(x, y);
		gl.glVertex2f(0, y);
		gl.glEnd();
		gl.glPopName();

		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float ySpacing = view.getParentGLCanvas().getPixelGLConverter()
				.getGLHeightForPixelHeight(1);

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, caption, 0, ySpacing, 0, x, y - 2
				* ySpacing);

	}
}
