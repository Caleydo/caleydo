package org.caleydo.core.plex;

import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.collab.CollabView;
import org.eclipse.swt.widgets.Composite;

public class RcpCollabView
	extends CaleydoRCPViewPart {
	public static final String VIEW_TYPE = "org.caleydo.core.plex.view.collaboration";

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new CollabView(parentComposite);

		((ASWTView) view).draw();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createDefaultSerializedView() {

	}
}
