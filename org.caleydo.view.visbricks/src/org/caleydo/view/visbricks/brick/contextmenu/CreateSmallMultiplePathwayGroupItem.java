package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.OpenCreateSmallMultiplePathwayGroupDialogEvent;

/**
 * Context menu item for opening a dialog used to create a small multiple
 * pathway dimension group group.
 * 
 * @author Marc Streit
 * 
 */
public class CreateSmallMultiplePathwayGroupItem extends AContextMenuItem {

	public CreateSmallMultiplePathwayGroupItem(DataContainer dimensionGroupDataContainer,
			DimensionPerspective dimensionPerspective) {

		setLabel("Create Small Multiple Pathway Group");

		OpenCreateSmallMultiplePathwayGroupDialogEvent event = new OpenCreateSmallMultiplePathwayGroupDialogEvent(
				dimensionGroupDataContainer, dimensionPerspective);
		event.setSender(this);
		registerEvent(event);
	}
}
