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
package org.caleydo.view.search.api;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.eclipse.jface.action.MenuManager;

/**
 * extension point for defining actions that should come up with the context menu of a search result row
 *
 * @author Samuel Gratzl
 *
 */
public interface ISearchResultActionFactory {
	/**
	 * create the actions related to the given {@link Perspective} of this row
	 *
	 * @param mgr
	 * @param row
	 * @param perspectives
	 * @return whether an item was created or not
	 */
	boolean createPerspectiveActions(MenuManager mgr, IResultRow row, Collection<Perspective> perspectives);

	/**
	 * create general actions based on the {@link IDType}s of this row
	 *
	 * @param mgr
	 * @param row
	 * @return whether an item was created or not
	 */
	boolean createIDTypeActions(MenuManager mgr, IResultRow row);
}
