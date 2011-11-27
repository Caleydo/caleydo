package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.OpenCreatePathwaySmallMultiplesGroupDialogEvent;

/**
 * Context menu item for opening a dialog used to create a small multiple
 * pathway dimension group.
 * 
 * @author Marc Streit
 * 
 */
public class CreatePathwaySmallMultiplesGroupItem extends AContextMenuItem {

	public CreatePathwaySmallMultiplesGroupItem(
			DataContainer dimensionGroupDataContainer,
			DimensionPerspective dimensionPerspective) {

		setLabel("Create Small Multiple Pathway Group");

		OpenCreatePathwaySmallMultiplesGroupDialogEvent event = new OpenCreatePathwaySmallMultiplesGroupDialogEvent(
				dimensionGroupDataContainer, dimensionPerspective);
		event.setSender(this);
		registerEvent(event);
	}
}
