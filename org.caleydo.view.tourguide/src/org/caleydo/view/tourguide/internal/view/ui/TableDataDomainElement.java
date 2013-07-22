/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.view.tourguide.internal.event.SelectDimensionSelectionEvent;
import org.caleydo.view.tourguide.internal.model.StratificationDataDomainQuery;

public class TableDataDomainElement extends ADataDomainElement {

	public TableDataDomainElement(StratificationDataDomainQuery model) {
		super(model);
	}

	@Override
	public StratificationDataDomainQuery getModel() {
		return (StratificationDataDomainQuery) super.getModel();
	}

	@Override
	protected void createContextMenu(ContextMenuCreator creator) {
		super.createContextMenu(creator);
		Collection<Perspective> dims = getModel().getDimensionPerspectives();
		if (!dims.isEmpty()) {
			Perspective dim = getModel().getDimensionSelection();
			GroupContextMenuItem item = new GroupContextMenuItem("Used Dimension Perspective");
			creator.addContextMenuItem(item);
			for (Perspective d : dims)
				item.add(new GenericContextMenuItem(d.getLabel(), EContextMenuType.CHECK,
						new SelectDimensionSelectionEvent(d).to(this)).setState(d == dim));
			creator.addSeparator();
		}
	}

	@ListenTo(sendToMe = true)
	private void onSelectionDimension(final SelectDimensionSelectionEvent e) {
		Perspective d = e.getDim();
		getModel().setDimensionSelection(d);
	}

	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload) {

	}
}
