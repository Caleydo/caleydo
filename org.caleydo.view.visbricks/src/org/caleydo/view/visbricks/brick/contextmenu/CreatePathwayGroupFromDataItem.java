package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.OpenCreatePathwayGroupDialogEvent;

public class CreatePathwayGroupFromDataItem extends AContextMenuItem {
	
	public CreatePathwayGroupFromDataItem(IDataDomain dataDomain, RecordVirtualArray recordVA) {
		setLabel("Create Pathway Group From Data");
		
		OpenCreatePathwayGroupDialogEvent event = new OpenCreatePathwayGroupDialogEvent(dataDomain, recordVA);
		event.setSender(this);
		registerEvent(event);
	}

}
