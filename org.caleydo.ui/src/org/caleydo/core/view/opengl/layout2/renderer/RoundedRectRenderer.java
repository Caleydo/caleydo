/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * Utility to render a rounded rect with various options
 *
 * @author Samuel Gratzl
 *
 */
public class RoundedRectRenderer {
	public static final int FLAG_FILL = 1<<0;
	public static final int FLAG_TOP_LEFT = 1<<2;
	public static final int FLAG_TOP_RIGHT = 1<<3;
	public static final int FLAG_BOTTOM_LEFT = 1<<4;
	public static final int FLAG_BOTTOM_RIGHT = 1<<5;

	public static final int FLAG_TOP = FLAG_TOP_LEFT | FLAG_TOP_RIGHT;
	public static final int FLAG_LEFT = FLAG_TOP_LEFT | FLAG_BOTTOM_LEFT;
	public static final int FLAG_RIGHT = FLAG_BOTTOM_RIGHT | FLAG_TOP_RIGHT;
	public static final int FLAG_BOTTOM = FLAG_BOTTOM_LEFT | FLAG_BOTTOM_RIGHT;
	public static final int FLAG_ALL = FLAG_TOP | FLAG_BOTTOM;

	private static boolean isFlagSet(int flags, int flag) {
		return (flags & flag) != 0;
	}

	public static void renderRoundedCorner(GLGraphics g, float x, float y, float radius, int segments, int direction) {
		float[] offsets = getRoundedOffsets(radius, segments);
		final int ol = offsets.length - 1;
		GL2 gl = g.gl;
		float z = g.z();
		switch (direction) {
		case FLAG_TOP_LEFT:
			gl.glVertex3f(x, y + radius, z);
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + offsets[ol - i], y + offsets[i], z);
			}
			gl.glVertex3f(x + radius, y, z);
			break;
		case FLAG_TOP_RIGHT:
			gl.glVertex3f(x, y, z);
			// round
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + radius - offsets[i], y + offsets[ol - i], z);
			}
			gl.glVertex3f(x + radius, y + radius, z);
			break;
		case FLAG_BOTTOM_LEFT:
			gl.glVertex3f(x + radius, y + radius, z);
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + offsets[i], y + radius - offsets[ol - i], z);
			}
			gl.glVertex3f(x, y, z);
			break;
		case FLAG_BOTTOM_RIGHT:
			gl.glVertex3f(x + radius, y, z);
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + radius - offsets[ol - i], y + radius - offsets[i], z);
			}
			gl.glVertex3f(x, y + radius, z);
			break;
		}
	}

	public static int render(GLGraphics g, float x, float y, float w, float h, float radius, int segments, int flags) {
		if (w < radius * 2 || h < radius * 2)
			return 0;
		assert w >= radius * 2 : "w" + w + " " + radius;
		assert h >= radius * 2 : "h" + h + " " + radius;
		assert radius > 0 : "radius <= 0";
		int count = 0;
		float[] offsets = getRoundedOffsets(radius, segments);
		final int ol = offsets.length - 1;

		GL2 gl = g.gl;
		float z = g.z();
		gl.glBegin(isFlagSet(flags, FLAG_FILL) ? GL2.GL_POLYGON : GL.GL_LINE_LOOP);

		count += 4;

		if (isFlagSet(flags, FLAG_TOP_LEFT)) {
			gl.glVertex3f(x, y + radius, z);
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + offsets[ol - i], y + offsets[i], z);
			}
			gl.glVertex3f(x + radius, y, z);
			count += offsets.length;
		} else
			gl.glVertex3f(x, y, z);

		if (isFlagSet(flags, FLAG_TOP_RIGHT)) {
			gl.glVertex3f(x + w - radius, y, z);
			// round
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + w - offsets[i], y + offsets[ol - i], z);
			}
			gl.glVertex3f(x + w, y + radius, z);
			count += offsets.length;
		} else
			gl.glVertex3f(x + w, y, z);

		if (isFlagSet(flags, FLAG_BOTTOM_RIGHT)) {
			gl.glVertex3f(x + w, y + h - radius, z);
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + w - offsets[ol - i], y + h - offsets[i], z);
			}
			gl.glVertex3f(x + w - radius, y + h, z);
			count += offsets.length;
		} else
			gl.glVertex3f(x + w, y + h, z);

		if (isFlagSet(flags, FLAG_BOTTOM_LEFT)) {
			gl.glVertex3f(x + radius, y + h, z);
			for (int i = 0; i < offsets.length; ++i) {
				gl.glVertex3f(x + offsets[i], y + h - offsets[ol - i], z);
			}
			gl.glVertex3f(x, y + h - radius, z);
			count += offsets.length;
		} else
			gl.glVertex3f(x, y + h, z);

		gl.glEnd();
		return count;
	}

	private static float[] getRoundedOffsets(float radius, int segments) {
		float[] offsets;

		if (segments < 2)
			offsets = new float[0];
		else {
			offsets = new float[segments - 1];
			double delta = Math.toRadians(90.f / segments);
			double act = delta;
			for (int i = 0; i < offsets.length; ++i) {
				offsets[i] = radius - (float) Math.sin(act) * radius;
				act += delta;
			}
		}
		return offsets;
	}
}
