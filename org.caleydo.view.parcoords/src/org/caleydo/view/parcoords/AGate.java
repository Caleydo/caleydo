package org.caleydo.view.parcoords;

import static org.caleydo.view.parcoords.PCRenderStyle.GATE_TIP_HEIGHT;
import javax.media.opengl.GL2;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public abstract class AGate extends AGLGUIElement {

	protected int axisID;
	protected int gateID;
	protected float currentPosition;

	protected DataTable table;
	protected PCRenderStyle renderStyle;
	protected float mouseTopSpacing;
	protected float mouseBottomSpacing;
	protected float top;
	protected float bottom;

	public abstract void draw(GL2 gl, PickingManager pickingManager,
			TextureManager textureManager, CaleydoTextRenderer textRenderer, int viewID);

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
	public void handleDragging(GL2 gl, float mousePositionX, float mousePositionY,
			PickingType draggedObject, boolean isGateDraggingFirstTime) {

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
	 * @param bottom
	 *            Value the bottom of the gate shall be set to.
	 */
	public void setBottom(float bottom) {
		this.bottom = bottom / renderStyle.getAxisHeight();
		// lowerValue = (float) table.getRawForNormalized(bottom /
		// renderStyle.getAxisHeight());

		// double setMin = table.getMinAs(EExternalDataRepresentation.NORMAL);

		// if (lowerValue < setMin) {
		// lowerValue = (float) setMin;
		// }
	}

	/**
	 * Sets the top of the gate.
	 * 
	 * @param top
	 *            Value the top of the gate shall be set to.
	 */
	public void setTop(float top) {
		this.top = top / renderStyle.getAxisHeight();
		// upperValue = (float) table.getRawForNormalized(top /
		// renderStyle.getAxisHeight());
		//
		// double setMax = table.getMaxAs(EExternalDataRepresentation.NORMAL);
		//
		// if (upperValue > setMax) {
		// upperValue = (float) setMax;
		// }
	}

	/**
	 * @return Lower cutoff value of the gate.
	 */
	public float getLowerValue() {
		return bottom;
	}

	/**
	 * @return Upper cutoff value of the gate.
	 */
	public float getUpperValue() {
		return top;
	}

	/**
	 * This returns false unless is it is overwritten in sub-classes.
	 * 
	 * @return
	 */
	public boolean isMasterGate() {
		return false;
	}

}
