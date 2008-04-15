package org.caleydo.rcp;

import java.util.Iterator;

import javax.media.opengl.GLEventListener;

import org.caleydo.core.view.jogl.JoglCanvasForwarder;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering3D;
import org.caleydo.rcp.views.AGLViewPart;
import org.caleydo.rcp.views.GLHeatmap2DView;
import org.caleydo.rcp.views.GLJukeboxPathwayView;
import org.caleydo.rcp.views.GLParCoordsView;
import org.caleydo.rcp.views.GLPathway3DView;
import org.caleydo.rcp.views.GLRemoteRendering3DView;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

public class ApplicationWorkbenchAdvisor 
extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.caleydo.rcp.perspective";
	
	protected Animator gLAnimator;

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {

		super.initialize(configurer);

		configurer.setSaveAndRestore(true);
	}
	
	@Override
	public void postStartup() {

		super.postStartup();
		
		openLoadedViews();
	}
	
	protected void openLoadedViews() {
		
		// Initialize all GL views in RCP
		Iterator<GLEventListener> iterGLEventListener = Application.refGeneralManager.getSingelton()
			.getViewGLCanvasManager().getAllGLEventListeners().iterator();
		
		JoglCanvasForwarder tmpCanvasForwarder;
		GLEventListener tmpGLEventListener;
		int iInstanceNum = 0;
		AGLViewPart viewPart = null;
		
		gLAnimator = new FPSAnimator(null, 60);
		
		while (iterGLEventListener.hasNext()) 
		{
			tmpGLEventListener = iterGLEventListener.next();
			tmpCanvasForwarder = ((AGLCanvasUser)tmpGLEventListener).getParentGLCanvas();
			
			// Ignore this event listener if there is no containing canvas view.
			if (tmpCanvasForwarder == null)
				continue;
			
			try 
			{	
				if (tmpGLEventListener.getClass().equals(
						org.caleydo.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D.class))
				{	
					viewPart = (GLJukeboxPathwayView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLJukeboxPathwayView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpGLEventListener.getClass().equals(
						org.caleydo.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn.class))
				{
					viewPart = (GLHeatmap2DView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLHeatmap2DView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpGLEventListener.getClass().equals(
						org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D.class))
				{
					viewPart = (GLParCoordsView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLParCoordsView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}			
				else if (tmpGLEventListener.getClass().equals(
						org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D.class))
				{
					viewPart = (GLPathway3DView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLPathway3DView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}	
				else if (tmpGLEventListener.getClass().equals(
						org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering3D.class))
				{
					viewPart = (GLRemoteRendering3DView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLRemoteRendering3DView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}	
				
				if (viewPart == null)
					continue;
				
				viewPart.setCanvasForwader(tmpCanvasForwarder);
				viewPart.createPartControlGL();
				
				gLAnimator.add(tmpCanvasForwarder);
				
				iInstanceNum++;
				
			} catch (CoreException e) {
				e.printStackTrace();
			} 
		}
		
		gLAnimator.start();
	}
}
