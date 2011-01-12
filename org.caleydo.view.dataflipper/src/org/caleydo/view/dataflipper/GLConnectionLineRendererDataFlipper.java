package org.caleydo.view.dataflipper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.view.ConnectionMap;
import org.caleydo.core.manager.view.SelectedElementRepList;
import org.caleydo.core.manager.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.vislink.VisLinkAnimationStage;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;

/**
 * Specialized connection line renderer for data flipper view.
 * 
 * @author Marc Streit
 */
public class GLConnectionLineRendererDataFlipper extends AGLConnectionLineRenderer {

	RemoteLevelElement focusElement;
	ArrayList<RemoteLevelElement> stackElementsRight;
	ArrayList<RemoteLevelElement> stackElementsLeft;

	/**
	 * Constructor.
	 */
	public GLConnectionLineRendererDataFlipper(RemoteLevelElement focusElement,
			ArrayList<RemoteLevelElement> stackElementsLeft,
			ArrayList<RemoteLevelElement> stackElementsRight) {

		super();

		this.focusElement = focusElement;
		this.stackElementsRight = stackElementsRight;
		this.stackElementsLeft = stackElementsLeft;
	}

	@Override
	protected void renderConnectionLines(final GL2 gl) {

		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(
				1);

		VisLinkAnimationStage connectionLines = new VisLinkAnimationStage();

		ViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();
		for (Entry<IDType, ConnectionMap> typeConnections : connectedElementRepManager
				.getTransformedConnectionsByType().entrySet()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;

			IDType idType = typeConnections.getKey();
			HashMap<Integer, ArrayList<ArrayList<Vec3f>>> viewToPointList = hashIDTypeToViewToPointLists
					.get(idType);

			if (viewToPointList == null) {
				viewToPointList = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
				hashIDTypeToViewToPointLists.put(idType, viewToPointList);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections
					.getValue().entrySet()) {
				for (SelectedElementRep selectedElementRep : connections.getValue()) {

					if (selectedElementRep.getIDType() != idType)
						throw new IllegalStateException(
								"Current ID Type does not match the selected elemen rep's");

					AGLView glView = viewGLCanvasManager.getGLView(selectedElementRep
							.getSourceViewID());

					if (glView == null) {
						// TODO: investigate! view must not be null here.
						// GeneralManager.get().getLogger().log(Level.WARNING,
						// "View in connection line manager is null!");
						continue;
					}

					RemoteLevelElement remoteLevelElement = glView
							.getRemoteLevelElement();
					if (remoteLevelElement == null) {
						// ignore views that are not rendered remote
						continue;
					}

					if (remoteLevelElement == stackElementsLeft.get(0)
							|| remoteLevelElement == stackElementsRight.get(0)
							|| remoteLevelElement == focusElement) {
						int viewID = selectedElementRep.getSourceViewID();

						alPointLists = hashIDTypeToViewToPointLists.get(idType).get(
								viewID);
						if (alPointLists == null) {
							alPointLists = new ArrayList<ArrayList<Vec3f>>();
							viewToPointList.put(viewID, alPointLists);
						}

						alPointLists.add(selectedElementRep.getPoints());
					}
				}
			}

			if (focusElement == null || focusElement.getGLView() == null)
				continue;
			else if (viewToPointList.containsKey(focusElement.getGLView().getID())) {

				for (ArrayList<Vec3f> sourceViewPoints : viewToPointList.get(focusElement
						.getGLView().getID())) {

					// Connect point in focus view with points in first view in
					// LEFT stack
					if (stackElementsLeft.get(0).getGLView() != null
							&& viewToPointList.containsKey(stackElementsLeft.get(0)
									.getGLView().getID())) {
						for (ArrayList<Vec3f> targetViewPoints : viewToPointList
								.get(stackElementsLeft.get(0).getGLView().getID())) {
							// renderLine(gl, sourceViewPoints.get(0),
							// targetViewPoints
							// .get(0), 0, new float[] { 1, 0, 0 });

							ArrayList<Vec3f> line = new ArrayList<Vec3f>(2);
							line.add(sourceViewPoints.get(0));
							line.add(new Vec3f(sourceViewPoints.get(0).x() - 0.5f, 2, 4));
							line.add(targetViewPoints.get(0));

							connectionLines.addLine(line);
						}
					}

					RemoteLevelElement rightElement = stackElementsRight.get(0);
					if (rightElement == null || rightElement.getGLView() == null)
						continue;

					// Connect point in focus view with points in first view in
					// RIGHT stack
					if (stackElementsRight.get(0).getGLView() != null
							&& viewToPointList.containsKey(rightElement.getGLView()
									.getID())) {
						for (ArrayList<Vec3f> targetViewPoints : viewToPointList
								.get(stackElementsRight.get(0).getGLView().getID())) {

							// renderLine(gl, sourceViewPoints.get(0),
							// targetViewPoints
							// .get(0), 0, new float[] { 1, 0, 0 });

							ArrayList<Vec3f> line = new ArrayList<Vec3f>(2);

							line.add(targetViewPoints.get(0));
							// line.add(new Vec3f(sourceViewPoints.get(0).x() +
							// (targetViewPoints.get(0).x() -
							// sourceViewPoints.get(0).x()) / 3f, 2, 4));
							line.add(sourceViewPoints.get(0));

							connectionLines.addLine(line);
						}
					}
				}
			}

			hashIDTypeToViewToPointLists.clear();
		}
		gl.glTranslatef(-1.5f, -1.5f, 0);
		connectionLinesAllViews.add(connectionLines);
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
		gl.glTranslatef(1.5f, 1.5f, 0);
	}
}