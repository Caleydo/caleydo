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
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.enroute.SelectionColorCalculator;

/**
 * @author Alexander Lex
 * 
 */
public abstract class SelectableRenderer extends LayoutRenderer {

	protected AGLView parentView;
	protected MappedDataRenderer parent;

	SelectionColorCalculator colorCalculator;

	/**
	 * 
	 */
	public SelectableRenderer(AGLView parentView, MappedDataRenderer parent,
			Color baseColor) {
		this.parentView = parentView;
		this.parent = parent;
		colorCalculator = new SelectionColorCalculator(baseColor);
	}

	// protected void calculateColors(ArrayList<SelectionType> selectionTypes) {
	//
	// if (selectionTypes.size() != 0
	// && !selectionTypes.get(0).equals(SelectionType.NORMAL)
	// && selectionTypes.get(0).isVisible()) {
	// topBarColor = selectionTypes.get(0).getColor();
	//
	// if (selectionTypes.size() > 1
	// && !selectionTypes.get(1).equals(SelectionType.NORMAL)
	// && selectionTypes.get(1).isVisible()) {
	// bottomBarColor = selectionTypes.get(1).getColor();
	// } else {
	// bottomBarColor = topBarColor;
	// }
	// }
	// }

//	protected void calculateColors(ArrayList<SelectionType> selectionTypes) {
//
//		if (selectionTypes.size() != 0
//				&& !selectionTypes.get(0).equals(SelectionType.NORMAL)
//				&& selectionTypes.get(0).isVisible()) {
//			topBarColor = selectionTypes.get(0).getColor();
//
//			if (selectionTypes.size() > 1
//					&& !selectionTypes.get(1).equals(SelectionType.NORMAL)
//					&& selectionTypes.get(1).isVisible()) {
//				bottomBarColor = selectionTypes.get(1).getColor();
//			} else {
//				bottomBarColor = topBarColor;
//			}
//		} else {
//			topBarColor = baseColor;
//			bottomBarColor = baseColor;
//		}
//	}
}
