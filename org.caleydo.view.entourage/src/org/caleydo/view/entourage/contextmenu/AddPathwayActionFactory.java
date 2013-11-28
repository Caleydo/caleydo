package org.caleydo.view.entourage.contextmenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.IAction;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.PathwayActions.IPathwayActionFactory;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.entourage.EEmbeddingID;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLEntourageView;
import org.caleydo.view.entourage.event.AddPathwayEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Action factory to add a pathway to {@link GLEntourage}.
 *
 * @author Christian Partl
 *
 */
public class AddPathwayActionFactory implements IPathwayActionFactory {

	public AddPathwayActionFactory() {
	}

	@Override
	public Collection<Pair<String, ? extends IAction>> create(PathwayGraph pathway, Object sender) {

		final AddPathwayEvent event = new AddPathwayEvent(pathway, EEmbeddingID.PATHWAY_LEVEL1);
		event.setSender(sender);
		List<Pair<String, ? extends IAction>> actions = new ArrayList<>();
		actions.add(Pair.make("Show " + pathway.getTitle() + "in Entourage", new IAction() {

			@Override
			public void perform() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage();
						try {
							RcpGLEntourageView entourage = (RcpGLEntourageView) activePage
									.showView(GLEntourage.VIEW_TYPE);
							String eventSpace = entourage.getView().getPathEventSpace();
							event.setEventSpace(eventSpace);
							EventPublisher.trigger(event);
						} catch (PartInitException e) {
							Logger.log(new Status(IStatus.ERROR, "Show Pathway in Entourage Action",
									"Could not show view" + GLEntourage.VIEW_TYPE));
						}

					}
				});

			}
		}));
		return actions;
	}

}
