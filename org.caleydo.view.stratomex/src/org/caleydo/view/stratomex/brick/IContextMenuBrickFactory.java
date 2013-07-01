/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * extension point interface for creating context menu entries for the gl brick used for triggering scoring
 *
 * @author Samuel Gratzl
 *
 */
public interface IContextMenuBrickFactory {
	public static final String EXTENSION_ID = "org.caleydo.view.stratomex.brick.contextmenu";

	public Iterable<AContextMenuItem> createGroupEntries(BrickColumn referenceColumn, TablePerspective groupTable);

	public Iterable<AContextMenuItem> createStratification(BrickColumn referenceColumn);

}
