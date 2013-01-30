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
package org.caleydo.view.tourguide.spi;

import java.util.Set;

import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * a factory of a metric score, which have a defined number of possibilities and no "real" parameter
 *
 * @author Samuel Gratzl
 *
 */
public interface IMetricFactory {
	/**
	 * add the adder context menu entries for the provided metrics
	 * 
	 * @param creator
	 * @param visible
	 *            the currently visible columns, i.e. to avoid duplicates
	 * @param receiver
	 *            the receiver to use for the {@link AddScoreColumnEvent} events
	 */
	void addCreateMetricItems(ContextMenuCreator creator, Set<IScore> visible, Object receiver);

	/**
	 * determines whether the given {@link EDataDomainQueryMode} mode is supported
	 *
	 * @param mode
	 * @return
	 */
	boolean supports(EDataDomainQueryMode mode);
}
