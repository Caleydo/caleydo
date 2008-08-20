package org.caleydo.rcp.progress;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayLoaderThread;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class PathwayLoadingProgressIndicatorAction
	implements IWorkbenchWindowActionDelegate
{
	public void run(IAction action)
	{
		Job job = new Job("Loading pathways...   ")
		{
			@Override
			public IStatus run(IProgressMonitor monitor)
			{
				monitor.beginTask("Loading pathways", 100);
				
				monitor.subTask("KEGG");
				PathwayLoaderThread.loadAllPathwaysByType(GeneralManager.get(), 
						GeneralManager.get().getPathwayManager()
							.getPathwayDatabaseByType(EPathwayDatabaseType.KEGG));
				monitor.worked(50);
				
				monitor.subTask("BioCarta");
				PathwayLoaderThread.loadAllPathwaysByType(GeneralManager.get(), 
						GeneralManager.get().getPathwayManager()
							.getPathwayDatabaseByType(EPathwayDatabaseType.BIOCARTA));				monitor.worked(50);
				
				monitor.done();

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	public void dispose()
	{
		// TODO Auto-generated method stub
	}

	public void init(IWorkbenchWindow window)
	{
		// TODO Auto-generated method stub
	}
	
	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub
	}
}
