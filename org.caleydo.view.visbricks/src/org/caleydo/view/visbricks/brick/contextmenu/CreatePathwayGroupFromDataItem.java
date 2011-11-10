package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.OpenCreatePathwayGroupDialogEvent;

/**
 * Context menu item for opening a dialog used to create a pathway dimension
 * group.
 * 
 * @author Partl
 * 
 */
public class CreatePathwayGroupFromDataItem extends AContextMenuItem {

	public CreatePathwayGroupFromDataItem(ATableBasedDataDomain dataDomain,
			RecordVirtualArray recordVA, DimensionPerspective dimensionPerspective) {
		setLabel("Create Pathway Group From Data");

		OpenCreatePathwayGroupDialogEvent event = new OpenCreatePathwayGroupDialogEvent(
				dataDomain, recordVA, dimensionPerspective);
		event.setSender(this);
		registerEvent(event);
	}

}
