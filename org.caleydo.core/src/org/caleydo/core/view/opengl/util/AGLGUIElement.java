package org.caleydo.core.view.opengl.util;

import java.nio.IntBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

public abstract class AGLGUIElement {

	private float minSize;

	public AGLGUIElement() {
		minSize = 10.0f;
	}

	public void beginGUIElement(GL gl) {
		IntBuffer buffer = BufferUtil.newIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = (float) minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > (float) currentWidth)
			scaling = referenceWidth / (float) currentWidth;

		gl.glPushMatrix();

		gl.glScalef(scaling, scaling, scaling);
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
		IntBuffer buffer = BufferUtil.newIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = (float) minSize * 10.0f;
		float scaling = 1;

		if (referenceWidth > (float) currentWidth)
			scaling = referenceWidth / (float) currentWidth;
		
		return value * scaling;
	}
}
