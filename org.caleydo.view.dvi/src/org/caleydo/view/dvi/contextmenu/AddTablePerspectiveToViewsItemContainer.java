/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.contextmenu;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

/**
 * Item container that is used to add a {@link TablePerspective} to
 * {@link IMultiTablePerspectiveBasedView}s.
 *
 * @author Christian Partl
 *
 */
public class AddTablePerspectiveToViewsItemContainer extends AContextMenuItem {

	public AddTablePerspectiveToViewsItemContainer(List<IMultiTablePerspectiveBasedView> views,
			TablePerspective tablePerspective) {

		setLabel("Add " + tablePerspective.getLabel() + "to...");

		for (IMultiTablePerspectiveBasedView view : views) {

			addSubItem(new AddTablePerspectiveToViewItem(tablePerspective, view));
		}
	}

	public AddTablePerspectiveToViewsItemContainer(List<IMultiTablePerspectiveBasedView> views,
			TablePerspectiveCreator creator) {

		setLabel("Add " + creator.getLabel() + "to...");

		for (IMultiTablePerspectiveBasedView view : views) {

			addSubItem(new AddTablePerspectiveToViewItem(creator, view));
		}
	}

}
