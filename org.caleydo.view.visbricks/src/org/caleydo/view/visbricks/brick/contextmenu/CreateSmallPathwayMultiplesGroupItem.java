package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.OpenCreateSmallPathwayMultiplesGroupDialogEvent;

/**
 * Context menu item for opening a dialog used to create a small multiple
 * pathway dimension group group.
 * 
 * @author Marc Streit
 * 
 */
public class CreateSmallPathwayMultiplesGroupItem extends AContextMenuItem {

	public CreateSmallPathwayMultiplesGroupItem(DataContainer dimensionGroupDataContainer,
			DimensionPerspective dimensionPerspective) {

		setLabel("Create Small Multiple Pathway Group");

		OpenCreateSmallPathwayMultiplesGroupDialogEvent event = new OpenCreateSmallPathwayMultiplesGroupDialogEvent(
				dimensionGroupDataContainer, dimensionPerspective);
		event.setSender(this);
		registerEvent(event);
	}
}
