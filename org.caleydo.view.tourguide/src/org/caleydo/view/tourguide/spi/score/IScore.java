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

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.PiecewiseLinearMapping;

/**
 * basic abstraction of a score
 *
 * @author Samuel Gratzl
 *
 */
public interface IScore extends ILabelProvider, IFloatFunction<IRow> {
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

	PiecewiseLinearMapping createMapping();
}
