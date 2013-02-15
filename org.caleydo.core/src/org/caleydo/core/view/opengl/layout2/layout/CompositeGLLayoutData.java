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
package org.caleydo.core.view.opengl.layout2.layout;


/**
 * composite variant of a chained layout data object,
 * 
 * @author Samuel Gratzl
 * 
 */
public class CompositeGLLayoutData implements IHasGLLayoutData {
	private final Object[] elems;

	private CompositeGLLayoutData(Object... elems) {
		this.elems = elems;
	}

	public static CompositeGLLayoutData combine(Object... elems) {
		return new CompositeGLLayoutData(elems);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		for (Object elem : elems) {
			T v = GLLayouts.resolveLayoutData(clazz, elem, null);
			if (v != null)
				return v;
		}
		return default_;
	}

}
