/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.contextmenu;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;

/**
 * @author Alexander Lex
 *
 */
public class RemoveColumnItem extends AContextMenuItem {

	public RemoveColumnItem(IMultiTablePerspectiveBasedView view, TablePerspective tablePerspective) {

		setLabel("Remove Column");

		RemoveTablePerspectiveEvent event = new RemoveTablePerspectiveEvent(
tablePerspective, view);
		event.setSender(this);
		registerEvent(event);
	}

}
