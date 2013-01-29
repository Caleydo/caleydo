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

import static org.caleydo.view.parcoords.PCRenderStyle.GATE_Z;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Represents a gate for {@link GLParallelCoordinates}.
 *
 * @author Christian Partl
 * @author Alexander Lex
 */
public class Gate {

	protected int axisID;
	protected int gateID;
	protected float xPosition;

	protected Table table;
	protected float mouseTopSpacing;
	protected float mouseBottomSpacing;

	/** the upper value of the gate in normalized (0-1) range */
	protected float upperBound;
	/** the lower value of the gate in normalized (0-1) range */
	protected float lowerBound;

	// /** Table holding numerical data, shadowing {@link AGate#table} */
	// @SuppressWarnings("hiding")
	// private NumericalTable table;
	/**
	 * Flag determining whether this gate is a master gate or not, defaults to false
	 */
	private boolean isMasterGate = false;

	/** The data transformation used by the PCs */
	private String dataTransformation;

	private PixelGLConverter pixelGLConverter;

	private GLParallelCoordinates pcs;

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
	public Gate(GLParallelCoordinates pcs, int gateID, int axisID, float lowerBound, float upperBound) {
		this.pcs = pcs;
		this.gateID = gateID;
		this.axisID = axisID;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		pixelGLConverter = pcs.getPixelGLConverter();
		table = pcs.getDataDomain().getTable();
		dataTransformation = pcs.getDataTransformation();

		// this.table = table;
		// this.renderStyle = renderStyle;
		// this.dataTransformation = dataTransformation;
		// this.pixelGLConverter = pixelGLConverter;

	}

	/**
	 * Draws the gate using the upper and lower cutoff values to calculate the upperBound and lowerBound of the gate.
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
	public void draw(GL2 gl) {

		float top = upperBound * pcs.renderStyle.getAxisHeight();
		// upperBound = upperValue;

		// Scaled lowerBound = unscaled lowerBound !
		float bottom = lowerBound * pcs.renderStyle.getAxisHeight();
		// lowerBound = upperValue;

		// gate body texture has width 18, use half
		float width = pixelGLConverter.getGLWidthForPixelWidth(9);

		// gate top texture is 32x14
		float gateTopWidth = pixelGLConverter.getGLWidthForPixelWidth(32);
		float gateTopHeight = pixelGLConverter.getGLHeightForPixelHeight(14);

		gl.glColor4f(1, 1, 1, 0f);
		int PickingID = pcs.getPickingManager().getPickingID(pcs.getID(), EPickingType.REMOVE_GATE.name(), gateID);
		gl.glPushName(PickingID);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(xPosition + width, top - gateTopHeight, GATE_Z);
		gl.glVertex3f(xPosition - width, top - gateTopHeight, GATE_Z);
		gl.glVertex3f(xPosition - width, top, GATE_Z);
		gl.glVertex3f(xPosition + width, top, GATE_Z);
		gl.glEnd();
		gl.glPopName();

		// The tip of the gate
		Vec3f lowerLeftCorner = new Vec3f(xPosition - width, top - gateTopHeight, GATE_Z);
		Vec3f lowerRightCorner = new Vec3f(xPosition + gateTopWidth - width, top - gateTopHeight, GATE_Z);
		Vec3f upperRightCorner = new Vec3f(xPosition + gateTopWidth - width, top, GATE_Z);
		Vec3f upperLeftCorner = new Vec3f(xPosition - width, top, GATE_Z);

		gl.glPushName(pcs.getPickingManager().getPickingID(pcs.getID(), EPickingType.GATE_TIP_SELECTION.name(), gateID));

		pcs.getTextureManager().renderTexture(gl, PCRenderStyle.GATE_TOP, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		// gate_menu texture is 77x22
		float menuWidth = pixelGLConverter.getGLWidthForPixelWidth(77);
		float menuHeight = pixelGLConverter.getGLHeightForPixelHeight(22);

		lowerLeftCorner.set(xPosition - menuWidth + width, top + menuHeight, GATE_Z);
		lowerRightCorner.set(xPosition, top + menuHeight, GATE_Z);
		upperRightCorner.set(xPosition, top, GATE_Z);
		upperLeftCorner.set(xPosition - menuWidth + width, top, GATE_Z);

		pcs.getTextureManager().renderTexture(gl, PCRenderStyle.GATE_MENUE, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		pcs.getTextRenderer().setColor(1, 1, 1, 1);

		float textWidth = pixelGLConverter.getGLWidthForPixelWidth(40);
		float textHeight = pixelGLConverter.getGLHeightForPixelHeight(12);
		float textBottomSpacing = pixelGLConverter.getGLHeightForPixelHeight(6);

		if (table instanceof NumericalTable) {

			String caption = Formatter.formatNumber(((NumericalTable) table).getRawForNormalized(dataTransformation,
					upperBound));
			pcs.getTextRenderer().renderTextInBounds(gl, caption, xPosition - menuWidth + 3 * width,
					top + textBottomSpacing, PCRenderStyle.TEXT_ON_LABEL_Z, textWidth, textHeight);

		}
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

		gl.glPushName(pcs.getPickingManager()
				.getPickingID(pcs.getID(), EPickingType.GATE_BODY_SELECTION.name(), gateID));

		// gate bottom texture is as wide as gate body and 3 px high
		float gateBottomHeight = pixelGLConverter.getGLHeightForPixelHeight(3);

		lowerLeftCorner.set(xPosition - width, bottom + gateBottomHeight, GATE_Z);
		lowerRightCorner.set(xPosition + width, bottom + gateBottomHeight, GATE_Z);
		upperRightCorner.set(xPosition + width, top - gateTopHeight, GATE_Z);
		upperLeftCorner.set(xPosition - width, top - gateTopHeight, GATE_Z);

		pcs.getTextureManager().renderTexture(gl, PCRenderStyle.GATE_BODY, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		gl.glPushName(pcs.getPickingManager().getPickingID(pcs.getID(), EPickingType.GATE_BOTTOM_SELECTION.name(),
				gateID));

		lowerLeftCorner.set(xPosition - width, bottom, GATE_Z);
		lowerRightCorner.set(xPosition + width, bottom, GATE_Z);
		upperRightCorner.set(xPosition + width, bottom + gateBottomHeight, GATE_Z);
		upperLeftCorner.set(xPosition - width, bottom + gateBottomHeight, GATE_Z);

		pcs.getTextureManager().renderTexture(gl, PCRenderStyle.GATE_BOTTOM, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		lowerLeftCorner.set(xPosition - menuWidth + width, bottom - menuHeight, GATE_Z);
		lowerRightCorner.set(xPosition + width, bottom - menuHeight, GATE_Z);
		upperRightCorner.set(xPosition + width, bottom, GATE_Z);
		upperLeftCorner.set(xPosition - menuWidth + width, bottom, GATE_Z);

		pcs.getTextureManager().renderTexture(gl, PCRenderStyle.GATE_MENUE, lowerLeftCorner, lowerRightCorner,
				upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		if (table instanceof NumericalTable) {

			String caption = Formatter.formatNumber(((NumericalTable) table).getRawForNormalized(dataTransformation,
					lowerBound));
			pcs.getTextRenderer().renderTextInBounds(gl, caption, xPosition - menuWidth + 3 * width,
					bottom - menuHeight + textBottomSpacing, PCRenderStyle.TEXT_ON_LABEL_Z, textWidth, textHeight);

		}
		gl.glPopName();

	}

	public int getGateID() {
		return gateID;
	}

	public void setGateID(int gateID) {
		this.gateID = gateID;
	}

	/**
	 * Sets a gate to be a master gate (a gate which is used accross several axes). Defaults to false, if this is not
	 * called.
	 *
	 * @param isMasterGate
	 *            true if this should be a master gate
	 */
	public void setMasterGate(boolean isMasterGate) {
		this.isMasterGate = isMasterGate;
	}

