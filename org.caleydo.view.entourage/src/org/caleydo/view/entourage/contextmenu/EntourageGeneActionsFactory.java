package org.caleydo.view.entourage.contextmenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
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
import org.caleydo.view.entourage.event.AddGeneEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class EntourageGeneActionsFactory implements IGeneActionFactory {

	public EntourageGeneActionsFactory() {
	}

	@Override
	public Collection<Pair<String, ? extends Runnable>> create(Object id, IDType idType, Object sender) {

		IDType humanReadableIDType = idType.getIDCategory().getHumanReadableIDType();
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(humanReadableIDType);

		Set<String> humanReadableIDs = mappingManager.getHumanReadableIDs(idType, id);
		String humanReadableID = id.toString();
		if (humanReadableIDs != null && humanReadableIDs.size() > 0) {
			humanReadableID = humanReadableIDs.iterator().next();
		}

		AEvent event = null;
		event = new AddGeneEvent(id, idType);
		event.setSender(sender);
		List<Pair<String, ? extends Runnable>> actions = new ArrayList<>();
		actions.add(createEntourageAction("Show " + humanReadableID + " Data in Entourage", event));

		Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(idType, id);
		event = new LoadPathwaysEvent(pathways == null ? new HashSet<PathwayGraph>() : pathways);
		int numPathways = pathways == null ? 0 : pathways.size();
		event.setSender(sender);

		actions.add(createEntourageAction("List Pathways containing " + humanReadableID + " in Entourage ("
				+ numPathways
				+ " pathways available)", event));

		return actions;
	}

	private Pair<String, ? extends Runnable> createEntourageAction(String label, final AEvent event) {
		return Pair.make(label, Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					RcpGLEntourageView entourage = (RcpGLEntourageView) activePage.showView(GLEntourage.VIEW_TYPE);
					String eventSpace = entourage.getView().getPathEventSpace();
					event.setEventSpace(eventSpace);
					EventPublisher.trigger(event);
				} catch (PartInitException e) {
					Logger.log(new Status(IStatus.ERROR, "Entourage Gene Action", "Could not show view"
							+ GLEntourage.VIEW_TYPE));
				}
			}
		}));
	}
}
