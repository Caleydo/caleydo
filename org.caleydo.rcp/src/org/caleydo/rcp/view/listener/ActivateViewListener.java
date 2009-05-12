package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.opengl.GLRemoteRenderingView;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivateViewListener 
extends AEventListener<IListenerOwner> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwayEvent || event instanceof LoadPathwaysByGeneEvent) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					GLRemoteRenderingView.ID);
			}
			catch (PartInitException e) {
				e.printStackTrace();
				GeneralManager.get().getLogger().log(
					new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Unable to open bucket view.", e));
			}
		}
	}

}
