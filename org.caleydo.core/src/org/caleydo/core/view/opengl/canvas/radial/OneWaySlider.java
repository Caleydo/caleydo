package org.caleydo.core.view.opengl.canvas.radial;

import java.awt.Point;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class OneWaySlider {

	private static final float SLIDING_ELEMENT_MAX_HEIGHT = 0.2f;
	private static final float DOWN_BUTTON_MAX_HEIGHT = 0.2f;

	private Vec2f vecPosition;
	private float fHeight;
	private float fWidth;
	private int iSelectedValue;
	private int iValueStep;
	private int iMaxValue;
	private int iMinValue;
	private float fDrawingStep;
	private float fSlidingElementDrawingPosition;
	private float fSlidingElementHeight;
	private float fDownButtonHeight;
	private boolean bIsDragging;

	public OneWaySlider(Vec2f vecPosition, float fWidth, float fHeight, int iSelectedValue, int iValueStep,
		int iMinValue, int iMaxValue) {

		this.vecPosition = vecPosition;
		this.fWidth = fWidth;
		this.fHeight = fHeight;
		this.iSelectedValue = iSelectedValue;
		this.iValueStep = iValueStep;
		this.iMinValue = iMinValue;
		this.iMaxValue = iMaxValue;
		bIsDragging = false;
		fSlidingElementHeight = Math.min(fHeight * 0.2f, SLIDING_ELEMENT_MAX_HEIGHT);
		fDownButtonHeight = Math.min(fHeight * 0.2f, DOWN_BUTTON_MAX_HEIGHT);
		fDrawingStep =
			(float) iValueStep * ((fHeight - fSlidingElementHeight - fDownButtonHeight) / (float) (iMaxValue - iMinValue - 1));
		fSlidingElementDrawingPosition = vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}

	public void draw(GL gl, PickingManager pickingManager, TextureManager textureManager, int iViewID, int iSliderID, int iSliderButtonID) {

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_SLIDER_SELECTION,
			iSliderID));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fDownButtonHeight, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fDownButtonHeight, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fHeight, 0);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fHeight, 0);
		gl.glEnd();

		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), fSlidingElementDrawingPosition, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, fSlidingElementDrawingPosition, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, fSlidingElementDrawingPosition + fSlidingElementHeight, 0);
		gl.glVertex3f(vecPosition.x(), fSlidingElementDrawingPosition + fSlidingElementHeight, 0);
		gl.glEnd();
		
		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_SLIDER_BUTTON_SELECTION,
			iSliderButtonID));
		
		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glBegin(GL.GL_POLYGON);		
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fDownButtonHeight, 0);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fDownButtonHeight, 0);
		
		gl.glEnd();
		
		gl.glPopName();
		
		tempTexture.disable();

		gl.glPopAttrib();
		
	}

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (!bIsDragging) {
			return false;
		}

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fYCoordinate = fArTargetWorldCoordinates[1];

		if (fYCoordinate < vecPosition.y() + fDownButtonHeight) {
			iSelectedValue = iMinValue;
			fSlidingElementDrawingPosition = vecPosition.y() + fDownButtonHeight;
		}
		else if (!(fYCoordinate > fSlidingElementDrawingPosition)) {
			iSelectedValue = (int) ((fYCoordinate - vecPosition.y() - fDownButtonHeight) / fDrawingStep);
			fSlidingElementDrawingPosition = vecPosition.y() + fDownButtonHeight + (float)(iSelectedValue * fDrawingStep);
		}

		if (glMouseListener.wasMouseReleased()) {
			bIsDragging = false;
		}

		return true;
	}
	
	public void handleButtonClick() {
		iSelectedValue -= iValueStep;
		if(iSelectedValue <= iMinValue) {
			iSelectedValue = iMinValue;
		}
		
		fSlidingElementDrawingPosition = vecPosition.y() + fDownButtonHeight + (float)(iSelectedValue * fDrawingStep);
	}

	public Vec2f getPosition() {
		return vecPosition;
	}

	public void setPosition(Vec2f vecPosition) {
		this.vecPosition = vecPosition;
	}

	public float getHeight() {
		return fHeight;
	}

	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	public float getWidth() {
		return fWidth;
	}

	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}

	public int getSelectedValue() {
		return iSelectedValue;
	}

	public void setSelectedValue(int iSelectedValue) {
		this.iSelectedValue = iSelectedValue;
		fSlidingElementDrawingPosition = vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}

	public int getValueStep() {
		return iValueStep;
	}

	public void setValueStep(int iValueStep) {
		this.iValueStep = iValueStep;
	}

	public int getMaxValue() {
		return iMaxValue;
	}

	public void setMaxValue(int iMaxValue) {
		this.iMaxValue = iMaxValue;
	}

	public int getMinValue() {
		return iMinValue;
	}

	public void setMinValue(int iMinValue) {
		this.iMinValue = iMinValue;
	}

	public boolean isDragging() {
		return bIsDragging;
	}

	public void setDragging(boolean bIsDragging) {
		this.bIsDragging = bIsDragging;
	}

}
