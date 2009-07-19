package org.caleydo.core.view.opengl.canvas.radial;

import gleem.linalg.Vec2f;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Represents a slider that can be dragged in one direction only. Moving the slider into the other direction
 * can only be performed indirectly.
 * 
 * @author Christian Partl
 */
public class OneWaySlider {

	private static final float SLIDING_ELEMENT_MAX_HEIGHT = 0.2f;
	private static final float DOWN_BUTTON_MAX_HEIGHT = 0.2f;
	private static final int SLIDER_FONT_SIZE = 32;
	private static final String SLIDER_FONT_NAME = "Arial";
	private static final int SLIDER_FONT_STYLE = Font.PLAIN;

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
	private boolean bIsDraggingFirstTime;
	private boolean bIsBodySelected;
	private float fDraggingBottomSpacing;

	private TextRenderer textRenderer;

	/**
	 * Constructor.
	 * 
	 * @param vecPosition
	 *            Position of the slider.
	 * @param fWidth
	 *            Width of the slider.
	 * @param fHeight
	 *            Height of the slider.
	 * @param iSelectedValue
	 *            The value that should currently be selected by the slider.
	 * @param iValueStep
	 *            Number the selected value should be increased or decreased in one step.
	 * @param iMinValue
	 *            Minimum value the selected value can be.
	 * @param iMaxValue
	 *            Maximum value the selected value can be.
	 */
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
		bIsDraggingFirstTime = false;
		bIsBodySelected = false;

		fSlidingElementHeight = Math.min(fHeight * 0.2f, SLIDING_ELEMENT_MAX_HEIGHT);
		fDownButtonHeight = Math.min(fHeight * 0.2f, DOWN_BUTTON_MAX_HEIGHT);
		fDrawingStep =
			(float) iValueStep
				* ((fHeight - fSlidingElementHeight - fDownButtonHeight) / (float) (iMaxValue - iMinValue - 1));
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;