	/**
	 * @return The current position (x coordinate) of the gate.
	 */
	public float getCurrentPosition() {
		return xPosition;
	}

	/**
	 * Sets the upperBound of the gate.
	 *
	 * @param upperBound
	 *            Value the upperBound of the gate shall be set to.
	 */

	public void setUpperBound(float upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * @return Top of the gate.
	 */
	public float getUpperBound() {
		return upperBound;
	}

	/**
	 * @param lowerBound
	 *            setter, see {@link lowerBound}
	 */
	public void setLowerBound(float lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * @return Bottom of the gate.
	 */
	public float getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the ID of the associated axis
	 */
	public int getAxisID() {
		return axisID;
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
	public void handleDragging(GL2 gl, float mousePositionX, float mousePositionY, EPickingType draggedObject,
			boolean isGateDraggingFirstTime) {

		float bottomSpacing = pcs.fYTranslation;
		float bound = (mousePositionY - bottomSpacing) / pcs.renderStyle.getAxisHeight();

		if (isGateDraggingFirstTime) {
			mouseTopSpacing = upperBound - bound;
			mouseBottomSpacing = bound - lowerBound;
			isGateDraggingFirstTime = false;
		}

		float tipUpperLimit = 1;
		float tipLowerLimit = 0.01f;
		float bottomLowerLimit = 0;
		float bottomUpperLimit = 0.99f;

		switch (draggedObject) {

		case GATE_TIP_SELECTION:
			setUpperBound(bound);
			break;

		case GATE_BOTTOM_SELECTION:
			setLowerBound(bound);
			break;

		case GATE_BODY_SELECTION:
			setLowerBound(bound - mouseBottomSpacing);
			setUpperBound(bound + mouseTopSpacing);

			break;

		default:
			return;
		}

		if (upperBound > tipUpperLimit) {
			setUpperBound(tipUpperLimit);
		}
		if (upperBound < tipLowerLimit) {
			setUpperBound(tipLowerLimit);
		}
		if (lowerBound > bottomUpperLimit) {
			setLowerBound(bottomUpperLimit);
		}
		if (lowerBound < bottomLowerLimit) {
			setLowerBound(bottomLowerLimit);
		}
	}

	/**
	 * This returns false unless is it is overwritten in sub-classes.
	 *
	 * @return
	 */
	public boolean isMasterGate() {
		return false;
	}

	/**
	 * @param xPosition
	 *            setter, see {@link xPosition}
	 */
	public void setxPosition(float xPosition) {
		this.xPosition = xPosition;
	}

}
