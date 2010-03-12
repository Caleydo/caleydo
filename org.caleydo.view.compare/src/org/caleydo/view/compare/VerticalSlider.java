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

	private AHeatMapLayout layout;
	private float relativeBottomPositionY;
	private float relativeTopPositionY;
//	private float relativeSliderHeight;
	private float sliderBottomPositionY;
	private float sliderTopPositionY;
//	private float sliderHeight;
	private float arrowHeight;
	private boolean isBodyDragging;
	private boolean isArrowUpDragging;
	private boolean isArrowDownDragging;
	private float draggingSpacing;
	private boolean isDraggingFirstTime = true;

	public VerticalSlider(AHeatMapLayout layout) {
//		relativeSliderHeight = 0.25f;
		relativeTopPositionY = 1.0f;
		relativeBottomPositionY = 0.75f;
		this.layout = layout;
	}

	public void draw(final GL gl, PickingManager pickingManager,
			TextureManager textureManager, int viewID, int pickingID) {

		// sliderHeight = relativeSliderHeight * layout.getOverviewHeight();
		sliderBottomPositionY = layout.getOverviewMinSliderPositionY()
				+ relativeBottomPositionY * layout.getOverviewHeight();
		sliderTopPositionY = layout.getOverviewMinSliderPositionY()
				+ relativeTopPositionY * layout.getOverviewHeight();
		float sliderHeight = sliderTopPositionY - sliderBottomPositionY;

		float sliderWidth = layout.getOverviewSliderWidth();
		arrowHeight = (2.0f * sliderWidth <= sliderHeight) ? sliderWidth
				: sliderHeight / 2.0f;
		float bodyHeight = sliderHeight - 2.0f * arrowHeight;
		float sliderPositionX = layout.getOverviewSliderPositionX();

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION,
				pickingID));
		Vec3f upperLeftCorner = new Vec3f(sliderPositionX,
				sliderBottomPositionY, 0.0f);
		Vec3f upperRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderBottomPositionY, 0.0f);
		Vec3f lowerRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderBottomPositionY + arrowHeight, 0.0f);
		Vec3f lowerLeftCorner = new Vec3f(sliderPositionX,
				sliderBottomPositionY + arrowHeight, 0.0f);

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

		lowerLeftCorner = new Vec3f(sliderPositionX, sliderTopPositionY
				- arrowHeight, 0.0f);
		lowerRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderTopPositionY - arrowHeight, 0.0f);
		upperRightCorner = new Vec3f(sliderPositionX + sliderWidth,
				sliderTopPositionY, 0.0f);
		upperLeftCorner = new Vec3f(sliderPositionX, sliderTopPositionY, 0.0f);

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
				draggingSpacing = pickedYCoordinate - sliderTopPositionY;
			} else {
				draggingSpacing = pickedYCoordinate - sliderBottomPositionY;
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
		float newSliderBottomPositionY = pickedYCoordinate - draggingSpacing;
		float sliderHeight = sliderTopPositionY - sliderBottomPositionY;
		float newSliderTopPositionY = newSliderBottomPositionY + sliderHeight;
		if (newSliderBottomPositionY + sliderHeight > layout
				.getOverviewMaxSliderPositionY()) {
			newSliderBottomPositionY = layout.getOverviewMaxSliderPositionY()
					- sliderHeight;
			newSliderTopPositionY = layout.getOverviewMaxSliderPositionY();
		} else if(newSliderBottomPositionY < layout.getOverviewMinSliderPositionY()) {
			newSliderBottomPositionY = layout.getOverviewMinSliderPositionY();
			newSliderTopPositionY = layout.getOverviewMinSliderPositionY() + sliderHeight;
		}
		setSliderBottomPositionY(newSliderBottomPositionY);
		relativeBottomPositionY = getRelativeYCoordinate(sliderBottomPositionY);
		setSliderTopPositionY(newSliderTopPositionY);
		relativeTopPositionY = getRelativeYCoordinate(sliderTopPositionY);
	}

	private void handleArrowUpDragging(float pickedYCoordinate) {

		float newSliderTopPositionY = pickedYCoordinate - draggingSpacing;
		setSliderTopPositionY(newSliderTopPositionY);
		relativeTopPositionY = getRelativeYCoordinate(sliderTopPositionY);

		// float newTopPositionY = pickedYCoordinate - draggingSpacing;
		// setSliderHeight(newTopPositionY - sliderBottomPositionY);
		// setRelativeSliderHeight(getRelativeSize(sliderHeight));
	}

	private void handleArrowDownDragging(float pickedYCoordinate) {

		float newSliderBottomPositionY = pickedYCoordinate - draggingSpacing;
		setSliderBottomPositionY(newSliderBottomPositionY);
		relativeBottomPositionY = getRelativeYCoordinate(sliderBottomPositionY);

		// float newSliderPositionY = pickedYCoordinate - draggingSpacing;
		// if (newSliderPositionY > sliderTopPositionY - 2.0f * arrowHeight)
		// newSliderPositionY = sliderTopPositionY - 2.0f * arrowHeight;
		// setSliderBottomPositionY(newSliderPositionY);
		// setSliderHeight(sliderTopPositionY - sliderBottomPositionY);
		// relativeSliderHeight = getRelativeSize(sliderHeight);
		// setRelativeBottomPositionY(getRelativeYCoordinate(sliderBottomPositionY));
	}

	private void setSliderBottomPositionY(float sliderPositionY) {
		if (sliderPositionY + (2.0f * arrowHeight) > sliderTopPositionY) {
			this.sliderBottomPositionY = sliderTopPositionY
					- (2.0f * arrowHeight);
		} else if (sliderPositionY < layout.getOverviewMinSliderPositionY()) {
			this.sliderBottomPositionY = layout.getOverviewMinSliderPositionY();
		} else {
			this.sliderBottomPositionY = sliderPositionY;
		}
	}

	private void setSliderTopPositionY(float sliderPositionY) {
		if (sliderPositionY > layout.getOverviewMaxSliderPositionY()) {
			this.sliderTopPositionY = layout.getOverviewMaxSliderPositionY();
		} else if (sliderPositionY - (2.0f * arrowHeight) < sliderBottomPositionY) {
			this.sliderTopPositionY = sliderBottomPositionY
					+ (2.0f * arrowHeight);
		} else {
			this.sliderTopPositionY = sliderPositionY;
		}
	}

