package org.caleydo.view.datagraph.contextmenu;

import java.util.List;

import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class ShowDataContainerInViewsItem extends AContextMenuItem {

	public ShowDataContainerInViewsItem(List<CreateViewItem> createViewItems) {

		setLabel("Show in...");
		for (CreateViewItem item : createViewItems) {
			addSubItem(item);
		}
	}
}
