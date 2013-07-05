/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import java.util.Arrays;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * a abstraction of a padding used by label renderer
 *
 * @author Samuel Gratzl
 *
 */
public class Padding {
	public static final Padding NONE = new Padding(EMode.GL, 0);

	private final float[] padding;
	private final EMode mode;

	public enum EMode {
		GL, PIXEL, PROPORTIONAL
	}

	public Padding(EMode mode, float padding) {
		this(mode, padding, padding);
	}

	public Padding(EMode mode, float paddingH, float paddingV) {
		this(mode, paddingH, paddingV, paddingH, paddingV);
	}

	public Padding(EMode mode, float east, float north, float west, float south) {
		this.mode = mode;
		padding = new float[] { east, north, west, south };
	}

	@Override
	public String toString() {
		return Arrays.toString(padding);
	}

	/**
	 * @param pixelGLConverter
	 * @return
	 */
	public float[] resolve(PixelGLConverter pixelGLConverter, float width, float height) {
		switch(mode) {
		case GL:
			return padding;
		case PIXEL: {
			float w = pixelGLConverter.getGLWidthForPixelWidth(1);
			float h = pixelGLConverter.getGLHeightForPixelHeight(1);
			return new float[]{ padding[0]*w,padding[1]*h,padding[2]*w,padding[3]*h};
		}
		case PROPORTIONAL:
			return new float[] { padding[0] * width, padding[1] * height, padding[2] * width, padding[3] * height };
		default:
			throw new IllegalStateException();
		}
	}

	public float get(int direction) {
		return this.padding[direction];
	}
}
