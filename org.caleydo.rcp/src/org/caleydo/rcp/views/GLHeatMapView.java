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
import org.caleydo.rcp.ApplicationWorkbenchAdvisor;
import org.caleydo.rcp.action.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.UseRandomSamplingAction;
import org.caleydo.rcp.action.view.storagebased.parcoords.RenderContextAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

import com.sun.opengl.util.Animator;


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
		
		createToolBarItems(-1);
	}
	
	private void createStandaloneGLParts()
	{
		IGeneralManager generalManager = GeneralManager.get();
		
		CmdViewCreateRcpGLCanvas cmdCanvas = (CmdViewCreateRcpGLCanvas) generalManager
			.getCommandManager().createCommandByType(ECommandType.CREATE_VIEW_RCP_GLCANVAS);
		cmdCanvas.doCommand();
		
		ArrayList<Integer> iAlSets = new ArrayList<Integer>();
		for (ISet set : generalManager.getSetManager().getAllItems())
		{
			iAlSets.add(set.getID());
		}
		
		CmdCreateGLEventListener cmdView = (CmdCreateGLEventListener) generalManager
			.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_HEAT_MAP_3D);
		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, 
				iAlSets, cmdCanvas.getCreatedObject().getID());
		cmdView.doCommand();
		
		GLCaleydoCanvas glCanvas = cmdCanvas.getCreatedObject();
		setGLData(glCanvas, cmdView.getCreatedObject().getID());
		createPartControlGL();
		
//		Animator animator = ApplicationWorkbenchAdvisor.glAnimator;
//		animator.add(glCanvas);
//		animator.start();
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
		createStandaloneGLParts();
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