//	private void setSliderHeight(float sliderHeight) {
//		if (sliderHeight + sliderBottomPositionY > layout
//				.getOverviewMaxSliderPositionY()) {
//			this.sliderHeight = layout.getOverviewMaxSliderPositionY()
//					- sliderBottomPositionY;
//		} else if (sliderHeight < 2.0f * arrowHeight) {
//			this.sliderHeight = 2.0f * arrowHeight;
//		} else {
//			this.sliderHeight = sliderHeight;
//		}
//	}

//	private float getRelativeSize(float absoluteSize) {
//		return absoluteSize / layout.getOverviewHeight();
//	}

	private float getRelativeYCoordinate(float absoluteYCoordinate) {
		return (absoluteYCoordinate - layout.getOverviewMinSliderPositionY())
				/ layout.getOverviewHeight();
	}

//	private void setRelativeSliderHeight(float relativeSliderHeight) {
//		if (relativeBottomPositionY + relativeSliderHeight > 1.0f) {
//			this.relativeSliderHeight = 1.0f - relativeBottomPositionY;
//		} else if (relativeSliderHeight < 2.0f * getRelativeSize(arrowHeight)) {
//			this.relativeSliderHeight = 2.0f * getRelativeSize(arrowHeight);
//		} else {
//			this.relativeSliderHeight = relativeSliderHeight;
//		}
//	}

//	private void setRelativeBottomPositionY(float relativePositionY) {
//		if (relativePositionY + relativeSliderHeight > 1.0f) {
//			this.relativeBottomPositionY = 1.0f - relativeSliderHeight;
//		} else if (relativePositionY < 0.0f) {
//			this.relativeBottomPositionY = 0.0f;
//		} else {
//			this.relativeBottomPositionY = relativePositionY;
//		}
//	}

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

//	public float getSliderHeight() {
//		return sliderHeight;
//	}

	public float getSliderBottomPositionY() {
		return sliderBottomPositionY;
	}
	
	public float getSliderTopPositionY() {
		return sliderTopPositionY;
	}
	
	public float getSliderHeight() {
		return sliderTopPositionY - sliderBottomPositionY;
	}
}
