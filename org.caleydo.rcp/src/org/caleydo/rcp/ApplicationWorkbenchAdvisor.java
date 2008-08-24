package org.caleydo.rcp;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.rcp.views.AGLViewPart;
import org.caleydo.rcp.views.GLParCoordsView;
import org.caleydo.rcp.views.GLPathwayView;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

public class ApplicationWorkbenchAdvisor
	extends WorkbenchAdvisor
{
	private static final String PERSPECTIVE_ID = "org.caleydo.rcp.perspective";

	protected Animator gLAnimator;

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer)
	{	
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
		
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId()
	{
		return PERSPECTIVE_ID;
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer)
	{
		super.initialize(configurer);
		
		configurer.setSaveAndRestore(true);
	}

	@Override
	public void postStartup()
	{
		super.postStartup();
		
		// Check if an early exit should be performed
		if (Application.bDoExit)
		{
			this.getWorkbenchConfigurer().getWorkbench().close();
			return;
		}
		
		openLoadedViews();
	}

	@Override
	public boolean preShutdown()
	{	
		super.preShutdown();
		
		if (gLAnimator.isAnimating())
			gLAnimator.stop();

//		if (caleydoCore != null)
//		{
//			if (caleydoCore.isRunning())
//			{
//				caleydoCore.stop();
//				caleydoCore = null;
//			}
//		}
		
		return true;
	}

	public void openLoadedViews()
	{
		// Initialize all GL views in RCP
		GLCaleydoCanvas canvas;
		int iInstanceNum = 0;
		AGLViewPart viewPart = null;

		gLAnimator = new FPSAnimator(null, 60);

		for(AGLEventListener tmpGLEventListener : GeneralManager.get()
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			canvas = ((AGLEventListener) tmpGLEventListener).getParentGLCanvas();

			// Ignore this event listener if there is no containing canvas view.
			if (canvas == null)
				continue;

			try
			{
				if (tmpGLEventListener instanceof GLParallelCoordinates)
				{
					viewPart = (GLParCoordsView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage().showView(
									GLParCoordsView.ID, Integer.toString(iInstanceNum),
									IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpGLEventListener instanceof GLPathway)
				{
					viewPart = (GLPathwayView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage().showView(
									GLPathwayView.ID, Integer.toString(iInstanceNum),
									IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpGLEventListener instanceof GLRemoteRendering)
				{
					viewPart = (GLRemoteRenderingView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage().showView(
									GLRemoteRenderingView.ID,
									Integer.toString(iInstanceNum),
									IWorkbenchPage.VIEW_ACTIVATE);
				}

				if (viewPart == null)
					continue;

				viewPart.setGLCanvas(canvas);
				viewPart.setViewId(((AGLEventListener) tmpGLEventListener).getID());
				viewPart.createPartControlGL();

				gLAnimator.add(canvas);

				iInstanceNum++;

			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}

		// MessageBox alert = new MessageBox(new Shell(), SWT.OK);
		// alert.setMessage("Start animator!");
		// alert.open();

		gLAnimator.start();
	}
}
