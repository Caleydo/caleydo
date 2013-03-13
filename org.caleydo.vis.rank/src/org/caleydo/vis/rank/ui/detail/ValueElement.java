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
package org.caleydo.vis.rank.ui.detail;

import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.vis.rank.model.IRow;

/**
 * special element for the columns with extra information
 *
 * @author Samuel Gratzl
 *
 */
public class ValueElement extends PickableGLElement {
	private int animationFlag = 0;
	public ValueElement() {
		setVisibility(EVisibility.VISIBLE);
	}

	/**
	 * returns the row represented by this element of this column
	 *
	 * @return
	 */
	protected final IRow getRow() {
		return getLayoutDataAs(IRow.class, null);
	}

	/**
	 * @return the animationFlag, see {@link #animationFlag}
	 */
	public int getAnimationFlag() {
		return animationFlag;
	}

	/**
	 * @param animationFlag
	 *            setter, see {@link animationFlag}
	 */
	public void setAnimationFlag(int animationFlag) {
		this.animationFlag = animationFlag;
	}
}