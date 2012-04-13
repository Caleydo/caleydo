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

/**
 * Class managing the entry point into the recursively specified layouts. Also this class is intended to be
 * sub-classed and the {@link #setStaticLayouts()} to be overridden, specifying static layouts if desired.
 * 
 * @author Alexander Lex
 */
public class LayoutConfiguration {

	protected ElementLayout baseElementLayout;

	/**
	 * <p>
	 * Sets the static layouts that may be specified in a sub-class.
	 * </p>
	 * <p>
	 * For static layouts (for example for a particular view) the layouting should be done in a sub-class of
	 * ATemplate in this method. If the layout is generated dynamically, this typically should be empty.
	 * </p>
	 */
	public void setStaticLayouts() {
	}

	/**
	 * @return the baseElementLayout, see {@link #baseElementLayout}
	 */
	public ElementLayout getBaseElementLayout() {
		return baseElementLayout;
	}

}
