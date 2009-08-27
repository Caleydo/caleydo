package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_TIP_HEIGHT;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.getDecimalFormat;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Represents a gate for {@link GLParallelCoordinates}.
 * 
 * @author Christian Partl
 */
public class Gate
	extends AGLGUIElement {

	private int gateID;
	private float currentPosition;
	private float upperValue;
	private float lowerValue;
	private ISet set;
	private ParCoordsRenderStyle renderStyle;
	private float mouseTopSpacing;
	private float mouseBottomSpacing;
	private float top;
	private float bottom;

	/**
	 * Constructor.
	 * 
	 * @param gateID
	 *            ID of the gate.
	 * @param lowerValue
	 *            Lower cutoff value.
	 * @param upperValue
	 *            Upper cutoff value.
	 * @param set
	 *            Set.
	 * @param renderStyle
	 *            Render Style.
	 */
	public Gate(int gateID, float lowerValue, float upperValue, ISet set, ParCoordsRenderStyle renderStyle) {
		this.gateID = gateID;
		this.upperValue = upperValue;
		this.lowerValue = lowerValue;
		this.set = set;
		this.renderStyle = renderStyle;
		top = (float) set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight();
		bottom = (float) set.getNormalizedForRaw(lowerValue) * renderStyle.getAxisHeight();
		minSize = 100;
	}

	/**
	 * Draws the gate using the upper and lower cutoff values to calculate the top and bottom of the gate.
	 * 
	 * @param gl
	 *            GL context.
	 * @param pickingManager
	 *            PickingManager that shall be used.
	 * @param textureManager
	 *            TextureManager that shall be used.
	 * @param textRenderer
	 *            TextRenderer that shall be used.
	 * @param iViewID
	 *            Unique ID of the view.
	 */
	public void draw(GL gl, PickingManager pickingManager, TextureManager textureManager,
		CaleydoTextRenderer textRenderer, int iViewID) {

		top = (float) set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight();

		// Scaled bottom = unscaled bottom !
		bottom = (float) set.getNormalizedForRaw(lowerValue) * renderStyle.getAxisHeight();
		float unscaledTop = getRealCoordinateFromScaledCoordinate(gl, top, bottom);

		Vec3f scalingPivot = new Vec3f(currentPosition, bottom, GATE_Z);

		beginGUIElement(gl, scalingPivot);

		gl.glColor4f(1, 1, 1, 0f);
		int PickingID = pickingManager.getPickingID(iViewID, EPickingType.REMOVE_GATE, gateID);
		gl.glPushName(PickingID);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(currentPosition + GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop, GATE_Z);
		gl.glVertex3f(currentPosition + GATE_WIDTH, unscaledTop, GATE_Z);
		gl.glEnd();
		gl.glPopName();

		// The tip of the gate
		Vec3f lowerLeftCorner =
			new Vec3f(currentPosition - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		Vec3f lowerRightCorner =
			new Vec3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		Vec3f upperRightCorner = new Vec3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop, GATE_Z);
		Vec3f upperLeftCorner = new Vec3f(currentPosition - GATE_WIDTH, unscaledTop, GATE_Z);

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GATE_TIP_SELECTION, gateID));

		textureManager.renderTexture(gl, EIconTextures.GATE_TOP, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		float menuHeight = 8 * GATE_WIDTH / 3.5f;

		lowerLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledTop + menuHeight, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop + menuHeight, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop, GATE_Z);
		upperLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledTop, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_MENUE, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		textRenderer.setColor(1, 1, 1, 1);
		renderNumber(textRenderer, getDecimalFormat().format(upperValue), currentPosition - 5 * GATE_WIDTH,
			unscaledTop + 0.02f);
		gl.glPopName();

		// if (set.isSetHomogeneous())
		// {
		// // renderBoxedYValues(gl, fCurrentPosition, fTop,
		// // getDecimalFormat().format(
		// // set.getRawForNormalized(fTop / renderStyle.getAxisHeight())),
		// // ESelectionType.NORMAL);
		// }
		// else
		// {
		// // TODO storage based acces
		// }

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GATE_BODY_SELECTION, gateID));

		lowerLeftCorner.set(currentPosition - GATE_WIDTH, bottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, bottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		upperLeftCorner.set(currentPosition - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_BODY, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GATE_BOTTOM_SELECTION, gateID));

		lowerLeftCorner.set(currentPosition - GATE_WIDTH, bottom, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, bottom, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, bottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		upperLeftCorner.set(currentPosition - GATE_WIDTH, bottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_BOTTOM, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		lowerLeftCorner.set(currentPosition - 7 * GATE_WIDTH, bottom - menuHeight, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, bottom - menuHeight, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, bottom, GATE_Z);
		upperLeftCorner.set(currentPosition - 7 * GATE_WIDTH, bottom, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_MENUE, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		textRenderer.setColor(1, 1, 1, 1);
		renderNumber(textRenderer, getDecimalFormat().format(lowerValue), currentPosition - 5 * GATE_WIDTH,
			bottom - menuHeight + 0.02f);
		gl.glPopName();

		endGUIElement(gl);

	}

	/**
	 * Renders a specified number.
	 * 
	 * @param textRenderer
	 *            TextRenderer that shall be used.
	 * @param rawValue
	 *            Number to render.
	 * @param xOrigin
	 *            X coordinate of the position where the number shall be rendered.
	 * @param yOrigin
	 *            Y coordinate of the position where the number shall be rendered.
	 */
	private void renderNumber(CaleydoTextRenderer textRenderer, String rawValue, float xOrigin, float yOrigin) {

		textRenderer.begin3DRendering();
		float scaling = 0.004f;
		textRenderer.draw3D(rawValue, xOrigin, yOrigin, ParCoordsRenderStyle.TEXT_ON_LABEL_Z, scaling);
		textRenderer.end3DRendering();
	}

	public int getGateID() {
		return gateID;
	}

	public void setGateID(int gateID) {
		this.gateID = gateID;
	}

	/**
	 * @return The current position (x coordinate) of the gate.
	 */
	public float getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * Sets the current position (x coordinate) of the gate.
	 * 
	 * @param currentPosition
	 *            Position of the gate.
	 */
	public void setCurrentPosition(float currentPosition) {
		this.currentPosition = currentPosition;
	}

	/**
	 * Handles the dragging of the current gate.
	 * 
	 * @param gl
	 *            GL context.
	 * @param mousePositionX
	 *            X coordinate of the mouse position.
	 * @param mousePositionY
	 *            Y coordinate of the mouse position.
	 * @param draggedObject
	 *            Specifies the part of the gate that has been dragged.
	 * @param isGateDraggingFirstTime
	 *            Specifies whether the gate is dragged the first time or not.
	 */
	public void handleDragging(GL gl, float mousePositionX, float mousePositionY, EPickingType draggedObject,
		boolean isGateDraggingFirstTime) {

		if (isGateDraggingFirstTime) {
			mouseTopSpacing = top - mousePositionY;
			mouseBottomSpacing = mousePositionY - bottom;
			isGateDraggingFirstTime = false;
		}

		float tipUpperLimit = renderStyle.getAxisHeight();
		float tipLowerLimit = bottom + getScaledSizeOf(gl, GATE_TIP_HEIGHT);
		float bottomLowerLimit = 0;
		float bottomUpperLimit = top - getScaledSizeOf(gl, GATE_TIP_HEIGHT);

		switch (draggedObject) {

			case GATE_TIP_SELECTION:
				setTop(mousePositionY);
				break;

			case GATE_BOTTOM_SELECTION:
				setBottom(mousePositionY);
				break;

			case GATE_BODY_SELECTION:
				setBottom(mousePositionY - mouseBottomSpacing);
				setTop(mousePositionY + mouseTopSpacing);

				break;

			default:
				return;
		}

		if (top > tipUpperLimit) {
			setTop(tipUpperLimit);
		}
		if (top < tipLowerLimit) {
			setTop(tipLowerLimit);
		}
		if (bottom > bottomUpperLimit) {
			setBottom(bottomUpperLimit);
		}
		if (bottom < bottomLowerLimit) {
			setBottom(bottomLowerLimit);
		}
	}

	/**
	 * Sets the bottom of the gate.
	 * 
	 * @param bottom Value the bottom of the gate shall be set to.
	 */
	public void setBottom(float bottom) {
		this.bottom = bottom;
		lowerValue = (float) set.getRawForNormalized(bottom / renderStyle.getAxisHeight());

		double setMin = set.getMinAs(EExternalDataRepresentation.NORMAL);

		if (lowerValue < setMin) {
			lowerValue = (float) setMin;
		}
	}

	/**
	 * Sets the top of the gate.
	 * 
	 * @param top Value the top of the gate shall be set to.
	 */
	public void setTop(float top) {
		this.top = top;
		upperValue = (float) set.getRawForNormalized(top / renderStyle.getAxisHeight());

		double setMax = set.getMaxAs(EExternalDataRepresentation.NORMAL);

		if (upperValue > setMax) {
			upperValue = (float) setMax;
		}
	}

	/**
	 * Sets the upper cutoff value of the gate.
	 * 
	 * @param upperValue Value the upper cutoff value shall be set to.
	 */
	public void setUpperValue(float upperValue) {
		this.upperValue = upperValue;
		top = (float) set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight();
	}

	/**
	 * @return Upper cutoff value of the gate.
	 */
	public float getUpperValue() {
		return upperValue;
	}

	/**
	 * Sets the lower cutoff value of the gate.
	 * 
	 * @param lowerValue Value the lower cutoff value shall be set to.
	 */
	public void setLowerValue(float lowerValue) {
		this.lowerValue = lowerValue;
		bottom = (float) set.getNormalizedForRaw(lowerValue) * renderStyle.getAxisHeight();
	}

	/**
	 * @return Lower cutoff value of the gate.
	 */
	public float getLowerValue() {
		return lowerValue;
	}

	/**
	 * @return Top of the gate.
	 */
	public float getTop() {
		return top;
	}

	/**
	 * @return Bottom of the gate.
	 */
	public float getBottom() {
		return bottom;
	}

}
