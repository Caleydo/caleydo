package org.caleydo.core.plex;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.collab.CollabViewRep;
import org.eclipse.swt.widgets.Composite;

public class RcpCollabView
	extends CaleydoRCPViewPart {
	public static final String ID = "org.caleydo.core.plex.CollabView";

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		view = new CollabViewRep(parentComposite);

		if (view instanceof IDataDomainBasedView<?>) {
				((IDataDomainBasedView<IDataDomain>) view).setDataDomain(DataDomainManager
						.get().getDataDomain(serializedView.getDataDomainType()));
		}

		((ASWTView)view).draw();
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
