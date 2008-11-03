package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.rcp.action.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.view.storagebased.RenderContextAction;
import org.caleydo.rcp.action.view.storagebased.ResetViewAction;
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
		GLHeatMap heatMap = (GLHeatMap) GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID);

		alToolbar = new ArrayList<IAction>();

		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(iViewID);
		alToolbar.add(switchAxesToPolylinesAction);
		IAction clearSelectionsAction = new ClearSelectionsAction(iViewID);
		alToolbar.add(clearSelectionsAction);
		IAction resetViewAction = new ResetViewAction(iViewID);
		alToolbar.add(resetViewAction);
		IAction propagateSelectionAction = new PropagateSelectionsAction(iViewID);
		alToolbar.add(propagateSelectionAction);
		
		if (heatMap.isRenderedRemote()
				&& GeneralManager.get().getPreferenceStore().getBoolean(
						PreferenceConstants.HM_LIMIT_REMOTE_TO_CONTEXT))
			return;

		IAction toggleRenderContextAction = new RenderContextAction(iViewID);
		alToolbar.add(toggleRenderContextAction);
		IAction useRandomSamplingAction = new UseRandomSamplingAction(iViewID);
		alToolbar.add(useRandomSamplingAction);
		

	}

	@Override
	protected final void fillToolBar()
	{
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_HEAT_MAP_3D, glCanvas.getID(), true);

		createToolBarItems(iGLEventListenerID);

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}