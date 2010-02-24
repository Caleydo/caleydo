package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.awt.Point;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class VerticalSlider {

	private HeatMapLayout layout;
	private float relativePositionY;
	private float relativeSliderHeight;
	private float sliderPositionY;
	private float sliderHeight;
	private float arrowHeight;
	private float bodyHeight;
	private boolean isBodyDragging;
	private boolean isArrowUpDragging;
	private boolean isArrowDownDragging;
	private float draggingSpacing;
	private boolean isDraggingFirstTime = true;

	public VerticalSlider(HeatMapLayout layout) {
		relativeSliderHeight = 0.25f;
		relativePositionY = 0.75f;
		this.layout = layout;
	}

	public void draw(final GL gl, PickingManager pickingManager,
			TextureManager textureManager, int viewID, int pickingID) {

		sliderHeight = relativeSliderHeight * layout.getOverviewHeight();
		sliderPositionY = layout.getOverviewMinSliderPositionY()
				+ relativePositionY * layout.getOverviewHeight();

		float sliderWidth = layout.getOverviewSliderWidth();
		arrowHeight = (2.0f * sliderWidth <= sliderHeight) ? sliderWidth
				: sliderHeight / 2.0f;
		bodyHeight = sliderHeight - 2.0f * arrowHeight;
		float sliderPositionX = layout.getOverviewSliderPositionX();

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

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (!isArrowDownDragging && !isArrowUpDragging && !isBodyDragging)
			return false;

		Point pickedPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x,
						pickedPoint.y);

		float pickedYCoordinate = fArTargetWorldCoordinates[1];

		if (isDraggingFirstTime) {
			isDraggingFirstTime = false;
			if (isArrowUpDragging) {
				draggingSpacing = pickedYCoordinate - (sliderPositionY
						+ sliderHeight);
			} else {
				draggingSpacing = pickedYCoordinate - sliderPositionY;
			}
		}

		if (isBodyDragging) {
			handleBodyDragging(pickedYCoordinate);
		} else if (isArrowDownDragging) {
			handleArrowDownDragging(pickedYCoordinate);
		} else {
			handleArrowUpDragging(pickedYCoordinate);
		}

		if (glMouseListener.wasMouseReleased()) {
			isBodyDragging = false;
			isArrowUpDragging = false;
			isArrowDownDragging = false;
		}

		return true;
	}

	private void handleBodyDragging(float pickedYCoordinate) {
		float newSliderPositionY = pickedYCoordinate - draggingSpacing;
		setSliderPositionY(newSliderPositionY);
		setRelativeYPosition(getRelativeYCoordinate(sliderPositionY));
	}

	private void handleArrowUpDragging(float pickedYCoordinate) {

		float newTopPositionY = pickedYCoordinate - draggingSpacing;
		setSliderHeight(newTopPositionY - sliderPositionY);
		setRelativeSliderHeight(getRelativeSize(sliderHeight));
	}

	private void handleArrowDownDragging(float pickedYCoordinate) {
		float newSliderPositionY = pickedYCoordinate - draggingSpacing;
		float sliderTopPositionY = sliderPositionY + sliderHeight;
		if(newSliderPositionY > sliderTopPositionY - 2.0f * arrowHeight)
			newSliderPositionY = sliderTopPositionY - 2.0f * arrowHeight;
		setSliderPositionY(newSliderPositionY);
		setSliderHeight(sliderTopPositionY - sliderPositionY);
		relativeSliderHeight = getRelativeSize(sliderHeight);
		setRelativeYPosition(getRelativeYCoordinate(sliderPositionY));
	}

	private void setSliderPositionY(float sliderPositionY) {
		if (sliderPositionY + sliderHeight > layout.getOverviewMaxSliderPositionY()) {
			this.sliderPositionY = layout.getOverviewMaxSliderPositionY() - sliderHeight;
		} else if (sliderPositionY < layout.getOverviewMinSliderPositionY()) {
			this.sliderPositionY = layout.getOverviewMinSliderPositionY();
		} else {
			this.sliderPositionY = sliderPositionY;
		}
	}
	
	private void setSliderHeight(float sliderHeight) {
		if(sliderHeight + sliderPositionY > layout.getOverviewMaxSliderPositionY()) {
			this.sliderHeight = layout.getOverviewMaxSliderPositionY() - sliderPositionY;
		} else if(sliderHeight < 2.0f * arrowHeight) {
			this.sliderHeight = 2.0f * arrowHeight;
		} else {
			this.sliderHeight = sliderHeight;
		}
	}

	private float getRelativeSize(float absoluteSize) {
		return absoluteSize / layout.getOverviewHeight();
	}

	private float getRelativeYCoordinate(float absoluteYCoordinate) {
		return (absoluteYCoordinate - layout.getOverviewMinSliderPositionY())
				/ layout.getOverviewHeight();
	}

	private void setRelativeSliderHeight(float relativeSliderHeight) {
		if (relativePositionY + relativeSliderHeight > 1.0f) {
			this.relativeSliderHeight = 1.0f - relativePositionY;
		} else if (relativeSliderHeight < 2.0f * getRelativeSize(arrowHeight)) {
			this.relativeSliderHeight = 2.0f * getRelativeSize(arrowHeight);
		} else {
			this.relativeSliderHeight = relativeSliderHeight;
		}
	}

	private void setRelativeYPosition(float relativePositionY) {
		if (relativePositionY + relativeSliderHeight > 1.0f) {
			this.relativePositionY = 1.0f - relativeSliderHeight;
		} else if (relativePositionY < 0.0f) {
			this.relativePositionY = 0.0f;
		} else {
			this.relativePositionY = relativePositionY;
		}
	}

	public void handleSliderSelection(EPickingType pickingType,
			EPickingMode pickingMode) {

		if (pickingMode != EPickingMode.CLICKED)
			return;

		switch (pickingType) {
		case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION:
			isBodyDragging = true;
			break;
		case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION:
			isArrowUpDragging = true;
			break;
		case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION:
			isArrowDownDragging = true;
			break;
		}

		isDraggingFirstTime = true;
	}
	
	public float getSliderHeight() {
		return sliderHeight;
	}
	
	public float getSliderPositionY() {
		return sliderPositionY;
	}
}
