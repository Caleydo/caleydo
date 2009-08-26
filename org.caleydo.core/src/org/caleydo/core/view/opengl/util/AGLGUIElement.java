package org.caleydo.core.view.opengl.util;

import gleem.linalg.Vec3f;

import java.nio.IntBuffer;

import javax.media.opengl.GL;
import com.sun.opengl.util.BufferUtil;

public abstract class AGLGUIElement {

	protected float minSize;
	protected float lastScaling;

	public AGLGUIElement() {
		minSize = 10.0f;
		lastScaling = 1.0f;
	}

	public void beginGUIElement(GL gl, Vec3f scalingPivot) {

		float scaling = getScaling(gl);

		gl.glPushMatrix();

		gl.glTranslatef(scalingPivot.x(), scalingPivot.y(), scalingPivot.z());
		gl.glScalef(scaling, scaling, scaling);
		gl.glTranslatef(-scalingPivot.x(), -scalingPivot.y(), -scalingPivot.z());

		lastScaling = scaling;
	}

	public void endGUIElement(GL gl) {
		gl.glPopMatrix();
	}

	public float getMinSize() {
		return minSize;
	}

	public void setMinSize(float minSize) {
		this.minSize = minSize;
	}

	public float getScaledSizeOf(GL gl, float value) {
		return value * getScaling(gl);
	}

	public float getScaling(GL gl) {
		IntBuffer buffer = BufferUtil.newIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = (float) minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > (float) currentWidth)
			scaling = referenceWidth / (float) currentWidth;

		lastScaling = scaling;

		return scaling;
	}

	public Vec3f getScaledPosition(GL gl, Vec3f position, Vec3f scalingPivot) {

		float scaling = getScaling(gl);
		lastScaling = scaling;

		return new Vec3f(((position.x() - scalingPivot.x()) * scaling) + scalingPivot.x(),
			((position.y() - scalingPivot.y()) * scaling) + scalingPivot.y(), 
			((position.z() - scalingPivot.z()) * scaling) + scalingPivot.z());
	}

	public float getScaledCoordinate(GL gl, float coordinate, float scalingPivotCoordinate) {

		float scaling = getScaling(gl);
		lastScaling = scaling;

		return ((coordinate - scalingPivotCoordinate) * scaling) + scalingPivotCoordinate;
	}

	public Vec3f getRealPositionFromScaledPosition(GL gl, Vec3f scaledPosition, Vec3f scalingPivot) {

		float scaling = getScaling(gl);
		lastScaling = scaling;

		return new Vec3f(((scaledPosition.x() - scalingPivot.x()) / scaling) + scalingPivot.x(),
			((scaledPosition.y() - scalingPivot.y()) / scaling) + scalingPivot.y(),
			((scaledPosition.z() - scalingPivot.z()) / scaling) + scalingPivot.z());
	}

	public float getRealCoordinateFromScaledCoordinate(GL gl, float scaledCoordinate,
		float scalingPivotCoordinate) {

		float scaling = getScaling(gl);
		lastScaling = scaling;

		return ((scaledCoordinate - scalingPivotCoordinate) / scaling) + scalingPivotCoordinate;
	}

	public float getLastScaling() {
		return lastScaling;
	}
}
