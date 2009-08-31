package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.ConnectionMap;
import org.caleydo.core.manager.view.SelectedElementRepList;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLConnectionLineRendererBucket
	extends AGLConnectionLineRenderer {

	/**
	 * Constructor.
	 * 
	 * @param underInteractionLevel
	 * @param stackLevel
	 * @param poolLevel
	 */
	public GLConnectionLineRendererBucket(final RemoteLevel focusLevel, final RemoteLevel stackLevel,
		final RemoteLevel poolLevel) {
		super(focusLevel, stackLevel, poolLevel);
	}

	@Override
	protected void renderConnectionLines(final GL gl) {
		IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();

		for (Entry<EIDType, ConnectionMap> typeConnections : connectedElementRepManager.getTransformedConnectionsByType().entrySet()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;
			
			EIDType idType = typeConnections.getKey();
			HashMap<Integer, ArrayList<ArrayList<Vec3f>>> viewToPointList = hashIDTypeToViewToPointLists.get(idType);
			
			if(viewToPointList == null) {
				viewToPointList = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
				hashIDTypeToViewToPointLists.put(idType, viewToPointList);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections.getValue().entrySet()) {
				for (SelectedElementRep selectedElementRep : connections.getValue()) {
					
					if(selectedElementRep.getIDType() != idType)
						throw new IllegalStateException("Current ID Type does not match the selected elemen rep's");

					AGLEventListener glView =
						viewGLCanvasManager.getGLEventListener(selectedElementRep.getSourceViewID());

					if (glView == null) {
						// TODO: investigate! view must not be null here.
//						GeneralManager.get().getLogger().log(Level.WARNING, "View in connection line manager is null!");
						continue;
					}
					
					RemoteLevelElement remoteLevelElement = glView.getRemoteLevelElement();
					if (remoteLevelElement == null) {
						// ignore views that are not rendered remote
						continue;
					}

					RemoteLevel activeLevel = remoteLevelElement.getRemoteLevel();

					if (activeLevel == stackLevel || activeLevel == focusLevel) {
						int viewID = selectedElementRep.getSourceViewID();
						
						alPointLists = hashIDTypeToViewToPointLists.get(idType).get(viewID);
						if (alPointLists == null) {
							alPointLists = new ArrayList<ArrayList<Vec3f>>();
							viewToPointList.put(viewID, alPointLists);
						}

						alPointLists.add(selectedElementRep.getPoints());
					}
				}
			}
			if (viewToPointList.size() > 1) {
				renderLineBundling(gl, idType, new float[] { 0, 0, 0 });
				hashIDTypeToViewToPointLists.clear();
			}
		}
	}
}