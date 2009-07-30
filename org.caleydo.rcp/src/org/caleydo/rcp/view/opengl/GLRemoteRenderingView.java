package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedGlyphView;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.eclipse.core.runtime.Status;
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
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedRemoteRenderingView serializedView = new SerializedRemoteRenderingView();
		serializedView.setViewGUIID(getViewGUIID());
		
		serializedView.setPathwayTexturesEnabled(true);
		serializedView.setNeighborhoodEnabled(true);
		serializedView.setGeneMappingEnabled(true);
		serializedView.setConnectionLinesEnabled(true);
		
		ArrayList<ASerializedView> remoteViews = new ArrayList<ASerializedView>();

		if (Application.applicationMode != EApplicationMode.GENE_EXPRESSION_PATHWAY_VIEWER) {

			// FIXME: This is just a temporary solution to check if glyph view
			// should be added to bucket.
			try {
				GeneralManager.get().getIDManager().getInternalFromExternalID(453010);
				SerializedGlyphView glyph1 = new SerializedGlyphView();
				remoteViews.add(glyph1);			
				SerializedGlyphView glyph2 = new SerializedGlyphView();
				remoteViews.add(glyph2);			
			}
			catch (IllegalArgumentException e) {
				GeneralManager.get().getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					"Cannot add glyph to bucket! No glyph data loaded!"));
			}

			SerializedHeatMapView heatMap = new SerializedHeatMapView();
			remoteViews.add(heatMap);
			SerializedParallelCoordinatesView parCoords = new SerializedParallelCoordinatesView();
			remoteViews.add(parCoords);			
		}
		
		ArrayList<ASerializedView> focusLevel = new ArrayList<ASerializedView>();
		if (remoteViews.size() > 0) {
			focusLevel.add(remoteViews.remove(0));
		}
		serializedView.setFocusViews(focusLevel);
		serializedView.setStackViews(remoteViews);
		
		return serializedView;
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

	@Override
	public String getViewGUIID() {
		return ID;
	}
}
