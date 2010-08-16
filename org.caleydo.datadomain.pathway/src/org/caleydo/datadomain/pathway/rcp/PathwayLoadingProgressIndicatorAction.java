package org.caleydo.datadomain.pathway.rcp;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayLoaderThread;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class PathwayLoadingProgressIndicatorAction
	implements IWorkbenchWindowActionDelegate {
	public void run(IAction action) {
		Job job = new Job("Loading pathways...   ") {
			@Override
			public IStatus run(IProgressMonitor monitor) {

				PreferenceStore prefStore = GeneralManager.get().getPreferenceStore();
				String sPathwayDataSources =
					prefStore.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES);

				if (!sPathwayDataSources.isEmpty()) {

					// Turn on busy mode
					IViewManager viewManager = GeneralManager.get().getViewGLCanvasManager();
					viewManager.requestBusyMode(this);

					monitor.beginTask("Loading pathways", 100);

					if (sPathwayDataSources.contains(EPathwayDatabaseType.KEGG.getName())) {
						monitor.subTask("KEGG");

						PathwayManager.get().createPathwayDatabase(
							EPathwayDatabaseType.KEGG, "data/xml/", "data/images/", "");
						PathwayLoaderThread.loadAllPathwaysByType(PathwayManager.get().getPathwayDatabaseByType(EPathwayDatabaseType.KEGG));
						// monitor.worked(50);
					}

					if (sPathwayDataSources.contains(EPathwayDatabaseType.BIOCARTA.name())) {
						monitor.subTask("BioCarta");

						PathwayManager.get().createPathwayDatabase(
							EPathwayDatabaseType.BIOCARTA, "data/html/", "data/images/", "data/html");
						PathwayLoaderThread.loadAllPathwaysByType(PathwayManager.get().getPathwayDatabaseByType(EPathwayDatabaseType.BIOCARTA));
						// monitor.worked(50);
					}

					PathwayManager.get().notifyPathwayLoadingFinished(true);
					monitor.done();

					viewManager.releaseBusyMode(this);
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
