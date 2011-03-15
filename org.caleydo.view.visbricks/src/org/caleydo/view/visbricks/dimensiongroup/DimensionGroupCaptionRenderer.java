package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class DimensionGroupCaptionRenderer extends LayoutRenderer {

	private DimensionGroup dimensionGroup;

	public DimensionGroupCaptionRenderer(DimensionGroup dimensionGroup) {
		this.dimensionGroup = dimensionGroup;
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

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer
				.renderText(gl, dimensionGroup.getSet().getLabel(), 0, y/8.0f, 0,
						18, dimensionGroup.getParentGLCanvas()
								.getPixelGLConverter());

	}
}
