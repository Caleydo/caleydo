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

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * factory class for different {@link IDim}s
 * 
 * @author Samuel Gratzl
 * 
 */

public final class Dims {
	private Dims() {

	}

	public static final IDim zero = fix(0);

	public static IDim xpixel(final int value) {
		return new IDim() {
			@Override
			public float resolve(PixelGLConverter converter, float w, float h) {
				return converter.getGLWidthForPixelWidth(value);
			}

			@Override
			public String toString() {
				return String.format("y%dpx", value);
			}
		};
	}

	public static IDim ypixel(final int value) {
		return new IDim() {
			@Override
			public float resolve(PixelGLConverter converter, float w, float h) {
				return converter.getGLHeightForPixelHeight(value);
			}

			@Override
			public String toString() {
				return String.format("x%dpx", value);
			}
		};
	}


	public static IDim fix(final float value) {
		return new IDim() {
			@Override
			public float resolve(PixelGLConverter converter, float w, float h) {
				return value;
			}

			@Override
			public String toString() {
				return String.format("%f", value);
			}
		};
	}

	public static IDim xprop(final float value) {
		return new IDim() {
			@Override
			public float resolve(PixelGLConverter converter, float w, float h) {
				return w * value;
			}

			@Override
			public String toString() {
				return String.format("x%f%%", value);
			}
		};
	}

	public static IDim yprop(final float value) {
		return new IDim() {
			@Override
			public float resolve(PixelGLConverter converter, float w, float h) {
				return h * value;
			}

			@Override
			public String toString() {
				return String.format("y%f%%", value);
			}
		};
	}
}

