/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util;

import gleem.linalg.Vec3f;

import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Abstract base class for all GUI elements that require a minimum size.
 *
 * @author Christian Partl
 */
@Deprecated
public abstract class AGLGUIElement {

	protected float minSize;

	public AGLGUIElement() {
		minSize = 10.0f;
	}

	/**
	 * This function specifies the start of a GUI element, i.e. everything drawn afterwards will be scaled if
	 * smaller than the minimum size. Use endGUIElement to end this effect. Note, that calls of glTranslate
	 * between beginGUIElement and endGUIElement may affect the minimum size scaling and are therefore not
	 * recommended. Call these methods beforehand.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param scalingPivot
	 *            Position that is used as scaling pivot.
	 */
	public void beginGUIElement(GL2 gl, Vec3f scalingPivot) {

		float scaling = getScaling(gl, true);

		gl.glPushMatrix();

		gl.glTranslatef(scalingPivot.x(), scalingPivot.y(), scalingPivot.z());
		gl.glScalef(scaling, scaling, scaling);
		gl.glTranslatef(-scalingPivot.x(), -scalingPivot.y(), -scalingPivot.z());
	}

	/**
	 * This function specifies the end of a GUI element, i.e. everything drawn between beginGUIElement and
	 * endGUIElement will be scaled if smaller than the minimum size. Note, that calls of glTranslate between
	 * beginGUIElement and endGUIElement may affect the minimum size scaling and are therefore not
	 * recommended. Call these methods beforehand.
	 *
	 * @param gl
	 *            GL2 context.
	 */
	public void endGUIElement(GL2 gl) {
		gl.glPopMatrix();
	}

	/**
	 * @return The minimum size of the gui element.
	 */
	public float getMinSize() {
		return minSize;
	}

	/**
	 * Sets the minimum size of the gui element.
	 *
	 * @param minSize
	 *            Minimum size the gui element should have.
	 */
	public void setMinSize(float minSize) {
		this.minSize = minSize;
	}

	/**
	 * Gets the size the specified value would have when scaling it. Note that the scaling is dependent on the
	 * current window size and the minimum size of the gui element.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param value
	 *            Value for which the scaled size shall be calculated.
	 * @return Scaled value.
	 */
	public float getScaledSizeOf(GL2 gl, float value) {
		return value * getScaling(gl, true);
	}

	/**
	 * Gets the size the specified value would have when scaling it. Note that the scaling is dependent on the
	 * current window size and the minimum size of the gui element.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param value
	 *            Value for which the scaled size shall be calculated.
	 * @param useWidthAsReference
	 *            Determines whether the scaling us calculated relative to the width of height of the
	 *            viewport.
	 * @return Scaled value.
	 */
	public float getScaledSizeOf(GL2 gl, float value, boolean useWidthAsReference) {
		return value * getScaling(gl, false);
	}

	/**
	 * Gets the size the specified value would have when scaling it. Note that the scaling is dependent on the
	 * current window size and the minimum size of the gui element.
	 *
	 * @param viewportWidth
	 *            Width of the viewport.
	 * @param value
	 *            Value for which the scaled size shall be calculated.
	 * @return Scaled value.
	 */
	public float getScaledSizeOf(int viewportWidth, float value) {
		return value * getScaling(viewportWidth);
	}

	private float getScaling(int viewportWidth) {
		float referenceWidth = minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > viewportWidth)
			scaling = referenceWidth / viewportWidth;

		return scaling;
	}

	/**
	 * When submitting a scaled length this returns the value of the unscaled length. Note that the scaling is
	 * dependent on the current window size and the minimum size of the gui element.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param value
	 *            Value for which the scaled size shall be calculated.
	 * @return Scaled value.
	 */
	public float getUnscaledSizeOf(GL2 gl, float value) {
		return value / getScaling(gl, true);
	}

	/**
	 * Calculates the current scaling dependent on the current window size and the minimum size of the gui
	 * element.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param useWidthAsReference
	 *            Determines whether the scaling us calculated relative to the width of height of the
	 *            viewport.
	 * @return Current scaling.
	 */
	private float getScaling(GL2 gl, boolean useWidthAsReference) {
		IntBuffer buffer = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentSize = 0;
		if (useWidthAsReference) {
			currentSize = buffer.get(2);
		}
		else {
			currentSize = buffer.get(3);
		}

		float referenceSize = minSize * 10.0f;
		float scaling = 1;

		if (referenceSize > currentSize)
			scaling = referenceSize / currentSize;

		return scaling;
	}

	/**
	 * Calculates the position the specified position will have when drawn, i.e. calculates the scaled
	 * position.
	 *
	 * @param gl
	 *            GL2 Context.
	 * @param position
	 *            Position the scaled position shall be calculated for.
	 * @param scalingPivot
	 *            Position that is used as scaling pivot.
	 * @return Scaled position.
	 */
	public Vec3f getScaledPosition(GL2 gl, Vec3f position, Vec3f scalingPivot) {

		float scaling = getScaling(gl, true);

		return new Vec3f(((position.x() - scalingPivot.x()) * scaling) + scalingPivot.x(),
			((position.y() - scalingPivot.y()) * scaling) + scalingPivot.y(),
			((position.z() - scalingPivot.z()) * scaling) + scalingPivot.z());
	}

	/**
	 * Does the same as getScaledPosition, just for a single coordinate instead of three.
	 *
	 * @param gl
	 *            GL2 Context.
	 * @param coordinate
	 *            Coordinate the scaled coordinate shall be calculated for.
	 * @param scalingPivotCoordinate
	 *            Coordinate that is used as scaling pivot.
	 * @return Scaled coordinate.
	 */
	public float getScaledCoordinate(GL2 gl, float coordinate, float scalingPivotCoordinate) {

		float scaling = getScaling(gl, true);

		return ((coordinate - scalingPivotCoordinate) * scaling) + scalingPivotCoordinate;
	}

	/**
	 * Calculates the position that has to be used for drawing when the result should be the specified
	 * position, i.e. the inverse scaled position is calculated.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param scaledPosition
	 *            Position the inverse scaled position shall be calculated for.
	 * @param scalingPivot
	 *            Position that is used as scaling pivot.
	 * @return Inverse scaled position.
	 */
	public Vec3f getRealPositionFromScaledPosition(GL2 gl, Vec3f scaledPosition, Vec3f scalingPivot) {

		float scaling = getScaling(gl, true);

		return new Vec3f(((scaledPosition.x() - scalingPivot.x()) / scaling) + scalingPivot.x(),
			((scaledPosition.y() - scalingPivot.y()) / scaling) + scalingPivot.y(),
			((scaledPosition.z() - scalingPivot.z()) / scaling) + scalingPivot.z());
	}

	/**
	 * Does the same as getRealPositionFromScaledPosition, just for a single coordinate instead of three.
	 *
	 * @param gl
	 *            GL2 context.
	 * @param scaledCoordinate
	 *            Coordinate the inverse scaled coordinate shall be calculated for.
	 * @param scalingPivotCoordinate
	 *            Coordinate that is used as scaling pivot.
	 * @return Inverse scaled coordinate.
	 */
	public float getRealCoordinateFromScaledCoordinate(GL2 gl, float scaledCoordinate,
		float scalingPivotCoordinate) {

		float scaling = getScaling(gl, true);
		return ((scaledCoordinate - scalingPivotCoordinate) / scaling) + scalingPivotCoordinate;
	}

}
