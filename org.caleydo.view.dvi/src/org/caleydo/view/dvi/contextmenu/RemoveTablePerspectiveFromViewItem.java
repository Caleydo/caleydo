/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;

/**
 * Item to remove a {@link TablePerspective} from a {@link IMultiTablePerspectiveBasedView}.
 *
 * @author Christian Partl
 *
 */
public class RemoveTablePerspectiveFromViewItem extends AContextMenuItem {

	public RemoveTablePerspectiveFromViewItem(TablePerspective tablePerspective, IMultiTablePerspectiveBasedView view) {
		setLabel("Remove " + tablePerspective.getLabel() + " from " + view.getLabel());

		RemoveTablePerspectiveEvent event = new RemoveTablePerspectiveEvent(tablePerspective, view);
		event.setSender(this);
		registerEvent(event);
	}

}
