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

/**
 * mixin that the column has a annotation
 *
 * @author Samuel Gratzl
 *
 */
public interface IAnnotatedColumnMixin {
	String PROP_DESCRIPTION = "description";
	String PROP_TITLE = "title";

	void editAnnotation(GLElement summary);

	String getDescription();

	String getTitle();

	/**
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * @param description
	 */
	void setDescription(String description);
}
