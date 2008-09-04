package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.action.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.RenderContextAction;
import org.caleydo.rcp.action.view.storagebased.UseRandomSamplingAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.OcclusionPreventionAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.SaveSelectionsAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public class GLParCoordsView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLParCoordsView";

	/**
	 * Constructor.
	 */
	public GLParCoordsView()
	{
		super();
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);		
	}
	
	public static void createToolBarItems(int iViewID)
	{
		alToolbar = new ArrayList<IAction>();
		
		IAction angularBrushingAction = new AngularBrushingAction(iViewID);
		alToolbar.add(angularBrushingAction);	
		IAction occlusionPreventionAction = new OcclusionPreventionAction(iViewID);
		alToolbar.add(occlusionPreventionAction);	
		IAction propagateSelectionAction = new PropagateSelectionsAction(iViewID);
		alToolbar.add(propagateSelectionAction);	
		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(iViewID);
		alToolbar.add(switchAxesToPolylinesAction);	
		IAction toggleRenderContextAction = new RenderContextAction(iViewID);
		alToolbar.add(toggleRenderContextAction);
		IAction useRandomSamplingAction = new UseRandomSamplingAction(iViewID);
		alToolbar.add(useRandomSamplingAction);
		IAction clearSelectionsAction = new ClearSelectionsAction(iViewID);
		alToolbar.add(clearSelectionsAction);
		IAction saveSelectionsAction = new SaveSelectionsAction(iViewID);
		alToolbar.add(saveSelectionsAction);
	}
		
	@Override
	protected final void fillToolBar()
	{
		if (Application.bPathwayViewerMode)
		{
			 MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			 alert.setMessage("Cannot create heat map in pathway viewer mode!");
			 alert.open();
			
			 dispose();
			return;
		}
		
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_PARALLEL_COORDINATES_3D, glCanvas.getID());
	
		createToolBarItems(iGLEventListenerID);
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}