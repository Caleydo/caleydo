package org.caleydo.rcp.view.listener;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.view.OpenMatchmakerViewEvent;
import org.caleydo.core.manager.event.view.OpenViewEvent;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.rcp.action.toolbar.general.StartClusteringAction;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivateViewListener
	extends AEventListener<IListenerOwner> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void handleEvent(final AEvent event) {

		try {
			if ((event instanceof LoadPathwayEvent || event instanceof LoadPathwaysByGeneEvent)
				&& PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("org.caleydo.view.datawindows") == null
				&& PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("org.caleydo.view.dataflipper") == null) {

				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("org.caleydo.view.bucket");
			}
			else if (event instanceof OpenMatchmakerViewEvent) {

				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.caleydo.view.matchmaker");
				}
				catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				CompareGroupsEvent compareGroupsEvent =
					new CompareGroupsEvent(((OpenMatchmakerViewEvent) event).getSetsToCompare());
				compareGroupsEvent.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(compareGroupsEvent);

				GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						StartClusteringAction startClusteringAction = new StartClusteringAction();
						startClusteringAction.setSets(((OpenMatchmakerViewEvent) event).getSetsToCompare());
						startClusteringAction.run();
					}
				});

			}
			else if (event instanceof BookmarkEvent<?>) {

				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("org.caleydo.view.bookmark");

				// TODO only re-trigger event if view is initially opened

				event.setSender(handler);

				// Re-trigger event so that view receives the initial bookmarks
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
			else if (event instanceof OpenViewEvent) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(((OpenViewEvent) event).getViewType());
			}
		}
		catch (PartInitException e) {
			e.printStackTrace();
			Logger.log(new Status(IStatus.INFO, this.toString(), "Unable to open bucket view.", e));
		}
	}
}
