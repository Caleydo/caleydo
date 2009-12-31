package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;

public class ActivateViewListener
	extends AEventListener<IListenerOwner> {

	@Override
	public void handleEvent(AEvent event) {

		// FIXME: just commented out for EUROVIS
		// if (event instanceof LoadPathwayEvent || event instanceof LoadPathwaysByGeneEvent) {
		// try {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
		// RcpGLRemoteRenderingView.ID);
		// }
		// catch (PartInitException e) {
		// e.printStackTrace();
		// GeneralManager.get().getLogger().log(
		// new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Unable to open bucket view.", e));
		// }
		// }
	}

}
