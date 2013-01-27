/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.parcoords;

import static org.caleydo.view.parcoords.PCRenderStyle.GATE_TIP_HEIGHT;
import static org.caleydo.view.parcoords.PCRenderStyle.GATE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.GATE_Z;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

/**
 * Represents a gate for {@link GLParallelCoordinates}.
 *
 * @author Christian Partl
 * @author Alexander lex
 */
public class NominalGate extends AGate {

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
	public NominalGate(int gateID, int axisID, float bottom, float top, Table table, PCRenderStyle renderStyle) {
		this.gateID = gateID;

		this.table = table;
		this.renderStyle = renderStyle;
		this.axisID = axisID;
		this.top = top;
		this.bottom = bottom;
		minSize = 100;
	}

	/**
	 * Draws the gate using the upper and lower cutoff values to calculate the top and bottom of the gate.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param pickingManager
	 *            PickingManager that shall be used.
	 * @param textureManager
	 *            TextureManager that shall be used.
	 * @param textRenderer
	 *            TextRenderer that shall be used.
	 * @param viewID
	 *            Unique ID of the view.
	 */
	@Override
	public void draw(GL2 gl, PickingManager pickingManager, TextureManager textureManager,
			CaleydoTextRenderer textRenderer, int viewID) {

		// top = (float) table.getNormalizedForRaw(upperValue) *
		// renderStyle.getAxisHeight();
		// top = upperValue;

		// Scaled bottom = unscaled bottom !
		// bottom = lowerValue * renderStyle.getAxisHeight();
		// bottom = upperValue;
		float unscaledTop = getRealCoordinateFromScaledCoordinate(gl, top * renderStyle.getAxisHeight(), bottom);
		float unscaledBottom = bottom * renderStyle.getAxisHeight();

		Vec3f scalingPivot = new Vec3f(currentPosition, bottom, GATE_Z);

		beginGUIElement(gl, scalingPivot);

		gl.glColor4f(1, 1, 1, 0f);
		int PickingID = pickingManager.getPickingID(viewID, EPickingType.REMOVE_GATE.name(), gateID);
		gl.glPushName(PickingID);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(currentPosition + GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop, GATE_Z);
		gl.glVertex3f(currentPosition + GATE_WIDTH, unscaledTop, GATE_Z);
		gl.glEnd();
		gl.glPopName();

		// The tip of the gate
		Vec3f lowerLeftCorner = new Vec3f(currentPosition - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		Vec3f lowerRightCorner = new Vec3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT,
				GATE_Z);
		Vec3f upperRightCorner = new Vec3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop, GATE_Z);
		Vec3f upperLeftCorner = new Vec3f(currentPosition - GATE_WIDTH, unscaledTop, GATE_Z);

		gl.glPushName(pickingManager.getPickingID(viewID, EPickingType.GATE_TIP_SELECTION.name(), gateID));

		textureManager.renderTexture(gl, EIconTextures.GATE_TOP, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		float menuHeight = 8 * GATE_WIDTH / 3.5f;

		lowerLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledTop + menuHeight, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop + menuHeight, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop, GATE_Z);
		upperLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledTop, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_MENUE, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		textRenderer.setColor(1, 1, 1, 1);
		// TODO insert correct text here
		renderNumber(textRenderer, Formatter.formatNumber(top), currentPosition - 5 * GATE_WIDTH, unscaledTop + 0.02f);
		gl.glPopName();

		// if (table.isSetHomogeneous())
		// {
		// // renderBoxedYValues(gl, fCurrentPosition, fTop,
		// // getDecimalFormat().format(
		// // table.getRawForNormalized(fTop / renderStyle.getAxisHeight())),
		// // SelectionType.NORMAL);
		// }
		// else
		// {
		// // TODO dimension based acces
		// }

		gl.glPushName(pickingManager.getPickingID(viewID, EPickingType.GATE_BODY_SELECTION.name(), gateID));

		lowerLeftCorner.set(currentPosition - GATE_WIDTH, unscaledBottom + PCRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, unscaledBottom + PCRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		upperLeftCorner.set(currentPosition - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_BODY, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(viewID, EPickingType.GATE_BOTTOM_SELECTION.name(), gateID));

		lowerLeftCorner.set(currentPosition - GATE_WIDTH, unscaledBottom, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, unscaledBottom, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledBottom + PCRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		upperLeftCorner.set(currentPosition - GATE_WIDTH, unscaledBottom + PCRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_BOTTOM, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		lowerLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledBottom - menuHeight, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, unscaledBottom - menuHeight, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledBottom, GATE_Z);
		upperLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledBottom, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_MENUE, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		textRenderer.setColor(1, 1, 1, 1);
		// TODO: insert correct text here
		renderNumber(textRenderer, Formatter.formatNumber(bottom), currentPosition - 5 * GATE_WIDTH, unscaledBottom
				- menuHeight + 0.02f);
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
		textRenderer.draw3D(rawValue, xOrigin, yOrigin, PCRenderStyle.TEXT_ON_LABEL_Z, scaling);
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
	 * Handles the dragging of the current gate.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param mousePositionX
	 *            X coordinate of the mouse position.
	 * @param mousePositionY
	 *            Y coordinate of the mouse position.
	 * @param draggedObject
	 *            Specifies the part of the gate that has been dragged.
	 * @param isGateDraggingFirstTime
	 *            Specifies whether the gate is dragged the first time or not.
	 */
	@Override
	public void handleDragging(GL2 gl, float mousePositionX, float mousePositionY, EPickingType draggedObject,
			boolean isGateDraggingFirstTime) {

		if (isGateDraggingFirstTime) {
			mouseTopSpacing = top - (mousePositionY / renderStyle.getAxisHeight());
			mouseBottomSpacing = (mousePositionY / renderStyle.getAxisHeight()) - bottom;
			isGateDraggingFirstTime = false;
		}

		float tipUpperLimit = 1;
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
			setTop(tipUpperLimit * renderStyle.getAxisHeight());
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
	 * Sets the upper cutoff value of the gate.
	 *
	 * @param upperValue
	 *            Value the upper cutoff value shall be set to.
	 */
	// public void setUpperValue(float upperValue) {
	// this.upperValue = upperValue;
	// top = (float) table.getNormalizedForRaw(upperValue) *
	// renderStyle.getAxisHeight();
	// }

	/**
	 * Sets the lower cutoff value of the gate.
	 *
	 * @param lowerValue
	 *            Value the lower cutoff value shall be set to.
	 */
	// public void setLowerValue(float lowerValue) {
	// this.lowerValue = lowerValue;
	// bottom = (float) table.getNormalizedForRaw(lowerValue) *
	// renderStyle.getAxisHeight();
	// }

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
