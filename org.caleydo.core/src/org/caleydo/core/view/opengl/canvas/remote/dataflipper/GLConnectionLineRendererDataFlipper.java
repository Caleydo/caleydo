package org.caleydo.core.view.opengl.canvas.remote.dataflipper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Specialized connection line renderer for data flipper view.
 * 
 * @author Marc Streit
 */
public class GLConnectionLineRendererDataFlipper
	extends AGLConnectionLineRenderer {

	RemoteLevelElement focusElement;
	ArrayList<RemoteLevelElement> stackElementsRight;
	ArrayList<RemoteLevelElement> stackElementsLeft;

	/**
	 * Constructor.
	 */
	public GLConnectionLineRendererDataFlipper(RemoteLevelElement focusElement,
		ArrayList<RemoteLevelElement> stackElementsLeft, ArrayList<RemoteLevelElement> stackElementsRight) {

		super();

		this.focusElement = focusElement;
		this.stackElementsRight = stackElementsRight;
		this.stackElementsLeft = stackElementsLeft;
	}

	@Override
	protected void renderConnectionLines(final GL gl) {

		IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();
		for (Entry<EIDType, ConnectionMap> typeConnections : connectedElementRepManager
			.getTransformedConnectionsByType().entrySet()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;

			EIDType idType = typeConnections.getKey();
			HashMap<Integer, ArrayList<ArrayList<Vec3f>>> viewToPointList =
				hashIDTypeToViewToPointLists.get(idType);

			if (viewToPointList == null) {
				viewToPointList = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
				hashIDTypeToViewToPointLists.put(idType, viewToPointList);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections.getValue().entrySet()) {
				for (SelectedElementRep selectedElementRep : connections.getValue()) {

					if (selectedElementRep.getIDType() != idType)
						throw new IllegalStateException(
							"Current ID Type does not match the selected elemen rep's");

					AGLEventListener glView =
						viewGLCanvasManager.getGLEventListener(selectedElementRep.getSourceViewID());

					if (glView == null) {
						// TODO: investigate! view must not be null here.
						// GeneralManager.get().getLogger().log(Level.WARNING,
						// "View in connection line manager is null!");
						continue;
					}

					RemoteLevelElement remoteLevelElement = glView.getRemoteLevelElement();
					if (remoteLevelElement == null) {
						// ignore views that are not rendered remote
						continue;
					}

					if (remoteLevelElement == stackElementsLeft.get(0)
						|| remoteLevelElement == stackElementsRight.get(0)
						|| remoteLevelElement == focusElement) {
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

			if (viewToPointList.containsKey(focusElement.getContainedElementID())) {
				for (ArrayList<Vec3f> sourceViewPoints : viewToPointList.get(focusElement
					.getContainedElementID())) {
					// Connect point in focus view with points in first view in LEFT stack
					if (viewToPointList.containsKey(stackElementsLeft.get(0).getContainedElementID())) {
						for (ArrayList<Vec3f> targetViewPoints : viewToPointList.get(stackElementsLeft.get(0)
							.getContainedElementID())) {
							renderLine(gl, sourceViewPoints.get(0), targetViewPoints.get(0), 0, new float[] {
									1, 0, 0 });
						}
					}

					// Connect point in focus view with points in first view in RIGHT stack
					if (viewToPointList.containsKey(stackElementsRight.get(0).getContainedElementID())) {
						for (ArrayList<Vec3f> targetViewPoints : viewToPointList.get(stackElementsRight
							.get(0).getContainedElementID())) {
							renderLine(gl, sourceViewPoints.get(0), targetViewPoints.get(0), 0, new float[] {
									1, 0, 0 });
						}
					}
				}
			}

			hashIDTypeToViewToPointLists.clear();
		}
	}

	protected void renderLineBundling(final GL gl, EIDType idType, float[] fArColor) {

		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();

		for (Integer iKey : keySet) {

			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
				// if (alCurrentPoints.size() > 1) {
				// renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				// }
				// else {
				renderLine(gl, alCurrentPoints.get(1), alCurrentPoints.get(0), 0, fArColor);
				// }
			}

			// renderLine(gl, vecViewBundlingPoint, vecCenter, 0, fArColor);
		}
	}

	@Override
	protected void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
		final int iNumberOfLines, float[] fArColor) {

		gl.glTranslatef(-1.5f, -1.5f, 0);
		super.renderLine(gl, vecSrcPoint, vecDestPoint, iNumberOfLines, fArColor);
		gl.glTranslatef(1.5f, 1.5f, 0);
	}
}