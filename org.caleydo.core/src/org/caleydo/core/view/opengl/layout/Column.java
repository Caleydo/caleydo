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
 * Container for layouts that are stacked on top of each other. The column is a
 * {@link ElementLayout} and contains other ElementLayouts. It can be nested
 * into other containers
 *
 * @author Alexander Lex
 */
public class Column extends ALayoutContainer {

	public enum VAlign {
		LEFT, RIGHT, CENTER
	}

	public Column() {
		super(new ColumnLayout());
	}

	public Column(String layoutName) {
		super(layoutName, new ColumnLayout());
	}

	@Override
	public ColumnLayout getLayout() {
		return (ColumnLayout) super.getLayout();
	}

	public void setVAlign(VAlign vAlign) {
		getLayout().setVAlign(vAlign);
	}

	/**
	 * Set flag signaling whether the content should be rendered from bottom to top (default, true) or from top to
	 * bottom (false)
	 *
	 * @param isBottomUp
	 */
	public void setBottomUp(boolean isBottomUp) {
		getLayout().setBottomUp(isBottomUp);
	}

	/**
	 * <p>
	 * Set flag signaling whether the x-size of the container should be set to
	 * the largest size in y of its sub-elements (true), or if some size
	 * indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a
	 * ratioSize of 1 (which is the default initialization). The reason for this
	 * is that it makes no sense, and catching it prevents errors.
	 */
	@Override
	public void setXDynamic(boolean isXDynamic) {
		super.setXDynamic(isXDynamic);
	}
}
