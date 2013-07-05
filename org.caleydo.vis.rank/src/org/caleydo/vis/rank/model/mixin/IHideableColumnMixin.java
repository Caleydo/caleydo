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
package org.caleydo.vis.rank.model.mixin;

import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;

/**
 * contract that the column can be hidden
 *
 * @author Samuel Gratzl
 *
 */
public interface IHideableColumnMixin extends IDragInfo, IRankColumnModel {
	/**
	 * currently hide able in its state
	 *
	 * @return
	 */
	boolean isHideAble();

	/**
	 * triggers to hide this column
	 *
	 * @return successful?
	 */
	boolean hide();

	boolean isHidden();

	/**
	 * currently destroy able in its state
	 *
	 * @return
	 */
	boolean isDestroyAble();

	/**
	 * triggers to destroy this column
	 *
	 * @return successful?
	 */
	boolean destroy();
}
