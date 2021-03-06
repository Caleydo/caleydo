/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.RenameLabelHolderEvent;

/**
 * Context menu item that can be used to rename any kind of {@link ILabelHolder}
 * using a input dialog.
 *
 * @author Christian Partl
 *
 */
public class RenameLabelHolderItem extends AContextMenuItem {

	public RenameLabelHolderItem(ILabelHolder labelHolder) {
		setLabel("Rename " + labelHolder.getProviderName());

		RenameLabelHolderEvent event = new RenameLabelHolderEvent(labelHolder);
		event.setSender(this);
		registerEvent(event);
	}

}
