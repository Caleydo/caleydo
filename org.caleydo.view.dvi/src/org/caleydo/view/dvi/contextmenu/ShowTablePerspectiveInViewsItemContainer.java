/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.contextmenu;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

/**
 * Item container that is used to show a {@link TablePerspective} in a newly created view.
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

	public ShowTablePerspectiveInViewsItemContainer(TablePerspectiveCreator creator,
			List<Pair<String, String>> finalViewTypes) {

		setLabel("Show " + creator.getLabel() + " in...");

		for (Pair<String, String> viewType : finalViewTypes) {
			addSubItem(new ShowTablePerspectiveInViewItem(viewType.getFirst(), viewType.getSecond(),
					creator.getDataDomain(), creator));
		}
	}
}
