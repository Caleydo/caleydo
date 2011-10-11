package org.caleydo.view.datagraph.contextmenu;

import org.caleydo.core.data.container.ADataContainer;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.datagraph.event.CreateViewFromDataContainerEvent;

public class CreateViewItem extends AContextMenuItem {

	public CreateViewItem(String viewName, String viewType,
			IDataDomain dataDomain, ADataContainer dataContainer) {

		setLabel("Show in " + viewName);

		CreateViewFromDataContainerEvent event = new CreateViewFromDataContainerEvent(
				viewType, dataDomain, dataContainer);
		event.setSender(this);
		registerEvent(event);
	}
}
