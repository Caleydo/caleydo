/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.view.IView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.OpenViewEvent;

/**
 * Item to open an existing view, i.e. to bring it to the foreground.
 *
 * @author Christian Partl
 *
 */
public class OpenViewItem extends AContextMenuItem {

	public OpenViewItem(IView view) {

		setLabel("Open view");

		// ARcpGLViewPart viewPart = GeneralManager.get().getViewManager()
		// .getViewPartFromView(view);

		OpenViewEvent event = new OpenViewEvent(view);
		event.setSender(this);
		registerEvent(event);
	}
}
