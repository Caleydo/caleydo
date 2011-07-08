package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.manager.datadomain.AggregateGroupEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

/**
 * Context menu item for aggregating groups
 * 
 * @author Alexander Lex
 * 
 */
public class AggregateGroupItem extends AContextMenuItem {

	public AggregateGroupItem(Set<Integer> groups) {
		super();
		setText("Aggregate Group");
		AggregateGroupEvent event = new AggregateGroupEvent(groups);
		event.setSender(this);
		registerEvent(event);
	}
}
