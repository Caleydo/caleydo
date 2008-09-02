package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.view.camera.EProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.rcp.action.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.UseRandomSamplingAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.OcclusionPreventionAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.RenderContextAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;


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
		
		createToolBarItems(-1);
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_PARALLEL_COORDINATES_3D, glCanvas.getID());
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
	}
		
	@Override
	protected final void fillToolBar()
	{
		if (alToolbar == null)
		{
			createToolBarItems(iGLEventListenerID);
		}
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}