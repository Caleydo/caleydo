package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.view.model.PathwayDataDomainQuery;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class PathwayDataDomainElement extends ADataDomainElement {

	public PathwayDataDomainElement(PathwayDataDomainQuery model) {
		super(model);
	}

	@Override
	public PathwayDataDomainQuery getModel() {
		return (PathwayDataDomainQuery) super.getModel();
	}


	@Override
	protected void createContextMenu(ContextMenuCreator creator) {
		creator.addContextMenuItem(new GenericContextMenuItem("Edit Filter", new EditDataDomainFilterEvent().to(this)));
		creator.addSeparator();
	}

	@ListenTo(sendToMe = true)
	private void onEditDataDomainFilter(final EditDataDomainFilterEvent e) {
		if (e.isStartEditing()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					InputDialog d = new InputDialog(null, "Filter Pathways", "Edit Pathway Regex Filter", getModel()
							.getMatches(), null);
					if (d.open() == Window.OK) {
						String v = d.getValue().trim();
						if (v.length() == 0)
							v = "";
						EventPublisher.publishEvent(new EditDataDomainFilterEvent(v).to(PathwayDataDomainElement.this));
					}
				}
			});
		} else {
			setFilter(e.getPayload().toString());
		}
	}

	private void setFilter(String filter) {
		getModel().setMatches(filter);
		setHasFilter(model.hasFilter());
	}
}