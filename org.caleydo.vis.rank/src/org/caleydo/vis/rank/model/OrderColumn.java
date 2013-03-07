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
package org.caleydo.vis.rank.model;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * a column that orders it right elements
 *
 * @author Samuel Gratzl
 *
 */
public class OrderColumn extends ARankColumnModel {

	public OrderColumn(Color color, Color bgColor) {
		super(Color.LIGHT_GRAY, new Color(0.9f, .9f, .9f));
	}

	private OrderColumn(ARankColumnModel copy) {
		super(copy);
	}

	@Override
	public OrderColumn clone() {
		return new OrderColumn(this);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GLElement createValue() {
		return null;
	}

	/**
	 * @return
	 */
	public ColumnRanker getRanker() {
		// TODO Auto-generated method stub
		return null;
	}
}
