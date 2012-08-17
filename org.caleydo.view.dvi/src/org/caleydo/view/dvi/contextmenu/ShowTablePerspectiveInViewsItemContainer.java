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
package org.caleydo.view.dvi.contextmenu;

import java.util.List;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

/**
 * Item container that is used to show a {@link TablePerspective} in a newly
 * created view.
 * 
 * @author Christian Partl
 * 
 */
public class ShowTablePerspectiveInViewsItemContainer extends AContextMenuItem {

	public ShowTablePerspectiveInViewsItemContainer(TablePerspective tablePerspective,
			List<Pair<String, String>> finalViewTypes) {

		setLabel("Show " + tablePerspective.getLabel() + " in...");

		for (Pair<String, String> viewType : finalViewTypes) {
			addSubItem(new ShowTablePerspectiveInViewItem(viewType.getFirst(), viewType.getSecond(),
					tablePerspective.getDataDomain(), tablePerspective));
		}
	}
}
