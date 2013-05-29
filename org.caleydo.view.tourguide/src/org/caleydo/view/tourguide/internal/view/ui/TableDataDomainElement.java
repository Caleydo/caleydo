/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.event.SelectDimensionSelectionEvent;
import org.caleydo.view.tourguide.internal.model.StratificationDataDomainQuery;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class TableDataDomainElement extends ADataDomainElement {

	public TableDataDomainElement(StratificationDataDomainQuery model) {
		super(model);
		setHasFilter(true);
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
			if (dim == null)
				dim = dims.iterator().next();
			GroupContextMenuItem item = new GroupContextMenuItem("Used Dimension Perspective");
			creator.addContextMenuItem(item);
			for (Perspective d : dims)
				item.add(new GenericContextMenuItem(d.getLabel(), EContextMenuType.CHECK,
						new SelectDimensionSelectionEvent(d).to(this)).setState(d == dim));
			creator.addSeparator();
		}
	}

	@ListenTo(sendToMe = true)
	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload) {
		if (isStartEditing) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					InputDialog d = new InputDialog(null, "Filter Table (* = wildcard)",
							"Edit Record Perspective Filter",
							getModel().getMatches(), null);
					if (d.open() == Window.OK) {
						String v = d.getValue().trim();
						if (v.length() == 0)
							v = "";
						EventPublisher.trigger(new EditDataDomainFilterEvent(v).to(TableDataDomainElement.this));
					}
				}
			});
		} else {
			setFilter(payload.toString());
		}
	}

	private void setFilter(String filter) {
		getModel().setMatches(filter);
	}

	@ListenTo(sendToMe = true)
	private void onSelectionDimension(final SelectDimensionSelectionEvent e) {
		Perspective d = e.getDim();
		getModel().setDimensionSelection(d);
	}
}