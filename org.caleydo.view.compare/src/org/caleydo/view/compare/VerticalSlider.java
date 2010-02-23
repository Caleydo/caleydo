package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class VerticalSlider {

	HeatMapLayout layout;
	float positionX;
	float positionY;
	float bodyHeight;

	public VerticalSlider(HeatMapLayout layout) {
		this.layout = layout;
		bodyHeight = (layout.getOverviewMaxSliderHeight() / 5.0f) * 0.5f;
	}

	public void draw(final GL gl, PickingManager pickingManager,
			TextureManager textureManager, int viewID, int pickingID) {

		bodyHeight = (layout.getOverviewMaxSliderHeight() / 5.0f) * 0.5f;
		float sliderWidth = layout.getOverviewSliderWidth();
		float arrowHeight = sliderWidth;
		float sliderPositionX = layout.getOverviewSliderPositionX();
		float sliderPositionY = 0;

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION,
				pickingID));
		Vec3f upperLeftCorner = new Vec3f(sliderPositionX, sliderPositionY,
				0.0f);
		Vec3f upperRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderPositionY, 0.0f);
		Vec3f lowerRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderPositionY + arrowHeight, 0.0f);
		Vec3f lowerLeftCorner = new Vec3f(sliderPositionX, sliderPositionY
				+ arrowHeight, 0.0f);

		textureManager.renderTexture(gl, EIconTextures.HEAT_MAP_ARROW,
				lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		// fill gap between cursor
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		gl
				.glPushName(pickingManager.getPickingID(viewID,
						EPickingType.COMPARE_OVERVIEW_SLIDER_BODY_SELECTION,
						pickingID));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(lowerLeftCorner.x(), lowerLeftCorner.y(), 0.0f);
		gl.glVertex3f(lowerRightCorner.x(), lowerRightCorner.y(), 0.0f);
		gl.glVertex3f(lowerRightCorner.x(), lowerRightCorner.y() + bodyHeight,
				0.0f);
		gl.glVertex3f(lowerLeftCorner.x(), lowerLeftCorner.y() + bodyHeight,
				0.0f);
		gl.glEnd();
		gl.glPopName();

		// Polygon for iLastElement-Cursor
		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION,
				pickingID));

		lowerLeftCorner = new Vec3f(sliderPositionX, sliderPositionY
				+ arrowHeight + bodyHeight, 0.0f);
		lowerRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderPositionY + arrowHeight + bodyHeight, 0.0f);
		upperRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderPositionY + 2.0f * arrowHeight + bodyHeight, 0.0f);
		upperLeftCorner = new Vec3f(sliderPositionX, sliderPositionY + 2.0f
				* arrowHeight + bodyHeight, 0.0f);

		textureManager.renderTexture(gl, EIconTextures.HEAT_MAP_ARROW,
				lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);
		gl.glPopName();

	}
}
