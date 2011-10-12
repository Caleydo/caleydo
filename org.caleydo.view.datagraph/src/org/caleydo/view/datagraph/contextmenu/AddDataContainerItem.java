package org.caleydo.view.datagraph.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.datagraph.event.AddDataContainerEvent;

public class AddDataContainerItem extends AContextMenuItem {

	public AddDataContainerItem(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, String dimensionPerspectiveID,
			boolean createDimensionPerspective,
			DimensionVirtualArray dimensionVA, Group group) {

		setLabel("Create Data Container");

		AddDataContainerEvent event = new AddDataContainerEvent(dataDomain,
				recordPerspectiveID, dimensionPerspectiveID,
				createDimensionPerspective, dimensionVA, group);
		event.setSender(this);
		registerEvent(event);
	}

}
