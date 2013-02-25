package org.caleydo.view.tourguide.internal.view.ui;

import static org.caleydo.core.event.EventListenerManager.triggerEvent;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.event.SelectDimensionSelectionEvent;
import org.caleydo.view.tourguide.internal.view.model.TableDataDomainQuery;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class TableDataDomainElement extends ADataDomainElement {

	public TableDataDomainElement(TableDataDomainQuery model) {
		super(model);
		setHasFilter(true);
	}

	@Override
	public TableDataDomainQuery getModel() {
		return (TableDataDomainQuery) super.getModel();
	}

	@Override
	protected void createContextMenu(ContextMenuCreator creator) {
		creator.addContextMenuItem(new GenericContextMenuItem("Edit Filter", new EditDataDomainFilterEvent().to(this)));
		creator.addSeparator();
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
	private void onEditDataDomainFilter(final EditDataDomainFilterEvent e) {
		if (e.isStartEditing()) {
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
						triggerEvent(new EditDataDomainFilterEvent(v).to(TableDataDomainElement.this));
					}
				}
			});
		} else {
			setFilter(e.getPayload().toString());
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