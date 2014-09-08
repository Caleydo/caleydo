package org.caleydo.view.entourage.contextmenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.GeneActions.IGeneActionFactory;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwaysEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLEntourageView;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ListPathwaysForGeneActionFactory implements IGeneActionFactory {

	public ListPathwaysForGeneActionFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<Pair<String, ? extends Runnable>> create(Object id, IDType idType, Object sender) {
		Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(idType, id);
		final LoadPathwaysEvent event = new LoadPathwaysEvent(pathways == null ? new HashSet<PathwayGraph>() : pathways);
		int numPathways = pathways == null ? 0 : pathways.size();
		event.setSender(sender);
		List<Pair<String, ? extends Runnable>> actions = new ArrayList<>();
		actions.add(Pair.make("List Pathways containing " + id + " in Entourage (" + numPathways
				+ " pathways available)", Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					RcpGLEntourageView entourage = (RcpGLEntourageView) activePage.showView(GLEntourage.VIEW_TYPE);
					String eventSpace = entourage.getView().getPathEventSpace();
					event.setEventSpace(eventSpace);
					EventPublisher.trigger(event);
				} catch (PartInitException e) {
					Logger.log(new Status(IStatus.ERROR, "List Pathways containing Gene in Entourage Action",
							"Could not show view" + GLEntourage.VIEW_TYPE));
				}
			}
		})));
		return actions;
	}

}
