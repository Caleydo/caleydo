package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.core.event.EventPublisher;
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
	protected void onFilterEdit(boolean isStartEditing, Object payload) {
		if (isStartEditing) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					InputDialog d = new InputDialog(null, "Filter Pathways", "Edit Pathway Regex Filter", getModel()
							.getMatches(), null);
					if (d.open() == Window.OK) {
						String v = d.getValue().trim();
						if (v.length() == 0)
							v = "";
						EventPublisher.trigger(new EditDataDomainFilterEvent(v).to(PathwayDataDomainElement.this));
					}
				}
			});
		} else {
			setFilter(payload.toString());
		}
	}

	private void setFilter(String filter) {
		getModel().setMatches(filter);
		setHasFilter(model.hasFilter());
	}
}