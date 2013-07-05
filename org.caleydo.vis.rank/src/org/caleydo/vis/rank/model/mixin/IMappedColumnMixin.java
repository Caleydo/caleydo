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

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.vis.rank.model.IRow;

/**
 * contract that the column has an underlying mapping, which transform the values
 *
 * @author Samuel Gratzl
 *
 */
public interface IMappedColumnMixin extends IRankColumnModel {
	String PROP_MAPPING = "mapping";

	/**
	 * triggers to open the edit dialog given its summary element
	 *
	 * @param summary
	 * @param context
	 */
	void editMapping(GLElement summary, IGLElementContext context);

	/**
	 * returns a representation of the raw value
	 *
	 * @param row
	 * @return
	 */
	String getRawValue(IRow row);

	/**
	 * @return whether an different style should be used for renderign the values to indicate that the mapping is
	 *         complex
	 */
	boolean isComplexMapping();
}
