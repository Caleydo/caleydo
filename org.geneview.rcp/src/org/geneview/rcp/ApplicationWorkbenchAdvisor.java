package org.geneview.rcp;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.geneview.core.view.jogl.JoglCanvasForwarder;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.rcp.views.AGLViewPart;
import org.geneview.rcp.views.GLHeatmap2DView;
import org.geneview.rcp.views.GLJukeboxPathwayView;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.geneview.rcp.perspective";

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
		
		// Initialize all GL view in RCP
		Iterator<IGLCanvasUser> iterCanvasUser = Application.refGeneralManager.getSingelton()
			.getViewGLCanvasManager().getAllGLCanvasUsers().iterator();
		JoglCanvasForwarder tmpCanvasForwarder;
		IGLCanvasUser tmpCanvasUser;
		int iInstanceNum = 0;
		AGLViewPart viewPart = null;
		
		while (iterCanvasUser.hasNext()) 
		{
			tmpCanvasUser = iterCanvasUser.next();
			tmpCanvasForwarder = tmpCanvasUser.getGLCanvasDirector()
					.getJoglCanvasForwarder();
			
			try 
			{	
				if (tmpCanvasUser.getClass().equals(
						org.geneview.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D.class)) 
				{	
					viewPart = (GLJukeboxPathwayView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLJukeboxPathwayView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}
				else if (tmpCanvasUser.getClass().equals(
						org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn.class))
				{
					viewPart = (GLHeatmap2DView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(GLHeatmap2DView.ID,
								Integer.toString(iInstanceNum), IWorkbenchPage.VIEW_ACTIVATE);
				}
				
				if (viewPart == null)
					continue;
				
				viewPart.setCanvasForwader(tmpCanvasForwarder);
				viewPart.createPartControlGL();
				
				iInstanceNum++;
				
			} catch (CoreException e) {
				e.printStackTrace();
			} 
		}
	}
}
