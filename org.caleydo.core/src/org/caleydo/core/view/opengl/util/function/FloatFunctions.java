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
package org.caleydo.core.view.opengl.util.function;


/**
 * factory for various {@link IFloatFunction}s
 *
 * @author Samuel Gratzl
 *
 */
public final class FloatFunctions {
	private FloatFunctions() {

	}

	/**
	 * identity mapping
	 */
	public static final AFloatFunction IDENTITY = new AFloatFunction() {
		@Override
		public float apply(float in) {
			return in;
		}
	};

	/**
	 * clamps the given value between 0 and 1
	 */
	public static final AFloatFunction CLAMP01 = new AFloatFunction() {
		@Override
		public float apply(float in) {
			return (in < 0 ? 0 : (in > 1 ? 1 : in));
		}
	};
}

