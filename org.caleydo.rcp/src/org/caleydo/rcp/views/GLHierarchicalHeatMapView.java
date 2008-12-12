package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.EMediatorUpdateType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
import org.caleydo.rcp.action.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.view.storagebased.InFocusAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class GLHierarchicalHeatMapView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLHierarchicalHeatMapView";

	/**
	 * Constructor.
	 */
	public GLHierarchicalHeatMapView()
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
		GLHierarchicalHeatMap hierarchicalHeatMap = (GLHierarchicalHeatMap) GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID);

		alToolbar = new ArrayList<IAction>();

		// TODO: insert icons + action
//		IAction switchFocus = new InFocusAction(iViewID);
//		alToolbar.add(switchFocus);
		
//		IAction switchOrientation = new ChangeOrientationAction(iViewID);
//		alToolbar.add(switchOrientation);
	}

	@Override
	protected final void fillToolBar()
	{
		createGLCanvas();
		int iViewID = createGLEventListener(ECommandType.CREATE_GL_TEXTURE_HEAT_MAP_3D, 
				glCanvas.getID(), false);
		
		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		ArrayList<Integer> iAlReceiverIDs = new ArrayList<Integer>();
		iAlReceiverIDs.add(iViewID);
		
		for (AGLEventListener glEventListener : GeneralManager.get().getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (glEventListener instanceof GLRemoteRendering)
			{
				GeneralManager.get().getEventPublisher().addSendersAndReceiversToMediator(
						GeneralManager.get().getEventPublisher().getItem(
								((GLRemoteRendering) glEventListener).getMediatorID()),
						iAlSenderIDs, iAlReceiverIDs, EMediatorType.SELECTION_MEDIATOR,
						EMediatorUpdateType.MEDIATOR_DEFAULT);
			}
		}

		createToolBarItems(iGLEventListenerID);

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}