/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.RenameTablePerspectiveEvent;

/**
 * Context menu item to rename a {@link TablePerspective} using a dialog.
 * 
 * @author Christian Partl
 * 
 */
public class RenameTablePerspectiveItem extends AContextMenuItem {

	public RenameTablePerspectiveItem(TablePerspective tablePerspective) {
		setLabel("Rename Table Perspective");

		RenameTablePerspectiveEvent event = new RenameTablePerspectiveEvent(
				tablePerspective);
		event.setSender(this);
		registerEvent(event);
	}

}
