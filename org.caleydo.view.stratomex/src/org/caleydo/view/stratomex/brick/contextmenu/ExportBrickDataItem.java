/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.event.ExportBrickDataEvent;

/**
 * Item for exporting the data of a brick.
 *
 * @author Christian Partl
 *
 */
public class ExportBrickDataItem extends AContextMenuItem {

	public ExportBrickDataItem(GLBrick brick, boolean exportIdentifiersOnly) {

		setLabel(exportIdentifiersOnly ? "Export Identifiers" : "Export Data");

		ExportBrickDataEvent event = new ExportBrickDataEvent(brick, exportIdentifiersOnly);
		event.setSender(this);
		registerEvent(event);
	}

}
