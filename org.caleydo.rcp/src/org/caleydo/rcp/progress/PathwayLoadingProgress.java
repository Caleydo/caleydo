package org.caleydo.rcp.progress;

import java.lang.reflect.InvocationTargetException;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.specialized.genome.pathway.PathwayLoaderThread;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PathwayLoadingProgress
	implements IRunnableWithProgress
{
	/**
	 * Runs the long running operation
	 * 
	 * @param monitor
	 *            the progress monitor
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException
	{

		// Turn on busy mode
		for (AGLEventListener tmpGLEventListener : GeneralManager.get()
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(true);
		}

		monitor.beginTask("Loading pathways", 100);

		monitor.subTask("KEGG");
		PathwayLoaderThread.loadAllPathwaysByType(GeneralManager.get(), GeneralManager.get()
				.getPathwayManager().getPathwayDatabaseByType(EPathwayDatabaseType.KEGG));
		monitor.worked(50);

		monitor.subTask("BioCarta");
		PathwayLoaderThread.loadAllPathwaysByType(GeneralManager.get(), GeneralManager.get()
				.getPathwayManager().getPathwayDatabaseByType(EPathwayDatabaseType.BIOCARTA));
		monitor.worked(50);

		GeneralManager.get().getPathwayManager().notifyPathwayLoadingFinished(true);

		monitor.done();

		// Turn off busy mode
		for (AGLEventListener tmpGLEventListener : GeneralManager.get()
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(false);
		}
	}

	// public void run()
	// {
	// // Turn on busy mode
	// for (AGLEventListener tmpGLEventListener : GeneralManager.get()
	// .getViewGLCanvasManager().getAllGLEventListeners())
	// {
	// if (!tmpGLEventListener.isRenderedRemote())
	// tmpGLEventListener.enableBusyMode(true);
	// }
	//		
	// monitor.beginTask("Loading pathways", 100);
	//
	// monitor.subTask("KEGG");
	// PathwayLoaderThread.loadAllPathwaysByType(GeneralManager.get(),
	// GeneralManager
	// .get().getPathwayManager().getPathwayDatabaseByType(
	// EPathwayDatabaseType.KEGG));
	// monitor.worked(50);
	//
	// monitor.subTask("BioCarta");
	// PathwayLoaderThread.loadAllPathwaysByType(GeneralManager.get(),
	// GeneralManager
	// .get().getPathwayManager().getPathwayDatabaseByType(
	// EPathwayDatabaseType.BIOCARTA));
	// monitor.worked(50);
	//
	// GeneralManager.get().getPathwayManager().notifyPathwayLoadingFinished(true);
	//
	// monitor.done();
	//		
	// // Turn off busy mode
	// for (AGLEventListener tmpGLEventListener : GeneralManager.get()
	// .getViewGLCanvasManager().getAllGLEventListeners())
	// {
	// if (!tmpGLEventListener.isRenderedRemote())
	// tmpGLEventListener.enableBusyMode(false);
	// }
	//
	// // return Status.OK_STATUS;
	// }
}
