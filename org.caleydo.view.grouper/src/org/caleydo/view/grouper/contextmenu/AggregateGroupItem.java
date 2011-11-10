package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.data.datadomain.AggregateGroupEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

/**
 * Context menu item for aggregating groups
 * 
 * @author Alexander Lex
 * 
 */
public class AggregateGroupItem extends AContextMenuItem {

	public AggregateGroupItem(Set<Integer> groups, String dataDomainID) {

		setLabel("Aggregate Group");

		AggregateGroupEvent event = new AggregateGroupEvent(groups);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		registerEvent(event);
	}
}
