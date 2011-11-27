package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent;

/**
 * Context menu item for opening a kaplan meier dimension group.
 * 
 * @author Marc Streit
 * 
 */
public class CreateKaplanMeierSmallMultiplesGroupItem extends AContextMenuItem {

	public CreateKaplanMeierSmallMultiplesGroupItem(
			DataContainer dimensionGroupDataContainer,
			DimensionPerspective dimensionPerspective) {

		setLabel("Create Small Multiple Clinical Data Group");

		OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent event = new OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent(
				dimensionGroupDataContainer, dimensionPerspective);
		event.setSender(this);
		registerEvent(event);
	}
}
