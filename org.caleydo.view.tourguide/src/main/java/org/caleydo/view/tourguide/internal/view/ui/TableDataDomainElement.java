/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.view.tourguide.api.model.StratificationDataDomainQuery;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.event.SelectDimensionSelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
			Perspective dim = getModel().getOppositeSelection();
			GroupContextMenuItem item = new GroupContextMenuItem("Used " + getModel().getOppositeIDType() + " View");
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
		getModel().setOppositeSelection(d);
	}

	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload, int minSize) {
		if (isStartEditing) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					new ColumnDataFilterDialog(new Shell()).open();
				}
			});
		} else {
			getModel().setMinSize(minSize);
			setHasFilter(getModel().hasFilter());
		}
	}

	private class ColumnDataFilterDialog extends AFilterDialog {
		public ColumnDataFilterDialog(Shell shell) {
			super(shell, model);
		}

		@Override
		public void create() {
			super.create();
			getShell().setText("Edit Filter of " + model.getDataDomain().getLabel());
			this.setBlockOnOpen(false);
		}

		@Override
		protected void okPressed() {
			EventPublisher.trigger(new EditDataDomainFilterEvent("DUMMY", minSizeUI.getSelection())
					.to(TableDataDomainElement.this));
			super.okPressed();
		}
	}
}
