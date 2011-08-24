package org.caleydo.core.view;

import org.caleydo.core.gui.toolbar.action.StartClusteringAction;
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
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivateViewListener
	extends AEventListener<IListenerOwner> {

	@Override
	public void handleEvent(final AEvent event) {

		try {
			if ((event instanceof LoadPathwayEvent || event instanceof LoadPathwaysByGeneEvent)
				&& PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("org.caleydo.view.bucket") == null
				&& PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("org.caleydo.view.datawindows") == null
				&& PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("org.caleydo.view.dataflipper") == null) {

//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//					.showView("org.caleydo.view.bucket");
//
//				event.setSender(handler);
//
//				// Re-trigger event so that the opened view receives it
//				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
			else if (event instanceof OpenMatchmakerViewEvent) {

				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.caleydo.view.matchmaker");

					// Re-trigger event so that view receives it
					GeneralManager.get().getEventPublisher().triggerEvent(event);
				}
				catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				CompareGroupsEvent compareGroupsEvent =
					new CompareGroupsEvent(((OpenMatchmakerViewEvent) event).getTablesToCompare());
				compareGroupsEvent.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(compareGroupsEvent);

//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//
//					@Override
//					public void run() {
//						StartClusteringAction startClusteringAction = new StartClusteringAction();
//						startClusteringAction.setTables(((OpenMatchmakerViewEvent) event)
//							.getTablesToCompare());
//						startClusteringAction.run();
//					}
//				});

			}
			else if (event instanceof BookmarkEvent<?>) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				String viewType = "org.caleydo.view.bookmark";
				boolean viewExists = false;
				for (IViewPart viewPart : page.getViews()) {
					if (!(viewPart instanceof CaleydoRCPViewPart))
						continue;
					ASerializedView serView = ((CaleydoRCPViewPart) viewPart).getSerializedView();

					if (event.getDataDomainID().equals(serView.getDataDomainID())
						&& serView.getViewType().equals(viewType)) {
						viewExists = true;
						break;
					}
				}

				if (!viewExists) {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.caleydo.view.bookmark");
				}

				// TODO only re-trigger event if view is initially opened
				event.setSender(handler);

				// Re-trigger event so that the opened view receives it
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
