package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.rcp.action.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.RenderContextAction;
import org.caleydo.rcp.action.view.storagebased.UseRandomSamplingAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;


public class GLHeatMapView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLHeatMapView";

	/**
	 * Constructor.
	 */
	public GLHeatMapView()
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
			
		IAction propagateSelectionAction = new PropagateSelectionsAction(iViewID);
		alToolbar.add(propagateSelectionAction);	
		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(iViewID);
		alToolbar.add(switchAxesToPolylinesAction);	
		IAction useRandomSamplingAction = new UseRandomSamplingAction(iViewID);
		alToolbar.add(useRandomSamplingAction);
		IAction toggleRenderContextAction = new RenderContextAction(iViewID);
		alToolbar.add(toggleRenderContextAction);
		IAction clearSelectionsAction = new ClearSelectionsAction(iViewID);
		alToolbar.add(clearSelectionsAction);
	}
	
	@Override
	protected final void fillToolBar()
	{	
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_HEAT_MAP_3D, glCanvas.getID());		
		
		createToolBarItems(iGLEventListenerID);
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}