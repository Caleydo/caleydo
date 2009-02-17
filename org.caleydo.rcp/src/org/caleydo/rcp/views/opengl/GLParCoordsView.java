package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.caleydo.rcp.action.toolbar.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.RenderContextAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ResetViewAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.UseRandomSamplingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.OcclusionPreventionAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.ResetAxisSpacingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.SaveSelectionsAction;
import org.eclipse.jface.action.IAction;
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

		if (Application.applicationMode == EApplicationMode.PATHWAY_VIEWER)
		{
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create parallel coordinates in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_PARALLEL_COORDINATES_GENE_EXPRESSION,
				glCanvas.getID(), true);
	}

	public static void createToolBarItems(int iViewID)
	{
		GLParallelCoordinates pcs = (GLParallelCoordinates) GeneralManager.get()
				.getViewGLCanvasManager().getGLEventListener(iViewID);

		alToolbar = new ArrayList<IAction>();

		// all pc views
		IAction angularBrushingAction = new AngularBrushingAction(iViewID);
		alToolbar.add(angularBrushingAction);
		IAction occlusionPreventionAction = new OcclusionPreventionAction(iViewID);
		alToolbar.add(occlusionPreventionAction);
		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(iViewID);
		alToolbar.add(switchAxesToPolylinesAction);
		IAction clearSelectionsAction = new ClearSelectionsAction(iViewID);
		alToolbar.add(clearSelectionsAction);
		IAction saveSelectionsAction = new SaveSelectionsAction(iViewID);
		alToolbar.add(saveSelectionsAction);
		IAction resetViewAction = new ResetViewAction(iViewID);
		alToolbar.add(resetViewAction);
		IAction propagateSelectionAction = new PropagateSelectionsAction(iViewID);
		alToolbar.add(propagateSelectionAction);
		IAction resetAxisSpacing = new ResetAxisSpacingAction(iViewID);
		alToolbar.add(resetAxisSpacing);

		// only if standalone or explicitly requested
		if (pcs.isRenderedRemote()
				&& GeneralManager.get().getPreferenceStore().getBoolean(
						PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT))
			return;

		IAction toggleRenderContextAction = new RenderContextAction(iViewID);
		alToolbar.add(toggleRenderContextAction);

		IAction useRandomSamplingAction = new UseRandomSamplingAction(iViewID);
		alToolbar.add(useRandomSamplingAction);

	}
}