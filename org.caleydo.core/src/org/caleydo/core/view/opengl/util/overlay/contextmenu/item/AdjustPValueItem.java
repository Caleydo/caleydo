package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.compare.AdjustPValueEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class AdjustPValueItem
	extends AContextMenuItem {

	public AdjustPValueItem() {
		super();
		setText("Adjust p-Value");
		AdjustPValueEvent event = new AdjustPValueEvent();
		event.setSender(this);
		registerEvent(event);
	}
}
