package org.caleydo.rcp;

import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;
import org.caleydo.rcp.views.AGLViewPart;
import org.caleydo.rcp.views.GLParCoordsView;
import org.caleydo.rcp.views.GLPathway3DView;
import org.caleydo.rcp.views.GLRemoteRendering3DView;
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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#createWorkbenchWindowAdvisor
	 * (org.eclipse.ui.application.IWorkbenchWindowConfigurer)
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer)
	{
		
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
		
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId
	 * ()
	 */
	public String getInitialWindowPerspectiveId()
	{
		return PERSPECTIVE_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui
	 * .application.IWorkbenchConfigurer)
	 */
	public void initialize(IWorkbenchConfigurer configurer)
	{
		super.initialize(configurer);
		
		configurer.setSaveAndRestore(true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#postStartup()
	 */
	public void postStartup()
	{

		super.postStartup();
		
		openLoadedViews();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#preShutdown()
	 */
	public boolean preShutdown()
	{

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

		for(AGLEventListener tmpGLEventListener : Application.generalManager
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			canvas = ((AGLEventListener) tmpGLEventListener).getParentGLCanvas();

			// Ignore this event listener if there is no containing canvas view.
			if (canvas == null)
				continue;

			try
			{
				if (tmpGLEventListener instanceof GLCanvasParCoords3D)
				{
					viewPart = (GLParCoordsView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage().showView(
									GLParCoordsView.ID, Integer.toString(iInstanceNum),
									IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpGLEventListener instanceof GLCanvasPathway3D)
				{
					viewPart = (GLPathway3DView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage().showView(
									GLPathway3DView.ID, Integer.toString(iInstanceNum),
									IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpGLEventListener instanceof GLCanvasRemoteRendering3D)
				{
					viewPart = (GLRemoteRendering3DView) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage().showView(
									GLRemoteRendering3DView.ID,
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
