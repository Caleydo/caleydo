package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;
import java.util.logging.Level;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.caleydo.rcp.action.toolbar.view.remote.CloseOrResetContainedViews;
import org.caleydo.rcp.action.toolbar.view.remote.ToggleConnectionLinesAction;
import org.caleydo.rcp.action.toolbar.view.remote.ToggleLayoutAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLRemoteRenderingView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLRemoteRenderingView";

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public GLRemoteRenderingView()
	{
		super();

		createToolBarItems(-1);
		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		createGLCanvas();

		// Only create parcoords and heatmap if the application is NOT in
		// pathway viewer mode
		if (Application.applicationMode != EApplicationMode.PATHWAY_VIEWER)
		{
			iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_HEAT_MAP_3D,
					-1, true));
			iAlContainedViewIDs.add(createGLEventListener(
					ECommandType.CREATE_GL_PARALLEL_COORDINATES_GENE_EXPRESSION, -1, true));
			// iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_CELL,
			// -1, true));

			// FIXME: This is just a temporary solution to check if glyph view
			// should be added to bucket.
			try
			{
				GeneralManager.get().getIDManager().getInternalFromExternalID(453010);
				iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_GLYPH,
						-1, true));
				iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_GLYPH,
						-1, true));
			}
			catch (IllegalArgumentException e)
			{
				GeneralManager.get().getLogger().log(Level.WARNING,
						"Cannot add glyph to bucket! No glyph data loaded!");
			}
		}

		createGLRemoteEventListener(ECommandType.CREATE_GL_BUCKET_3D, glCanvas.getID(), true,
				iAlContainedViewIDs);
	}

	public static void createToolBarItems(int iViewID)
	{
		alToolbar = new ArrayList<IAction>();

		// IAction takeSnapshotAction = new TakeSnapshotAction(-1);
		// alToolbar.add(takeSnapshotAction);
		IAction closeOrResetContainedViews = new CloseOrResetContainedViews(iViewID);
		alToolbar.add(closeOrResetContainedViews);
		IAction toggleLayoutAction = new ToggleLayoutAction(iViewID);
		alToolbar.add(toggleLayoutAction);
		IAction toggleConnectionLinesAction = new ToggleConnectionLinesAction(iViewID);
		alToolbar.add(toggleConnectionLinesAction);		
	}

	@Override
	public void dispose()
	{
		GLRemoteRendering glRemoteView = ((GLRemoteRendering) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
				iGLEventListenerID));
		
//		glRemoteView.clearAll();

		for (Integer iContainedViewID : iAlContainedViewIDs)
		{
			glRemoteView.removeView(GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iContainedViewID));
		}

		super.dispose();
		
		GeneralManager.get().getViewGLCanvasManager()
				.getConnectedElementRepresentationManager().clearByView(iGLEventListenerID);

		GeneralManager.get().getPathwayManager().resetPathwayVisiblityState();

		// TODO: cleanup data entity searcher view
	}
}