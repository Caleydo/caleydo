package org.caleydo.view.matchmaker.contextmenu;

import org.caleydo.core.manager.event.view.matchmaker.AdjustPValueEvent;
import org.caleydo.core.view.contextmenu.ContextMenuItem;

public class AdjustPValueItem
	extends ContextMenuItem {

	public AdjustPValueItem() {

		setLabel("Adjust P-Value");

		AdjustPValueEvent event = new AdjustPValueEvent();
		event.setSender(this);
		registerEvent(event);
	}
}
