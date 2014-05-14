package org.caleydo.view.entourage.contextmenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.GeneActions.IGeneActionFactory;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLEntourageView;
import org.caleydo.view.entourage.event.AddGeneEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class AddGeneActionFactory implements IGeneActionFactory {

	public AddGeneActionFactory() {
	}

	@Override
	public Collection<Pair<String, ? extends Runnable>> create(Object id, IDType idType, Object sender) {

		final AddGeneEvent event = new AddGeneEvent(id, idType);
		event.setSender(sender);
		List<Pair<String, ? extends Runnable>> actions = new ArrayList<>();
		actions.add(Pair.make("Show " + id + " in Entourage", Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					RcpGLEntourageView entourage = (RcpGLEntourageView) activePage.showView(GLEntourage.VIEW_TYPE);
					String eventSpace = entourage.getView().getPathEventSpace();
					event.setEventSpace(eventSpace);
					EventPublisher.trigger(event);
				} catch (PartInitException e) {
					Logger.log(new Status(IStatus.ERROR, "Show Gene in Entourage Action", "Could not show view"
							+ GLEntourage.VIEW_TYPE));
				}
			}
		})));
		return actions;
	}

}
