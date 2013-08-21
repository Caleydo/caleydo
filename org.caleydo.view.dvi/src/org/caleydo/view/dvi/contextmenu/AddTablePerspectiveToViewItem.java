/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.dvi.event.CreateTablePerspectiveBeforeAddingEvent;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

/**
 * Item to add a {@link TablePerspective} to a
 * {@link IMultiTablePerspectiveBasedView}.
 *
 * @author Christian Partl
 *
 */
public class AddTablePerspectiveToViewItem extends AContextMenuItem {

	public AddTablePerspectiveToViewItem(TablePerspective tablePerspective,
			IMultiTablePerspectiveBasedView view) {
		setLabel(view.getLabel());

		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(tablePerspective);
		event.to(view).from(this);
		registerEvent(event);
	}

	public AddTablePerspectiveToViewItem(TablePerspectiveCreator creator, IMultiTablePerspectiveBasedView view) {
		setLabel(view.getLabel());

		CreateTablePerspectiveBeforeAddingEvent event = new CreateTablePerspectiveBeforeAddingEvent(creator);
		event.setReceiver(view);
		event.setSender(this);
		registerEvent(event);
	}

}
