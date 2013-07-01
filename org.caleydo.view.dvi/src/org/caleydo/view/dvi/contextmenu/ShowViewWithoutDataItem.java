/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.ShowViewWithoutDataEvent;

/**
 * Context menu item that is used to open and show views that do not display
 * data.
 * 
 * @author Christian Partl
 * 
 */
public class ShowViewWithoutDataItem extends AContextMenuItem {

	/**
	 * @param viewID
	 *            ID of the view to show
	 * @param text
	 *            Text that is displayed in the context menu.
	 */
	public ShowViewWithoutDataItem(String viewID, String text) {

		setLabel(text);
		ShowViewWithoutDataEvent event = new ShowViewWithoutDataEvent(viewID);
		event.setSender(this);
		registerEvent(event);
	}
}
