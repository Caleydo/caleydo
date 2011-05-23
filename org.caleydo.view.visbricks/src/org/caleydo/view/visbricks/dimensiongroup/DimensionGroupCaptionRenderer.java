package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class DimensionGroupCaptionRenderer extends LayoutRenderer {

	private DimensionGroup dimensionGroup;
	private String caption;

	public DimensionGroupCaptionRenderer(DimensionGroup dimensionGroup,
			String caption) {
		this.dimensionGroup = dimensionGroup;
		this.caption = caption;
	}

	@Override
	public void render(GL2 gl) {

		int pickingID = dimensionGroup.getPickingManager().getPickingID(
				dimensionGroup.getVisBricksView().getID(),
				EPickingType.DIMENSION_GROUP, dimensionGroup.getID());

		gl.glPushName(pickingID);
		gl.glColor4f(1, 1, 1, 0);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(x, 0);
		gl.glVertex2f(x, y);
		gl.glVertex2f(0, y);
		gl.glEnd();
		gl.glPopName();

		CaleydoTextRenderer textRenderer = dimensionGroup.getTextRenderer();

		float ySpacing = dimensionGroup.getParentGLCanvas()
				.getPixelGLConverter().getGLHeightForPixelHeight(1);

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, caption, 0, ySpacing, 0, x, y - 2
				* ySpacing);

	}
}
