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
package org.caleydo.core.data.filter;

import java.util.ArrayList;

/**
 * A MetaFilter is a collection of filters that share for example a common set of settings. An example would
 * be a group of filters on dimensions, that should drop all records below a certain value. With the
 * MetaFilter this value can be set at once for all sub-filters.
 *
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 */
public interface MetaFilter {

	public ArrayList<Filter> getFilterList();
}
