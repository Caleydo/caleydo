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
package org.caleydo.vis.rank.ui.column;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.vis.rank.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.rank.model.ARankColumnModel;

public interface IColumModelLayout {
	int getNumVisibleRows(ARankColumnModel model);
	void layoutRows(ARankColumnModel model, IRowSetter setter, float w, float h);

	/**
	 * @param tableColumnUI
	 * @return
	 */
	boolean hasFreeSpace(ITableColumnUI tableColumnUI);

	/**
	 * @param tableColumnUI
	 * @return
	 */
	VAlign getAlignment(ITableColumnUI tableColumnUI);

	OrderColumnUI getRanker(ARankColumnModel model);

	/**
	 * @return
	 */
	boolean causesReorderingLayouting();

	/**
	 * @return
	 */
	Color getBarOutlineColor();

}
