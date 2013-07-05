/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.CreateViewFromTablePerspectiveEvent;

public class ShowTablePerspectiveInViewItem extends AContextMenuItem {

	public ShowTablePerspectiveInViewItem(String viewName, String viewType, IDataDomain dataDomain,
			TablePerspective tablePerspective) {

		setLabel(viewName);

		CreateViewFromTablePerspectiveEvent event = new CreateViewFromTablePerspectiveEvent(
				viewType, dataDomain, tablePerspective);
		event.setSender(this);
		registerEvent(event);
	}
}
