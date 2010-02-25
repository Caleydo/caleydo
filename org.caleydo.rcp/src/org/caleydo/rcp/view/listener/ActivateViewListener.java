package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.OpenViewEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivateViewListener
	extends AEventListener<IListenerOwner> {

	@Override
	public void handleEvent(AEvent event) {

		try {
			if (event instanceof LoadPathwayEvent || event instanceof LoadPathwaysByGeneEvent) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					"org.caleydo.view.bucket");
			}
			else if (event instanceof OpenViewEvent)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					((OpenViewEvent) event).getViewType());
		}
		catch (PartInitException e) {
			e.printStackTrace();
			GeneralManager.get().getLogger().log(
				new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Unable to open bucket view.", e));
		}
	}

}
