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
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

/**
 * an element that can be the parent of another element
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IGLElementParent {
	/**
	 * triggers that the parent should be re layouted
	 */
	void relayout();

	/**
	 * triggers that the parent hierarchy will be repainted
	 */
	void repaint();

	/**
	 * triggers that the parents hierarchy will be repainted just the picking
	 */
	void repaintPick();

	/**
	 * returns the absolute location of the parent
	 * 
	 * @return
	 */
	Vec2f getAbsoluteLocation();

	/**
	 * notification that the child will be moved to another parent
	 * 
	 * @param child
	 */
	void moved(GLElement child);

}