		textRenderer =
			new TextRenderer(new Font(SLIDER_FONT_NAME, SLIDER_FONT_STYLE, SLIDER_FONT_SIZE), false);
	}

	/**
	 * Draws the slider using the specified parameters.
	 * 
	 * @param gl
	 *            GL object that shall be used for drawing.
	 * @param pickingManager
	 *            Picking manager that shall be used.
	 * @param textureManager
	 *            Texture manager that shall be used.
	 * @param iViewID
	 *            ID of the view where the slider shall be drawn.
	 * @param iSliderID
	 *            Picking ID for the slider (the sliding element).
	 * @param iSliderButtonID
	 *            Picking ID for the slider button.
	 * @param iSliderBodyID
	 *            Picking ID for the slider body.
	 */
	public void draw(GL gl, PickingManager pickingManager, TextureManager textureManager, int iViewID,
		int iSliderID, int iSliderButtonID, int iSliderBodyID) {

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_SLIDER_BODY_SELECTION,
			iSliderBodyID));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fDownButtonHeight, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fDownButtonHeight, 0);
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() + fHeight, 0);
		gl.glVertex3f(vecPosition.x(), vecPosition.y() + fHeight, 0);
		gl.glEnd();

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_SLIDER_SELECTION,
			iSliderID));

		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), fSlidingElementDrawingPosition, 0.1f);
		gl.glVertex3f(vecPosition.x() + fWidth, fSlidingElementDrawingPosition, 0.1f);
		gl.glVertex3f(vecPosition.x() + fWidth, fSlidingElementDrawingPosition + fSlidingElementHeight, 0.1f);
		gl.glVertex3f(vecPosition.x(), fSlidingElementDrawingPosition + fSlidingElementHeight, 0.1f);
		gl.glEnd();

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iViewID,
			EPickingType.RAD_HIERARCHY_SLIDER_BUTTON_SELECTION, iSliderButtonID));

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

		Rectangle2D bounds = textRenderer.getBounds(new Integer(iSelectedValue).toString());
		float fFontScaling = determineFontScaling(new Integer(iSelectedValue).toString());

		float fTextPositionX =
			vecPosition.x() + fWidth / 2.0f - ((float) bounds.getWidth() / 2.0f) * fFontScaling;
		float fTextPositionY =
			fSlidingElementDrawingPosition + fSlidingElementHeight / 2.0f
				- ((float) bounds.getHeight() / 2.0f) * fFontScaling;
		textRenderer.setColor(1, 1, 1, 1);
		textRenderer.begin3DRendering();

		textRenderer.draw3D(new Integer(iSelectedValue).toString(), fTextPositionX, fTextPositionY, 0.1f,
			fFontScaling);

		textRenderer.end3DRendering();
		textRenderer.flush();

		gl.glPopAttrib();

	}

	/**
	 * Determines the scaling of a specified text that is needed for this text to fit into the sliding
	 * element.
	 * 
	 * @param sText
	 *            Text the scaling shall be calculated for.
	 * @return Scaling factor for the specified text.
	 */
	private float determineFontScaling(String sText) {
		Rectangle2D bounds = textRenderer.getBounds(sText);
		float fScalingWidth = (fWidth - 0.3f * fWidth) / (float) bounds.getWidth();
		float fScalingHeight =
			(fSlidingElementHeight - 0.3f * fSlidingElementHeight) / (float) bounds.getHeight();

		return Math.min(fScalingHeight, fScalingWidth);
	}

	/**
	 * Method that handles the dragging of the slider.
	 * 
	 * @param gl
	 *            Gl object.
	 * @param glMouseListener
	 *            Current mouse listener, used for determining the current mouse position.
	 * @return True, if the slider has been dragged, false otherwise.
	 */
	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (!bIsDragging && !bIsBodySelected) {
			return false;
		}

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fYCoordinate = fArTargetWorldCoordinates[1];

		if (bIsDraggingFirstTime) {
			fDraggingBottomSpacing = fYCoordinate - fSlidingElementDrawingPosition;
			bIsDraggingFirstTime = false;
		}

		if (bIsBodySelected) {
			bIsBodySelected = false;
			fDraggingBottomSpacing = 0;
		}

		int iNewSelectedValue =
			(int) ((fYCoordinate - fDraggingBottomSpacing - vecPosition.y() - fDownButtonHeight) / fDrawingStep);

		if (iNewSelectedValue < iSelectedValue) {
			iSelectedValue = iNewSelectedValue;
			if (iSelectedValue < iMinValue) {
				iSelectedValue = iMinValue;
			}
			fSlidingElementDrawingPosition =
				vecPosition.y() + fDownButtonHeight + (float) (iSelectedValue * fDrawingStep);
		}

		if (glMouseListener.wasMouseReleased()) {
			bIsDragging = false;
		}

		return true;
	}

	/**
	 * Handles the selection of the different slider parts.
	 * 
	 * @param pickingType
	 *            Type of the selected element.
	 * @return True, if the slider button has been selected, false otherwise.
	 */
	public boolean handleSliderSelection(EPickingType pickingType) {

		switch (pickingType) {
			case RAD_HIERARCHY_SLIDER_BODY_SELECTION:
				bIsBodySelected = true;
				break;

			case RAD_HIERARCHY_SLIDER_BUTTON_SELECTION:
				iSelectedValue -= iValueStep;
				if (iSelectedValue < iMinValue) {
					iSelectedValue = iMinValue;
				}

				fSlidingElementDrawingPosition =
					vecPosition.y() + fDownButtonHeight + (float) (iSelectedValue * fDrawingStep);
				return true;

			case RAD_HIERARCHY_SLIDER_SELECTION:
				bIsDragging = true;
				bIsDraggingFirstTime = true;
				break;
		}

		return false;

	}

	public Vec2f getPosition() {
		return vecPosition;
	}

	public void setPosition(Vec2f vecPosition) {
		this.vecPosition = vecPosition;
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}

	public float getHeight() {
		return fHeight;
	}

	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
		fSlidingElementHeight = Math.min(fHeight * 0.2f, SLIDING_ELEMENT_MAX_HEIGHT);
		fDownButtonHeight = Math.min(fHeight * 0.2f, DOWN_BUTTON_MAX_HEIGHT);
		fDrawingStep =
			(float) iValueStep
				* ((fHeight - fSlidingElementHeight - fDownButtonHeight) / (float) (iMaxValue - iMinValue - 1));
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
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
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}

	public int getValueStep() {
		return iValueStep;
	}

	public void setValueStep(int iValueStep) {
		this.iValueStep = iValueStep;
		fDrawingStep =
			(float) iValueStep
				* ((fHeight - fSlidingElementHeight - fDownButtonHeight) / (float) (iMaxValue - iMinValue - 1));
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}

	public int getMaxValue() {
		return iMaxValue;
	}

	public void setMaxValue(int iMaxValue) {
		this.iMaxValue = iMaxValue;
		fDrawingStep =
			(float) iValueStep
				* ((fHeight - fSlidingElementHeight - fDownButtonHeight) / (float) (iMaxValue - iMinValue - 1));
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}

	public int getMinValue() {
		return iMinValue;
	}

	public void setMinValue(int iMinValue) {
		this.iMinValue = iMinValue;
		fDrawingStep =
			(float) iValueStep
				* ((fHeight - fSlidingElementHeight - fDownButtonHeight) / (float) (iMaxValue - iMinValue - 1));
		fSlidingElementDrawingPosition =
			vecPosition.y() + fDownButtonHeight + (float) iSelectedValue * fDrawingStep;
	}
}
