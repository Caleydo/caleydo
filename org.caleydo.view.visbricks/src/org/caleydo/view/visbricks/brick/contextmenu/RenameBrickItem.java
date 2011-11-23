package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.RenameEvent;

public class RenameBrickItem extends AContextMenuItem {

    public RenameBrickItem(Integer brickID) {

	setLabel("Set Name/ Rename");

	RenameEvent event = new RenameEvent(brickID);
	event.setSender(this);
	registerEvent(event);
    }
}
