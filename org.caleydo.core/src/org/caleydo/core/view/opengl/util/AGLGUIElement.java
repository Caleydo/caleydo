package org.caleydo.core.view.opengl.util;

import gleem.linalg.Vec3f;

import java.nio.IntBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * Abstract base class for all GUI elements that require a minimum size.
 * 
 * @author Christian Partl
 */
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
	 *            GL context.
	 * @param scalingPivot
	 *            Position that is used as scaling pivot.
	 */
	public void beginGUIElement(GL gl, Vec3f scalingPivot) {

		float scaling = getScaling(gl);

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
	 *            GL context.
	 */
	public void endGUIElement(GL gl) {
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
	 *            GL context.
	 * @param value
	 *            Value for which the scaled size shall be calculated.
	 * @return Scaled value.
	 */
	public float getScaledSizeOf(GL gl, float value) {
		return value * getScaling(gl);
	}

	/**
	 * Calculates the current scaling dependent on the current window size and the minimum size of the gui
	 * element.
	 * 
	 * @param gl
	 *            GL context.
	 * @return Current scaling.
	 */
	private float getScaling(GL gl) {
		IntBuffer buffer = BufferUtil.newIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > currentWidth)
			scaling = referenceWidth / currentWidth;

		return scaling;
	}

	/**
	 * Calculates the position the specified position will have when drawn, i.e. calculates the scaled
	 * position.
	 * 
	 * @param gl
	 *            GL Context.
	 * @param position
	 *            Position the scaled position shall be calculated for.
	 * @param scalingPivot
	 *            Position that is used as scaling pivot.
	 * @return Scaled position.
	 */
	public Vec3f getScaledPosition(GL gl, Vec3f position, Vec3f scalingPivot) {

		float scaling = getScaling(gl);

		return new Vec3f(((position.x() - scalingPivot.x()) * scaling) + scalingPivot.x(),
			((position.y() - scalingPivot.y()) * scaling) + scalingPivot.y(), ((position.z() - scalingPivot
				.z()) * scaling)
				+ scalingPivot.z());
	}

	/**
	 * Does the same as getScaledPosition, just for a single coordinate instead of three.
	 * 
	 * @param gl
	 *            GL Context.
	 * @param coordinate
	 *            Coordinate the scaled coordinate shall be calculated for.
	 * @param scalingPivotCoordinate
	 *            Coordinate that is used as scaling pivot.
	 * @return Scaled coordinate.
	 */
	public float getScaledCoordinate(GL gl, float coordinate, float scalingPivotCoordinate) {

		float scaling = getScaling(gl);

		return ((coordinate - scalingPivotCoordinate) * scaling) + scalingPivotCoordinate;
	}

	/**
	 * Calculates the position that has to be used for drawing when the result should be the specified
	 * position, i.e. the inverse scaled position is calculated.
	 * 
	 * @param gl
	 *            GL context.
	 * @param scaledPosition
	 *            Position the inverse scaled position shall be calculated for.
	 * @param scalingPivot
	 *            Position that is used as scaling pivot.
	 * @return Inverse scaled position.
	 */
	public Vec3f getRealPositionFromScaledPosition(GL gl, Vec3f scaledPosition, Vec3f scalingPivot) {

		float scaling = getScaling(gl);

		return new Vec3f(((scaledPosition.x() - scalingPivot.x()) / scaling) + scalingPivot.x(),
			((scaledPosition.y() - scalingPivot.y()) / scaling) + scalingPivot.y(),
			((scaledPosition.z() - scalingPivot.z()) / scaling) + scalingPivot.z());
	}

	/**
	 * Does the same as getRealPositionFromScaledPosition, just for a single coordinate instead of three.
	 * 
	 * @param gl
	 *            GL context.
	 * @param scaledCoordinate
	 *            Coordinate the inverse scaled coordinate shall be calculated for.
	 * @param scalingPivotCoordinate
	 *            Coordinate that is used as scaling pivot.
	 * @return Inverse scaled coordinate.
	 */
	public float getRealCoordinateFromScaledCoordinate(GL gl, float scaledCoordinate,
		float scalingPivotCoordinate) {

		float scaling = getScaling(gl);
		return ((scaledCoordinate - scalingPivotCoordinate) / scaling) + scalingPivotCoordinate;
	}

}
