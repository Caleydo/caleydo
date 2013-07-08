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
package org.caleydo.view.treemap.layout.algorithm;

import org.caleydo.view.treemap.layout.ATreeMapNode;

/**
 * Interface for layout algorithm.
 * 
 * @author Michael Lafer
 * 
 */

public interface ILayoutAlgorithm {

	public static final int SIMPLE_LAYOUT_ALGORITHM = 0;
	public static final int SQUARIFIED_LAYOUT_ALGORITHM = 1;

	/**
	 * Apply layout on given data.
	 * 
	 * @param tree
	 *            Treemap model without display coordinates (see
	 *            <code>ATreeMapNode</code>).
	 */
	public void layout(ATreeMapNode tree);

}
