package org.caleydo.view.datagraph.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.datagraph.event.AddDataContainerEvent;

public class AddDataContainerItem extends AContextMenuItem {

	public AddDataContainerItem(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, String dimensionPerspectiveID) {
		
		setLabel("Create Data Container");
		
		AddDataContainerEvent event = new AddDataContainerEvent(dataDomain,
				recordPerspectiveID, dimensionPerspectiveID);
		event.setSender(this);
		registerEvent(event);
	}

}
