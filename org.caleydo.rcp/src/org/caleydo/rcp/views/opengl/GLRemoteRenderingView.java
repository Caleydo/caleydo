package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;
import java.util.logging.Level;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.ser.SerializedHeatMapView;
import org.caleydo.core.view.ser.SerializedParallelCoordinatesView;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.caleydo.rcp.action.toolbar.view.remote.CloseOrResetContainedViews;
import org.caleydo.rcp.action.toolbar.view.remote.ToggleConnectionLinesAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLRemoteRenderingView
	extends AGLViewPart {

	public static final String ID = "org.caleydo.rcp.views.opengl.GLRemoteRenderingView";

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public GLRemoteRenderingView() {
		super();

		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();

		// Only create parcoords and heatmap if the application is NOT in
		// pathway viewer mode
		if (Application.applicationMode != EApplicationMode.PATHWAY_VIEWER) {
/*
			AGLEventListener heatmap = createGLEventListener(ECommandType.CREATE_GL_HEAT_MAP_3D, -1, true); 
			iAlContainedViewIDs.add(heatmap.getID());
			
			AGLEventListener parcoords = createGLEventListener(
				ECommandType.CREATE_GL_PARALLEL_COORDINATES_GENE_EXPRESSION, -1, true);
			iAlContainedViewIDs.add(parcoords.getID());
*/
			// iAlContainedViewIDs.add(createGLEventListener(ECommandType.CREATE_GL_CELL,
			// -1, true));

			// FIXME: This is just a temporary solution to check if glyph view
			// should be added to bucket.
			try {
				GeneralManager.get().getIDManager().getInternalFromExternalID(453010);

				AGLEventListener glyph1 = createGLEventListener(ECommandType.CREATE_GL_GLYPH, -1, true);
				iAlContainedViewIDs.add(glyph1.getID());

				AGLEventListener glyph2 = createGLEventListener(ECommandType.CREATE_GL_GLYPH, -1, true);
				iAlContainedViewIDs.add(glyph2.getID());
			}
			catch (IllegalArgumentException e) {
				GeneralManager.get().getLogger().log(Level.WARNING,
					"Cannot add glyph to bucket! No glyph data loaded!");
			}
		}

		GLRemoteRendering bucket = (GLRemoteRendering) createGLRemoteEventListener(
			ECommandType.CREATE_GL_BUCKET_3D, glCanvas.getID(), true, iAlContainedViewIDs);

		SerializedHeatMapView heatMap = new SerializedHeatMapView();
		bucket.addInitialRemoteView(heatMap);
		SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView();
		bucket.addInitialRemoteView(parCoords);
	}

	@Override
	public void dispose() {
		GLRemoteRendering glRemoteView =
			(GLRemoteRendering) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);

		// glRemoteView.clearAll();

		for (Integer iContainedViewID : iAlContainedViewIDs) {
			glRemoteView.removeView(GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
				iContainedViewID));
		}

		super.dispose();

		GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager().clearByView(
			iViewID);

		GeneralManager.get().getPathwayManager().resetPathwayVisiblityState();

		// TODO: cleanup data entity searcher view
	}
}
