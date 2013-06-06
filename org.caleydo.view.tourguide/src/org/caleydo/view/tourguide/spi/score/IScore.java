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
package org.caleydo.view.tourguide.spi.score;

import java.awt.Color;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * basic abstraction of a score
 *
 * @author Samuel Gratzl
 *
 */
public interface IScore extends ILabeled {
	/**
	 * determines whether the current score support the given {@link EDataDomainQueryMode} mode
	 *
	 * @param mode
	 * @return
	 */
	boolean supports(EDataDomainQueryMode mode);

	/**
	 * @return
	 */
	String getAbbreviation();

	String getDescription();

	Color getColor();

	Color getBGColor();

	/**
	 * factory method for creating a {@link PiecewiseMapping} which is used by the mapping editor of the column
	 * 
	 * @return
	 */
	PiecewiseMapping createMapping();

	/**
	 * computes the score of the given {@link IComputeElement}
	 * 
	 * @param elem
	 * @param g
	 *            optional depending whether this kind of score is based on groups or not
	 * @return the score or {@link Float#NaN} otherwise
	 */
	float apply(IComputeElement elem, Group g);
}
