package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GraphDrawingUtils;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.vislink.VisLinkAnimationStage;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Michael Wittmayer
 */

public class GLConsecutiveConnectionGraphDrawing
	extends GraphDrawingUtils {

	final int PREDECESSOR = 0;
	final int SUCCESSOR = 1;
	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	ArrayList<Vec3f> controlPoints = new ArrayList<Vec3f>();
	private ArrayList<ArrayList<Vec3f>> heatmapPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> heatmapSuccessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsSuccessor = new ArrayList<ArrayList<Vec3f>>();
	ArrayList<Integer> allviews = new ArrayList<Integer>();
	private int heatMapID = getSpecialViewID(HEATMAP);
	private int parCoordID = getSpecialViewID(PARCOORDS);
	private int gapPosition = -1;
	private int gapSuccessorID = -1;
	private boolean multiplePoints = false;
	private boolean startingAtCenter = false;

	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 */
	public GLConsecutiveConnectionGraphDrawing(final RemoteLevel focusLevel, final RemoteLevel stackLevel) {
		super(focusLevel, stackLevel);

		this.focusLevel = focusLevel;
		this.stackLevel = stackLevel;
		// setControlPoints();
	}

	/**
	 * initializing the list of control points
	 */
	/*
	 * private void setControlPoints() { controlPoints.add(new Vec3f(-2, -2, 0)); controlPoints.add(new
	 * Vec3f(-2, 2, 0)); controlPoints.add(new Vec3f(2, 2, 0)); controlPoints.add(new Vec3f(2, -2, 0));
	 * controlPoints.add(new Vec3f(-2, 0, 0)); controlPoints.add(new Vec3f(0, 2, 0)); controlPoints.add(new
	 * Vec3f(2, 0, 0)); controlPoints.add(new Vec3f(0, -2, 0)); controlPoints.add(new Vec3f(0, 0, 0)); }
	 */

	protected void renderLineBundling(final GL gl, EIDType idType, float[] fArColor) {

		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();
		gapSuccessorID = -1;
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		ArrayList<ArrayList<Vec3f>> heatMapPoints = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> parCoordsPoints = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Integer> viewsToBeVisited = null;

		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();

		int heatMapID = getSpecialViewID(HEATMAP);
		int parCoordID = getSpecialViewID(PARCOORDS);

		for (Integer iKey : keySet) {
			if (iKey.equals(heatMapID))
				heatMapPoints = hashIDTypeToViewToPointLists.get(idType).get(iKey);
			else if (iKey.equals(parCoordID))
				parCoordsPoints = hashIDTypeToViewToPointLists.get(idType).get(iKey);
			else
				hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(
					iKey)));
		}

		// getting list of views that belong to the current graph, sorted by sequence of visiting
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null
			&& focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID) {
			viewsToBeVisited = getViewsOfCurrentPathStartingAtFocus(hashViewToCenterPoint);
			renderFromCenter(gl, idType, hashViewToCenterPoint, viewsToBeVisited, heatMapPoints,
				parCoordsPoints);
		}
		else {
			viewsToBeVisited = getViewsOfCurrentPathStartingAtStack(hashViewToCenterPoint);
			renderFromStack(gl, idType, hashViewToCenterPoint, viewsToBeVisited, heatMapPoints,
				parCoordsPoints);
		}
	}

	/**
	 * rendering the connection graph if the active view is located at one of the four stack elements
	 * 
	 * @param gl
	 *            the GL object
	 * @param idType
	 *            type of genome data
	 * @param hashViewToCenterPoint
	 *            list of center points of views excluding heatmap and parcoords
	 * @param viewsToBeVisited
	 *            list the holds the views that belong to the current connection graph and in which order they
	 *            have to be visited
	 * @param heatMapPoints
	 *            list of heatmap points from which the best points have to be chosen from
	 * @param parCoordsPoints
	 *            list of parcoord points from which the best points have to be chosen from
	 */
	private void renderFromStack(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<Integer> viewsToBeVisited, ArrayList<ArrayList<Vec3f>> heatMapPoints,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints) {

		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;
		startingAtCenter = false;

		if (gapPosition == -1) {
			heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
			heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
			parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
			parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
		}
		else {
			if (heatMapID == activeViewID) {
				heatMapPredecessorID = viewsToBeVisited.get(viewsToBeVisited.size() - 1);
				heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
				parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
				if (viewsToBeVisited.get(viewsToBeVisited.size() - 1) == parCoordID)
					parCoordsSuccessorID = -1;
				else
					parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
			}
			else if (parCoordID == activeViewID) {
				parCoordsPredecessorID = viewsToBeVisited.get(viewsToBeVisited.size() - 1);
				parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
				heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
				if (viewsToBeVisited.get(viewsToBeVisited.size() - 1) == heatMapID)
					heatMapSuccessorID = -1;
				else
					heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);

			}
			else {
				if (gapPosition == 4) {
					if (heatMapID != -1)
						heatMapPredecessorID = activeViewID;
					if (parCoordID != -1)
						parCoordsPredecessorID = activeViewID;
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLHeatMap) {
						heatMapPredecessorID = activeViewID;
						heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
						if (parCoordID != -1) {
							parCoordsPredecessorID = heatMapID;
							parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
						}
					}
					else if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLParallelCoordinates) {
						parCoordsPredecessorID = activeViewID;
						parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
						if (heatMapID != -1) {
							heatMapPredecessorID = parCoordID;
							heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
						}
					}
					else if (heatMapID != -1) {
						heatMapPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
						if (parCoordID != -1) {
							parCoordsPredecessorID =
								focusLevel.getElementByPositionIndex(0).getGLView().getID();
							parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
						}
					}
					else if (parCoordID != -1) {
						parCoordsPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
						if (heatMapID != -1) {
							heatMapPredecessorID =
								focusLevel.getElementByPositionIndex(0).getGLView().getID();
							heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
						}
					}

				}
			}
		}

		// safety check if something went wrong while updating points list of heatmap or parcoords
		if ((heatMapPoints.size() == 0 && heatMapID != -1)
			|| (parCoordsPoints.size() == 0 && parCoordID != -1))
			return;

		if (heatMapPoints.size() <= 6 || parCoordsPoints.size() <= 3)
			getSinglePointsFromHeatMapAndParCoords(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints,
				parCoordsPredecessorID, parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint);
		else
			getMultiplePointsFromHeatMapAndParCoords(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints,
				parCoordsPredecessorID, parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint);

		renderLines(gl, idType, heatMapPredecessorID, heatMapSuccessorID, parCoordsPredecessorID,
			parCoordsSuccessorID, viewsToBeVisited, hashViewToCenterPoint);
	}

	/**
	 * rendering the connection graph if the active view is located at the focus level of the bucket
	 * 
	 * @param gl
	 *            the GL object
	 * @param idType
	 *            type of genome data
	 * @param hashViewToCenterPoint
	 *            list of center points of views excluding heatmap and parcoords
	 * @param viewsToBeVisited
	 *            list the holds the views that belong to the current connection graph and in which order they
	 *            have to be visited
	 * @param heatMapPoints
	 *            list of heatmap points from which the best points have to be chosen from
	 * @param parCoordsPoints
	 *            list of parcoord points from which the best points have to be chosen from
	 */
	private void renderFromCenter(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<Integer> viewsToBeVisited, ArrayList<ArrayList<Vec3f>> heatMapPoints,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints) {

		startingAtCenter = true;
		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;

		if (gapSuccessorID == -1) {
			heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
			heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
			parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
			parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
		}
		else {
			if (heatMapID == activeViewID) {
				heatMapPredecessorID = getNextView(viewsToBeVisited, heatMapID);
				heatMapSuccessorID = gapSuccessorID;
				if (parCoordID == gapSuccessorID)
					parCoordsPredecessorID = heatMapID;
				else
					parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
				if (beforeGap(parCoordID))
					parCoordsSuccessorID = -1;
				else
					parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
			}
			else if (parCoordID == activeViewID) {
				if (gapSuccessorID == heatMapID)
					heatMapPredecessorID = parCoordID;
				else
					heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
				if (beforeGap(heatMapID))
					heatMapSuccessorID = -1;
				else
					heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
				parCoordsPredecessorID = getNextView(viewsToBeVisited, parCoordID);
				parCoordsSuccessorID = gapSuccessorID;
			}
			else {
				if (heatMapID != -1)
					heatMapPredecessorID = viewsToBeVisited.get(0);
				if (parCoordID != -1)
					parCoordsPredecessorID = viewsToBeVisited.get(0);

				if (beforeGap(heatMapID))
					heatMapSuccessorID = -1;
				else
					heatMapSuccessorID = getNextView(viewsToBeVisited, heatMapID);
				if (beforeGap(parCoordID))
					parCoordsSuccessorID = -1;
				else
					parCoordsSuccessorID = getNextView(viewsToBeVisited, parCoordID);
			}
		}

		// safety check if something went wrong while updating points list of heatmap or parcoords
		if ((heatMapPoints.size() == 0 && heatMapID != -1)
			|| (parCoordsPoints.size() == 0 && parCoordID != -1))
			return;

		// calculating optimal points for heatmap and/or parCoords
		if (heatMapPoints.size() <= 6 || parCoordsPoints.size() <= 3)
			getSinglePointsFromHeatMapAndParCoords(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints,
				parCoordsPredecessorID, parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint);
		else
			getMultiplePointsFromHeatMapAndParCoords(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints,
				parCoordsPredecessorID, parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint);

		renderLines(gl, idType, heatMapPredecessorID, heatMapSuccessorID, parCoordsPredecessorID,
			parCoordsSuccessorID, viewsToBeVisited, hashViewToCenterPoint);
	}

	/**
	 * check if a chosen view is located "before" the gap
	 * 
	 * @param iD
	 * @return true if view lies "before" the gap false otherwise
	 */
	private boolean beforeGap(int iD) {
		int count = 0;
		while (count < gapPosition) {
			if (allviews.get(count) == iD)
				return true;
			count++;
		}
		return false;
	}

	/**
	 * rendering lines
	 * 
	 * @param gl
	 * @param idType
	 * @param heatMapPredecessorID
	 * @param heatMapSuccessorID
	 * @param parCoordsPredecessorID
	 * @param parCoordsSuccessorID
	 * @param viewsToBeVisited
	 * @param hashViewToCenterPoint
	 */
	private void renderLines(GL gl, EIDType idType, int heatMapPredecessorID, int heatMapSuccessorID,
		int parCoordsPredecessorID, int parCoordsSuccessorID, ArrayList<Integer> viewsToBeVisited,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		// needed if the same point occurs more than once in the points list
		removeDuplicatePointEntries();

		Vec3f controlPoint = null;
		Vec3f src = null;

		// getting second view ID for calculating control point needed to calculate the first src point
		int secondID = -1;
		Vec3f centerOfSecondView = null;
		if (viewsToBeVisited.size() > 1)
			secondID = viewsToBeVisited.get(1);
		else
			return;
		if (secondID == heatMapID) {
			if (heatmapPredecessor == null)
				return;
			centerOfSecondView = calculateCenter(heatmapPredecessor);
		}
		else if (secondID == parCoordID) {
			if (parCoordsPredecessor == null)
				return;
			centerOfSecondView = calculateCenter(parCoordsPredecessor);
		}
		else
			centerOfSecondView = hashViewToCenterPoint.get(secondID);

		if (activeViewID == heatMapID) {
			if (heatmapSuccessor.size() == 0)
				return;
			controlPoint = calculateControlPoint(calculateCenter(heatmapSuccessor), centerOfSecondView);
			if (controlPoint == null)
				return;
			if (multiplePoints)
				src = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
			else
				src = calculateCenter(heatmapSuccessor);
		}
		else if (activeViewID == parCoordID) {
			if (parCoordsSuccessor.size() == 0)
				return;
			controlPoint = calculateControlPoint(calculateCenter(parCoordsSuccessor), centerOfSecondView);
			if (controlPoint == null)
				return;
			if (multiplePoints)
				src = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
			else
				src = calculateCenter(parCoordsSuccessor);
		}
		else {
			controlPoint = calculateControlPoint(hashViewToCenterPoint.get(activeViewID), centerOfSecondView);
			if (controlPoint == null)
				return;
			if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1)
				src = calculateBundlingPoint(hashViewToCenterPoint.get(activeViewID), centerOfSecondView);
			else
				src = hashViewToCenterPoint.get(activeViewID);
		}

		// render lines depending on whether there is a gap or not
		if (gapPosition == -1)
			renderLinesWithoutGap(gl, idType, hashViewToCenterPoint, viewsToBeVisited, heatMapPredecessorID,
				parCoordsPredecessorID, src, controlPoint);
		else
			renderLinesWithGap(gl, idType, hashViewToCenterPoint, viewsToBeVisited, heatMapPredecessorID,
				heatMapSuccessorID, parCoordsPredecessorID, parCoordsSuccessorID, src, controlPoint);
	}

	/**
	 * rendering lines if there is a "gap" in the sequence of views that will be visited
	 * 
	 * @param gl
	 *            the GL object
	 * @param idType
	 *            type of genome
	 * @param hashViewToCenterPoint
	 *            center points of views except parcoords and heatmap
	 * @param viewsToBeVisited
	 *            list of views that belong to the current connection graph, sorted by order of visiting
	 * @param heatMapPredecessorID
	 *            id of the heatmap preceding view
	 * @param heatMapSuccessorID
	 *            id of the heatmap succeeding view
	 * @param parCoordsPredecessorID
	 *            id of the parcoord preceding view
	 * @param parCoordSuccessorID
	 *            id of the parCoords succeeding view
	 * @param src
	 *            connection point of the first (starting) view
	 */
	private void renderLinesWithGap(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<Integer> viewsToBeVisited, int heatMapPredecessorID, int heatMapSuccessorID,
		int parCoordsPredecessorID, int parCoordsSuccessorID, Vec3f src, Vec3f controlPoint) {

		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();

		Vec3f vecViewBundlingPoint = null;
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;

		// if rendering of graph starts at focus level
		if (startingAtCenter) {
			//TODO implementation of lines when a pathway is at focus level
			for (int key : viewsToBeVisited) {
				if (key == gapSuccessorID) {
					if (key == parCoordID) {
						if (multiplePoints)
							src = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
						else
							src = heatmapSuccessor.get(0).get(0);
					}
					else if (key == heatMapID) {
						if (multiplePoints)
							src = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
						else
							src = parCoordsSuccessor.get(0).get(0);
					}
					else {
						if (activeViewID == heatMapID) {
							if (multiplePoints)
								src = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
							else
								src = heatmapSuccessor.get(0).get(0);
						}
						else if (activeViewID == parCoordID) {
							if (multiplePoints)
								src =
									calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
							else
								src = parCoordsSuccessor.get(0).get(0);
						}
					}
				}

				if (key == parCoordID && heatMapID == activeViewID) {
					if (parCoordsPredecessor.size() > 0) {
						// calculating control points and local bundling points
						controlPoint = calculateControlPoint(calculateCenter(parCoordsPredecessor), src);
						if (controlPoint == null)
							return;

						vecViewBundlingPoint =
							calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);

						if (multiplePoints) {
							if (parCoordsPredecessorID != heatMapID) {
								currentStage =
									renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint,
										controlPoint, PARCOORDS, null);

								if (parCoordsPredecessorID != heatMapID)
									bundling = new VisLinkAnimationStage(true);
								else
									bundling = new VisLinkAnimationStage();
								bundling
									.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));

								connectionLinesAllViews.add(bundling);
								connectionLinesAllViews.add(currentStage);
							}
							else {
								currentStage =
									renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint,
										controlPoint, PARCOORDS, null);
								currentStage.setReverseLineDrawingDirection(true);
								if (parCoordsPredecessorID != heatMapID)
									bundling = new VisLinkAnimationStage(true);
								else
									bundling = new VisLinkAnimationStage();
								bundling
									.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));
								connectionLinesAllViews.add(currentStage);

								currentStage = new VisLinkAnimationStage();
								ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
								for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor) {
									if (alCurrentPoints.size() > 1)
										renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
									else
										pointsToDepthSort.add(alCurrentPoints.get(0));
								}
								for (Vec3f currentPoint : depthSort(pointsToDepthSort))
									currentStage.addLine(createControlPoints(currentPoint,
										vecViewBundlingPoint, controlPoint));
								connectionLinesAllViews.add(currentStage);

							}
						}
						else {
							// calculating bundling line
							if (parCoordsPredecessorID != heatMapID)
								bundling = new VisLinkAnimationStage(true);
							else
								bundling = new VisLinkAnimationStage();

							bundling.addLine(createControlPoints(src, calculateCenter(parCoordsPredecessor),
								controlPoint));
							connectionLinesAllViews.add(bundling);
						}
						if (parCoordsSuccessor.size() > 0)
							src = calculateCenter(parCoordsSuccessor);
						else
							src = calculateCenter(heatmapSuccessor);
					}
				}
				else if (key == parCoordID && parCoordID == activeViewID) {
					// HEATMAP needs to provide bundling points
					if (parCoordsPredecessor.size() > 0)
						src = calculateCenter(parCoordsPredecessor);
				}

				else if (key == heatMapID && parCoordID == activeViewID) {
					if (heatmapPredecessor.size() > 0) {
						// calculating control points and local bundling points
						controlPoint = calculateControlPoint(calculateCenter(heatmapPredecessor), src);
						if (controlPoint == null)
							return;

						vecViewBundlingPoint =
							calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);

						if (multiplePoints) {
							if (heatMapPredecessorID != parCoordID) {
								currentStage =
									renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint,
										controlPoint, HEATMAP, null);

								if (heatMapPredecessorID != parCoordID)
									bundling = new VisLinkAnimationStage(true);
								else
									bundling = new VisLinkAnimationStage();
								bundling
									.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));

								connectionLinesAllViews.add(bundling);
								connectionLinesAllViews.add(currentStage);
							}
							else {
								currentStage =
									renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint,
										controlPoint, HEATMAP, null);
								currentStage.setReverseLineDrawingDirection(true);
								if (heatMapPredecessorID != parCoordID)
									bundling = new VisLinkAnimationStage(true);
								else
									bundling = new VisLinkAnimationStage();
								bundling
									.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));
								connectionLinesAllViews.add(currentStage);

								currentStage = new VisLinkAnimationStage();
								ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
								for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor) {
									if (alCurrentPoints.size() > 1)
										renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
									else
										pointsToDepthSort.add(alCurrentPoints.get(0));
								}
								for (Vec3f currentPoint : depthSort(pointsToDepthSort))
									currentStage.addLine(createControlPoints(currentPoint,
										vecViewBundlingPoint, controlPoint));
								connectionLinesAllViews.add(currentStage);

							}
						}
						else {
							// calculating bundling line
							if (heatMapPredecessorID != parCoordID)
								bundling = new VisLinkAnimationStage(true);
							else
								bundling = new VisLinkAnimationStage();

							bundling.addLine(createControlPoints(src, calculateCenter(heatmapPredecessor),
								controlPoint));
							connectionLinesAllViews.add(bundling);
						}
						if (heatmapSuccessor.size() > 0)
							src = calculateCenter(heatmapSuccessor);
						else
							src = calculateCenter(parCoordsSuccessor);
					}
				}
				else if (key == heatMapID && heatMapID == activeViewID) {
					// HEATMAP needs to provide bundling points
					if (heatmapPredecessor.size() > 0)
						src = calculateCenter(heatmapPredecessor);
				}

				else {
					controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), src);
					if (controlPoint == null)
						return;
					vecViewBundlingPoint =
						calculateBundlingPoint(hashViewToCenterPoint.get(key), controlPoint);

					currentStage =
						renderLinesOfCurrentStage(gl, idType, key, vecViewBundlingPoint, controlPoint,
							PATHWAY, hashViewToCenterPoint);

					// calculating bundling line
					bundling = new VisLinkAnimationStage();
					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
						bundling.addLine(createControlPoints(vecViewBundlingPoint, src, controlPoint));
					else
						bundling.addLine(createControlPoints(hashViewToCenterPoint.get(key), src,
							controlPoint));

					connectionLinesAllViews.add(bundling);

					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1) {
						currentStage.setReverseLineDrawingDirection(true);
						connectionLinesAllViews.add(currentStage);
						src = vecViewBundlingPoint;
					}
					else
						src = hashViewToCenterPoint.get(key);
				}
			}

		}
		// TODO algorithm if gap exists and rendering starts at stack
		else {

		}

		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);

	}

	/**
	 * rendering lines if no "gap" exists
	 * 
	 * @param gl
	 *            the GL object
	 * @param idType
	 *            type of genome
	 * @param hashViewToCenterPoint
	 *            center points of views except parcoords and heatmap
	 * @param viewsToBeVisited
	 *            list of views that belong to the current connection graph, sorted by order of visiting
	 * @param heatMapPredecessorID
	 *            id of the heatmap preceding view
	 * @param parCoordsPredecessorID
	 *            id of the parcoord preceding view
	 * @param src
	 *            connection point of the first (starting) view
	 * @param vecViewBundlingPoint
	 *            bundling point of the first view
	 * @param controlPoint
	 *            control point needed for calculating the local lines of the first view
	 */
	private void renderLinesWithoutGap(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<Integer> viewsToBeVisited, int heatMapPredecessorID, int parCoordsPredecessorID, Vec3f src,
		Vec3f controlPoint) {

		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();

		Vec3f vecViewBundlingPoint = null;
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;

		for (Integer key : viewsToBeVisited) {
			if (key == heatMapID) {
				if (heatmapPredecessor.size() > 0) {
					// calculating control points and local bundling points
					controlPoint = calculateControlPoint(calculateCenter(heatmapPredecessor), src);
					if (controlPoint == null)
						return;

					vecViewBundlingPoint =
						calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);

					if (multiplePoints) {
						if (heatMapPredecessorID != parCoordID) {
							currentStage =
								renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint,
									HEATMAP, null);
							// calculating bundling line
							if (heatMapPredecessorID != parCoordID)
								bundling = new VisLinkAnimationStage(true);
							else
								bundling = new VisLinkAnimationStage();

							bundling.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));
							connectionLinesAllViews.add(bundling);
							connectionLinesAllViews.add(currentStage);
						}
						else {

							currentStage =
								renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint,
									HEATMAP, null);
							currentStage.setReverseLineDrawingDirection(true);
							// calculating bundling line
							if (heatMapPredecessorID != parCoordID)
								bundling = new VisLinkAnimationStage(true);
							else
								bundling = new VisLinkAnimationStage();

							bundling.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));
							connectionLinesAllViews.add(currentStage);

							currentStage = new VisLinkAnimationStage();
							ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
							for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor) {
								if (alCurrentPoints.size() > 1)
									renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
								else
									pointsToDepthSort.add(alCurrentPoints.get(0));
							}
							for (Vec3f currentPoint : depthSort(pointsToDepthSort))
								currentStage.addLine(createControlPoints(currentPoint, vecViewBundlingPoint,
									controlPoint));
							connectionLinesAllViews.add(currentStage);

						}
					}
					else {
						// calculating bundling line
						if (heatMapPredecessorID != parCoordID)
							bundling = new VisLinkAnimationStage(true);
						else
							bundling = new VisLinkAnimationStage();

						bundling.addLine(createControlPoints(src, calculateCenter(heatmapPredecessor),
							controlPoint));
						connectionLinesAllViews.add(bundling);

					}
					// setting up src point for next view
					if (heatmapSuccessor.size() > 0)
						src = calculateCenter(heatmapSuccessor);
				}
			}
			else if (key == parCoordID) {
				if (parCoordsPredecessor.size() > 0) {
					// calculating control points and local bundling points
					controlPoint = calculateControlPoint(calculateCenter(parCoordsPredecessor), src);
					if (controlPoint == null)
						return;

					vecViewBundlingPoint =
						calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);

					if (multiplePoints) {
						if (parCoordsPredecessorID != heatMapID) {
							currentStage =
								renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint,
									PARCOORDS, null);

							if (parCoordsPredecessorID != heatMapID)
								bundling = new VisLinkAnimationStage(true);
							else
								bundling = new VisLinkAnimationStage();
							bundling.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));

							connectionLinesAllViews.add(bundling);
							connectionLinesAllViews.add(currentStage);
						}
						else {
							currentStage =
								renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint,
									PARCOORDS, null);
							currentStage.setReverseLineDrawingDirection(true);
							if (parCoordsPredecessorID != heatMapID)
								bundling = new VisLinkAnimationStage(true);
							else
								bundling = new VisLinkAnimationStage();
							bundling.addLine(createControlPoints(src, vecViewBundlingPoint, controlPoint));
							connectionLinesAllViews.add(currentStage);

							currentStage = new VisLinkAnimationStage();
							ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
							for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor) {
								if (alCurrentPoints.size() > 1)
									renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
								else
									pointsToDepthSort.add(alCurrentPoints.get(0));
							}
							for (Vec3f currentPoint : depthSort(pointsToDepthSort))
								currentStage.addLine(createControlPoints(currentPoint, vecViewBundlingPoint,
									controlPoint));
							connectionLinesAllViews.add(currentStage);

						}

					}
					else {
						// calculating bundling line
						if (parCoordsPredecessorID != heatMapID)
							bundling = new VisLinkAnimationStage(true);
						else
							bundling = new VisLinkAnimationStage();

						bundling.addLine(createControlPoints(src, calculateCenter(parCoordsPredecessor),
							controlPoint));
						connectionLinesAllViews.add(bundling);
					}
					// setting up src point for next view
					if (parCoordsSuccessor.size() > 0)
						src = calculateCenter(parCoordsSuccessor);
				}
			}
			else {
				// calculating control point and local bundling point
				if (key != activeViewID)
					controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), src);
				if (controlPoint == null)
					return;
				vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(key), controlPoint);

				currentStage =
					renderLinesOfCurrentStage(gl, idType, key, vecViewBundlingPoint, controlPoint, PATHWAY,
						hashViewToCenterPoint);

				if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1 && key == activeViewID)
					connectionLinesAllViews.add(currentStage);

				// calculating bundling line
				if (key != activeViewID) {
					bundling = new VisLinkAnimationStage();

					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
						bundling.addLine(createControlPoints(vecViewBundlingPoint, src, controlPoint));
					else
						bundling.addLine(createControlPoints(hashViewToCenterPoint.get(key), src,
							controlPoint));

					connectionLinesAllViews.add(bundling);

					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1) {
						currentStage.setReverseLineDrawingDirection(true);
						connectionLinesAllViews.add(currentStage);
						src = vecViewBundlingPoint;
					}
					else
						src = hashViewToCenterPoint.get(key);
				}
				else {
					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
						src = vecViewBundlingPoint;
					else
						src = hashViewToCenterPoint.get(key);
				}
			}
		}
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);

	}

	/**
	 * render local lines
	 * 
	 * @param gl
	 *            the gl object
	 * @param vecViewBundlingPoint
	 *            local bundling point of current view
	 * @param controlPoint
	 *            control point for rendering the lines between the local points and the local bundling point
	 * @param type
	 *            type of current view
	 * @param hashViewToCenterPoint
	 * @return the vislink stage that contains the lines of the current view
	 */
	private VisLinkAnimationStage renderLinesOfCurrentStage(GL gl, EIDType idType, Integer key,
		Vec3f vecViewBundlingPoint, Vec3f controlPoint, char type,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
		ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();

		if (type == HEATMAP) {

			for (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor) {
				if (alCurrentPoints.size() > 1)
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				else
					pointsToDepthSort.add(alCurrentPoints.get(0));
			}
		}
		else if (type == PARCOORDS) {
			for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor) {
				if (alCurrentPoints.size() > 1)
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				else
					pointsToDepthSort.add(alCurrentPoints.get(0));
			}
		}
		else if (type == PATHWAY) {
			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(key)) {
				if (alCurrentPoints.size() > 1)
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				else
					pointsToDepthSort.add(alCurrentPoints.get(0));
			}
		}
		if (type == PATHWAY) {
			for (Vec3f currentPoint : depthSort(pointsToDepthSort))
				currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint,
					hashViewToCenterPoint.get(key)));
		}
		else {
			if (type == HEATMAP)
				controlPoint = calculateCenter(heatmapPredecessor);
			else if (type == PARCOORDS)
				controlPoint = calculateCenter(parCoordsPredecessor);

			for (Vec3f currentPoint : depthSort(pointsToDepthSort))
				currentStage.addLine(createControlPoints(currentPoint, vecViewBundlingPoint, controlPoint));
		}
		return currentStage;
	}

	/**
	 * this method is just a helper method if an error during parsing occurred and a view that has only one
	 * connection point thinks it has more than one point
	 */
	private void removeDuplicatePointEntries() {
		heatmapPredecessor = removeDuplicates(heatmapPredecessor);
		heatmapSuccessor = removeDuplicates(heatmapSuccessor);
		parCoordsPredecessor = removeDuplicates(parCoordsPredecessor);
		parCoordsSuccessor = removeDuplicates(parCoordsSuccessor);
	}

	/**
	 * methods that eliminates duplicate entries in the heatmap/parcoord predecessor/successor lists
	 * 
	 * @param list
	 *            the list where duplicates shall be removed
	 */
	private ArrayList<ArrayList<Vec3f>> removeDuplicates(ArrayList<ArrayList<Vec3f>> list) {

		int firstInd = 0;
		while (firstInd < list.size() - 1) {
			Vec3f compared1 = list.get(firstInd).get(0);
			int secondInd = firstInd + 1;
			while (secondInd < list.size()) {
				Vec3f compared2 = list.get(secondInd).get(0);
				if (compared1.x() == compared2.x() && compared1.y() == compared2.y()
					&& compared1.z() == compared2.z())
					list.remove(secondInd);
				else
					secondInd++;
			}
			firstInd++;
		}
		return list;
	}

	/**
	 * getting the IDs of the views which are involved in the current path when rendering the path from the
	 * stack
	 * 
	 * @param hashViewToCenterPoint
	 *            list of center points of the views
	 * @return list of IDs which belong to the current graph
	 */
	private ArrayList<Integer> getViewsOfCurrentPathStartingAtStack(
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<Integer> viewsOfCurrentPath = new ArrayList<Integer>();

		ArrayList<RemoteLevelElement> stackElements = new ArrayList<RemoteLevelElement>(4);
		for (int count = 0; count < stackLevel.getCapacity(); count++)
			stackElements.add(stackLevel.getElementByPositionIndex(count));

		int stackCount = 0;
		while (stackElements.get(stackCount).getGLView() != null
			&& stackElements.get(stackCount).getGLView().getID() != activeViewID) {
			RemoteLevelElement tempElement = stackElements.get(stackCount);
			stackElements.remove(stackCount);
			if (tempElement != null)
				stackElements.add(tempElement);
		}
		for (int count = 0; count < stackElements.size(); count++) {
			if (stackElements.get(count).getGLView() != null
				&& ((stackElements.get(count).getGLView().getID() == parCoordID)
					|| (stackElements.get(count).getGLView().getID() == heatMapID) || (hashViewToCenterPoint
					.containsKey(stackElements.get(count).getGLView().getID()))))
				viewsOfCurrentPath.add(stackElements.get(count).getGLView().getID());
		}

		if ((focusLevel.getElementByPositionIndex(0).getGLView() != null)
			&& ((heatMapID == focusLevel.getElementByPositionIndex(0).getGLView().getID())
				|| (parCoordID == focusLevel.getElementByPositionIndex(0).getGLView().getID()) || (hashViewToCenterPoint
				.containsKey(focusLevel.getElementByPositionIndex(0).getGLView().getID())))) {
			viewsOfCurrentPath.add(1, focusLevel.getElementByPositionIndex(0).getGLView().getID());
			// check if gap if three stack elements and the focus element belong to the graph
			if (viewsOfCurrentPath.size() == 4)
				checkIfGapPresentRenderingFromStack(viewsOfCurrentPath);
		}
		// check if gap if three stack elements and not the focus element belong to the graph
		if (viewsOfCurrentPath.size() == 3)
			checkIfGapPresentRenderingFromStack(viewsOfCurrentPath);

		return viewsOfCurrentPath;
	}

	/**
	 * checks if a "gap" is there when rendering the graph from the stack
	 * 
	 * @param viewsOfCurrentPath
	 *            list of views that belong to the current graph
	 */
	private void checkIfGapPresentRenderingFromStack(ArrayList<Integer> viewsOfCurrentPath) {
		gapPosition = -1;
		gapSuccessorID = -1;
		int positionOfActiveView = -1;

		for (RemoteLevelElement stackElement : stackLevel.getAllElements()) {
			if ((stackElement.getGLView() != null) && (stackElement.getGLView().getID() == activeViewID)) {
				positionOfActiveView = stackLevel.getPositionIndexByElementID(stackElement);
				break;
			}
		}

		if (viewsOfCurrentPath.size() == 4) {
			if (positionOfActiveView == 0) {
				if ((stackLevel.getElementByPositionIndex(2).getGLView() == null)
					|| !(viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(2).getGLView()
						.getID())))
					gapPosition = 2;
			}
			if (positionOfActiveView == 1) {
				if ((stackLevel.getElementByPositionIndex(3).getGLView() == null)
					|| !(viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(3).getGLView()
						.getID())))
					gapPosition = 3;
			}
			if (positionOfActiveView == 2) {
				if ((stackLevel.getElementByPositionIndex(0).getGLView() == null)
					|| !(viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(0).getGLView()
						.getID())))
					gapPosition = 0;
			}
			if (positionOfActiveView == 3) {
				if ((stackLevel.getElementByPositionIndex(1).getGLView() == null)
					|| !(viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(1).getGLView()
						.getID())))
					gapPosition = 1;
			}
		}

		else if (viewsOfCurrentPath.size() == 3) {
			if (focusLevel.getElementByPositionIndex(0).getGLView() == null)
				return;

			if (positionOfActiveView == 0 || positionOfActiveView == 2) {
				if ((stackLevel.getElementByPositionIndex(1).getGLView() != null)
					&& (stackLevel.getElementByPositionIndex(3).getGLView() != null)) {
					if (viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(1).getGLView()
						.getID())
						&& viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(3).getGLView()
							.getID()))
						gapPosition = 4;
				}
			}
			else if (positionOfActiveView == 1 || positionOfActiveView == 3) {
				if ((stackLevel.getElementByPositionIndex(0).getGLView() != null)
					&& (stackLevel.getElementByPositionIndex(2).getGLView() != null)) {
					if (viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(0).getGLView()
						.getID())
						&& viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(2).getGLView()
							.getID()))
						gapPosition = 4;
				}
			}
		}
	}

	/**
	 * getting the IDs of the views which are involved in the current path when rendering the path from the
	 * focus
	 * 
	 * @param hashViewToCenterPoint
	 *            list of center points of the views
	 * @return list of IDs which belong to the current graph
	 */
	private ArrayList<Integer> getViewsOfCurrentPathStartingAtFocus(
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		allviews.clear();
		gapSuccessorID = -1;
		ArrayList<Integer> viewsOfCurrentPath = new ArrayList<Integer>();
		ArrayList<Integer> notLoadedPosition = new ArrayList<Integer>();

		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if ((stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && hashViewToCenterPoint
				.containsKey(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID()))
				|| (stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && heatMapID == stackLevel
					.getElementByPositionIndex(stackCount).getGLView().getID())
				|| (stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && parCoordID == stackLevel
					.getElementByPositionIndex(stackCount).getGLView().getID())) {
				viewsOfCurrentPath.add(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID());
				allviews.add(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID());
			}
			else {
				notLoadedPosition.add(stackCount);
				allviews.add(-1);
			}
		}
		if (viewsOfCurrentPath.size() == 2)
			checkIfGapPresentRenderingFromCenter(allviews, notLoadedPosition);
		else if (viewsOfCurrentPath.size() == 3)
			viewsOfCurrentPath = revertElements(viewsOfCurrentPath, notLoadedPosition.get(0));

		viewsOfCurrentPath.add(0, focusLevel.getElementByPositionIndex(0).getGLView().getID());

		return viewsOfCurrentPath;
	}

	/**
	 * checks if a "gap" is available when rendering from the focus
	 * 
	 * @param allviews
	 *            list of view that are currently loaded
	 * @param notLoadedPosition
	 *            bitmap that indicates which actually loaded views do not belong to the current connection
	 *            graph
	 */
	private void checkIfGapPresentRenderingFromCenter(ArrayList<Integer> allviews,
		ArrayList<Integer> notLoadedPosition) {
		if (notLoadedPosition.get(0) != (notLoadedPosition.get(1) - 1)) {
			if (notLoadedPosition.get(0) == 0 && notLoadedPosition.get(1) == 3)
				return;
			if (notLoadedPosition.get(0) == 1) {
				gapSuccessorID = allviews.get(notLoadedPosition.get(0) + 1);
				gapPosition = 1;
			}
			else {
				gapSuccessorID = allviews.get(notLoadedPosition.get(1) + 1);
				gapPosition = 2;
			}
		}
	}

	/**
	 * this method reverts the direction of the graph if three elements are loaded at stack level to avoid
	 * complex gap handling
	 * 
	 * @param viewsOfCurrentPath
	 *            views that belong to the current path
	 * @param nullElement
	 *            position of the not loaded element
	 */
	private ArrayList<Integer> revertElements(ArrayList<Integer> viewsOfCurrentPath, int nullElement) {
		if ((nullElement == 3) || (nullElement == 0))
			return viewsOfCurrentPath;
		ArrayList<Integer> views = new ArrayList<Integer>();
		for (int count = nullElement + 1; count < allviews.size(); count++) {
			views.add(allviews.get(count));
		}
		for (int count = 0; count < nullElement; count++) {
			views.add(allviews.get(count));
		}
		return views;

	}

	/**
	 * getting the optimal points of the heatmap/parcoords view
	 * 
	 * @param heatMapPredecessorID
	 *            ID of the heatmap precedent view
	 * @param heatMapSuccessorID
	 *            ID of the heatmap succeeding view
	 * @param heatMapPoints
	 *            list of heatmap points
	 * @param parCoordsPredecessorID
	 *            of the parcoord precedent view
	 * @param parCoordsSuccessorID
	 *            ID of the parcoord succeeding view
	 * @param parCoordsPoints
	 *            list of parcoord points
	 * @param hashViewToCenterPoint
	 *            center points of other views
	 */
	private void getSinglePointsFromHeatMapAndParCoords(int heatMapPredecessorID, int heatMapSuccessorID,
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int parCoordsPredecessorID, int parCoordsSuccessorID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		multiplePoints = false;
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();

		if (heatMapPredecessorID != -1) {
			heatmapPredecessor.clear();
			if (heatMapPredecessorID == parCoordID)
				getSinglePointsIfOtherViewIsDynamicView(parCoordsPoints, heatMapPoints, HEATMAP, PREDECESSOR);
			else
				getSinglePointsIfNotOtherViewIsDynamicView(heatMapPoints, hashViewToCenterPoint
					.get(heatMapPredecessorID), HEATMAP, PREDECESSOR);
		}
		if (heatMapSuccessorID != -1) {
			heatmapSuccessor.clear();
			if (heatMapSuccessorID == parCoordID)
				getSinglePointsIfOtherViewIsDynamicView(heatMapPoints, parCoordsPoints, HEATMAP, SUCCESSOR);
			else
				getSinglePointsIfNotOtherViewIsDynamicView(heatMapPoints, hashViewToCenterPoint
					.get(heatMapSuccessorID), HEATMAP, SUCCESSOR);
		}
		if (parCoordsPredecessorID != -1) {
			parCoordsPredecessor.clear();
			if (parCoordsPredecessorID == heatMapID)
				getSinglePointsIfOtherViewIsDynamicView(heatMapPoints, parCoordsPoints, PARCOORDS,
					PREDECESSOR);
			else
				getSinglePointsIfNotOtherViewIsDynamicView(parCoordsPoints, hashViewToCenterPoint
					.get(parCoordsPredecessorID), PARCOORDS, PREDECESSOR);
		}
		if (parCoordsSuccessorID != -1) {
			parCoordsSuccessor.clear();
			if (parCoordsSuccessorID == heatMapID)
				getSinglePointsIfOtherViewIsDynamicView(parCoordsPoints, heatMapPoints, PARCOORDS, SUCCESSOR);
			else
				getSinglePointsIfNotOtherViewIsDynamicView(parCoordsPoints, hashViewToCenterPoint
					.get(parCoordsSuccessorID), PARCOORDS, SUCCESSOR);
		}
	}

	/**
	 * getting the optimal points of heatmap/parcoords if multiple points have to be drawn
	 * 
	 * @param heatMapPredecessorID
	 *            ID of the heatmap precedent view
	 * @param heatMapSuccessorID
	 *            ID of the heatmap succeeding view
	 * @param heatMapPoints
	 *            list of heatmap points from which the optimal points have to be choosen from
	 * @param parCoordsPredecessorID
	 *            ID of the parcoord precedent view
	 * @param parCoordsSuccessorID
	 *            ID of the parcoord succeeding view
	 * @param parCoordsPoints
	 *            list of parcoord points from which the optimal points have to be choosen from
	 * @param hashViewToCenterPoint
	 *            list of center points excluding heatmap and parcoords
	 */
	private void getMultiplePointsFromHeatMapAndParCoords(int heatMapPredecessorID, int heatMapSuccessorID,
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int parCoordsPredecessorID, int parCoordsSuccessorID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		multiplePoints = true;

		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleHeatMapPoints =
			new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleParCoordPoints =
			new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		ArrayList<Vec3f> heatMapCenterPoints = new ArrayList<Vec3f>();
		ArrayList<Vec3f> parCoordCenterPoints = new ArrayList<Vec3f>();

		for (int count = 0; count < 6; count++)
			multipleHeatMapPoints.add(new ArrayList<ArrayList<Vec3f>>());
		for (int count = 0; count < 3; count++)
			multipleParCoordPoints.add(new ArrayList<ArrayList<Vec3f>>());

		for (int pointCount = 0; pointCount < heatMapPoints.size(); pointCount++)
			multipleHeatMapPoints.get(pointCount % 6).add(heatMapPoints.get(pointCount));

		for (int pointCount = 0; pointCount < parCoordsPoints.size(); pointCount++)
			multipleParCoordPoints.get(pointCount % 3).add(parCoordsPoints.get(pointCount));

		for (int count = 0; count < multipleHeatMapPoints.size(); count++) {
			heatMapCenterPoints.add(calculateCenter(multipleHeatMapPoints.get(count)));
		}

		for (int count = 0; count < multipleParCoordPoints.size(); count++) {
			parCoordCenterPoints.add(calculateCenter(multipleParCoordPoints.get(count)));
		}

		if (heatMapPredecessorID != -1) {
			if (heatMapPredecessorID == parCoordID)
				getMultiplePointsIfOtherViewIsDynamicView(multipleParCoordPoints, parCoordCenterPoints,
					multipleHeatMapPoints, heatMapCenterPoints, HEATMAP, PREDECESSOR);
			else
				getMultiplePointsIfNotOtherViewIsDynamicView(multipleHeatMapPoints, heatMapCenterPoints,
					hashViewToCenterPoint.get(heatMapPredecessorID), HEATMAP, PREDECESSOR);
		}
		if (heatMapSuccessorID != -1) {
			if (heatMapSuccessorID == parCoordID)
				getMultiplePointsIfOtherViewIsDynamicView(multipleHeatMapPoints, heatMapCenterPoints,
					multipleParCoordPoints, parCoordCenterPoints, HEATMAP, SUCCESSOR);
			else
				getMultiplePointsIfNotOtherViewIsDynamicView(multipleHeatMapPoints, heatMapCenterPoints,
					hashViewToCenterPoint.get(heatMapSuccessorID), HEATMAP, SUCCESSOR);
		}
		if (parCoordsPredecessorID != -1) {
			if (parCoordsPredecessorID == heatMapID)
				getMultiplePointsIfOtherViewIsDynamicView(multipleHeatMapPoints, heatMapCenterPoints,
					multipleParCoordPoints, parCoordCenterPoints, PARCOORDS, PREDECESSOR);
			else
				getMultiplePointsIfNotOtherViewIsDynamicView(multipleParCoordPoints, parCoordCenterPoints,
					hashViewToCenterPoint.get(parCoordsPredecessorID), PARCOORDS, PREDECESSOR);
		}
		if (parCoordsSuccessorID != -1) {
			if (parCoordsSuccessorID == heatMapID)
				getMultiplePointsIfOtherViewIsDynamicView(multipleParCoordPoints, parCoordCenterPoints,
					multipleHeatMapPoints, heatMapCenterPoints, PARCOORDS, SUCCESSOR);
			else
				getMultiplePointsIfNotOtherViewIsDynamicView(multipleParCoordPoints, parCoordCenterPoints,
					hashViewToCenterPoint.get(parCoordsSuccessorID), PARCOORDS, SUCCESSOR);
		}

	}

	/**
	 * calculates shortest path between heatmap and parcoords view when multiple points have to be calculated
	 * 
	 * @param multiplePredecessorPointsList
	 *            list of predecessor points
	 * @param predecessorCenterPointsList
	 *            contains the center points of the points list for simplifying the calculation of the
	 *            shortest path
	 * @param multipleSuccessorPointsList
	 *            list of successor points
	 * @param successorCenterPointsList
	 *            contains the center points of the points list for simplifying the calculation of the
	 *            shortest path
	 * @param type
	 *            type of the dynamic view (either HEATMAP or PARCOORDS)
	 * @param nextOrPrevious
	 *            indicates whether the remote view is predecessor or successor
	 */

	private void getMultiplePointsIfOtherViewIsDynamicView(
		ArrayList<ArrayList<ArrayList<Vec3f>>> multiplePredecessorPointsList,
		ArrayList<Vec3f> predecessorCenterPointsList,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleSuccessorPointsList,
		ArrayList<Vec3f> successorCenterPointsList, char type, int nextOrPrevious) {

		float currentPath = -1;
		float minPath = Float.MAX_VALUE;

		ArrayList<ArrayList<Vec3f>> optimalPredecessorPoints = null;
		ArrayList<ArrayList<Vec3f>> optimalSuccessorPoints = null;

		for (Vec3f successorPoint : successorCenterPointsList) {
			for (Vec3f predecessorPoint : predecessorCenterPointsList) {
				Vec3f distanceToPredecessor = predecessorPoint.minus(successorPoint);
				currentPath = distanceToPredecessor.length();
				if (currentPath < minPath) {
					minPath = currentPath;
					optimalPredecessorPoints =
						multiplePredecessorPointsList.get(predecessorCenterPointsList
							.indexOf(predecessorPoint));
					optimalSuccessorPoints =
						multipleSuccessorPointsList.get(successorCenterPointsList.indexOf(successorPoint));
				}
			}
		}

		if (type == HEATMAP && nextOrPrevious == PREDECESSOR) {
			parCoordsSuccessor = optimalSuccessorPoints;
			heatmapPredecessor = optimalPredecessorPoints;
		}
		else if (type == HEATMAP && nextOrPrevious == SUCCESSOR) {
			parCoordsPredecessor = optimalPredecessorPoints;
			heatmapSuccessor = optimalSuccessorPoints;
		}
		else if (type == PARCOORDS && nextOrPrevious == PREDECESSOR) {
			heatmapSuccessor = optimalSuccessorPoints;
			parCoordsPredecessor = optimalPredecessorPoints;
		}
		else if (type == PARCOORDS && nextOrPrevious == SUCCESSOR) {
			heatmapPredecessor = optimalPredecessorPoints;
			parCoordsSuccessor = optimalSuccessorPoints;
		}
	}

	/**
	 * Calculates the shortest path between two views if one of them is either the heatmap view or the
	 * parcoords view and the views contain multiple point
	 * 
	 * @param multipleDynamicPoints
	 *            list of multiple points
	 * @param dynamicCenterPointsList
	 *            list of center of multiple points (used to simplify calculation of optimal points list)
	 * @param connectionPoint
	 *            point of reomte view
	 * @param type
	 *            type of dynamic view (either HEATMAP or PARCOORDS)
	 * @param nextOrPrevious
	 *            indicates if the remote view is successor or predecessor of the dynamic view
	 */
	private void getMultiplePointsIfNotOtherViewIsDynamicView(
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleDynamicPoints,
		ArrayList<Vec3f> dynamicCenterPointsList, Vec3f connectionPoint, char type, int nextOrPrevious) {

		float currentPath = -1;
		float minPath = Float.MAX_VALUE;

		if (connectionPoint == null)
			return;
		ArrayList<ArrayList<Vec3f>> optimalPoints = null;
		for (Vec3f dynamicPoint : dynamicCenterPointsList) {
			Vec3f distanceToPredecessor = dynamicPoint.minus(connectionPoint);
			currentPath = distanceToPredecessor.length();
			if (currentPath < minPath) {
				minPath = currentPath;
				optimalPoints = multipleDynamicPoints.get(dynamicCenterPointsList.indexOf(dynamicPoint));
			}
		}

		if (type == HEATMAP && nextOrPrevious == PREDECESSOR)
			heatmapPredecessor = optimalPoints;
		else if (type == HEATMAP && nextOrPrevious == SUCCESSOR)
			heatmapSuccessor = optimalPoints;
		else if (type == PARCOORDS && nextOrPrevious == PREDECESSOR)
			parCoordsPredecessor = optimalPoints;
		else if (type == PARCOORDS && nextOrPrevious == SUCCESSOR)
			parCoordsSuccessor = optimalPoints;
	}

	/**
	 * calculates shortest path between heatmap and parcoords view
	 * 
	 * @param predecessorPointsList
	 *            points list to choose the best point from
	 * @param successorPointsList
	 *            points list to choose the best point from
	 * @param type
	 *            type of dynamic view (either HEATMAP or PARCOORD)
	 * @param nextOrPrevious
	 *            indicates if the other involved view is the predecessor or successor of the
	 *            heatmap/parcoords view
	 */
	private void getSinglePointsIfOtherViewIsDynamicView(ArrayList<ArrayList<Vec3f>> predecessorPointsList,
		ArrayList<ArrayList<Vec3f>> successorPointsList, char type, int nextOrPrevious) {
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;

		ArrayList<Vec3f> optimalPredecessorPoints = null;
		ArrayList<Vec3f> optimalSuccessorPoints = null;

		for (ArrayList<Vec3f> successorPoints : successorPointsList) {
			for (ArrayList<Vec3f> predecessorPoints : predecessorPointsList) {
				Vec3f predecessorPoint = predecessorPoints.get(0);
				Vec3f successorPoint = successorPoints.get(0);
				Vec3f distanceToPredecessor = predecessorPoint.minus(successorPoint);
				currentPath = distanceToPredecessor.length();
				if (currentPath < minPath) {
					minPath = currentPath;
					optimalPredecessorPoints = predecessorPoints;
					optimalSuccessorPoints = successorPoints;
				}
			}
		}
		if (type == HEATMAP && nextOrPrevious == PREDECESSOR) {
			if (!parCoordsSuccessor.contains(optimalSuccessorPoints))
				parCoordsSuccessor.add(optimalSuccessorPoints);
			if (!heatmapPredecessor.contains(optimalPredecessorPoints))
				heatmapPredecessor.add(optimalPredecessorPoints);
		}
		else if (type == HEATMAP && nextOrPrevious == SUCCESSOR) {
			if (!parCoordsPredecessor.contains(optimalPredecessorPoints))
				parCoordsPredecessor.add(optimalPredecessorPoints);
			if (!heatmapSuccessor.contains(optimalSuccessorPoints))
				heatmapSuccessor.add(optimalSuccessorPoints);
		}
		else if (type == PARCOORDS && nextOrPrevious == PREDECESSOR) {
			if (!heatmapSuccessor.contains(optimalSuccessorPoints))
				heatmapSuccessor.add(optimalSuccessorPoints);
			if (!parCoordsPredecessor.contains(optimalPredecessorPoints))
				parCoordsPredecessor.add(optimalPredecessorPoints);
		}
		else if (type == PARCOORDS && nextOrPrevious == SUCCESSOR) {
			if (!heatmapPredecessor.contains(optimalPredecessorPoints))
				heatmapPredecessor.add(optimalPredecessorPoints);
			if (!parCoordsSuccessor.contains(optimalSuccessorPoints))
				parCoordsSuccessor.add(optimalSuccessorPoints);
		}
	}

	/**
	 * Calculates the shortest path between two views if one of them is either the heatmap view or the
	 * parcoords view
	 * 
	 * @param dynamicPointsList
	 *            set of points to choose the best from
	 * @param connectionPoint
	 *            connection point to which the parcoord/heatmap view should be connected to
	 * @param type
	 *            type of dynamic view (either HEATMAP or PARCOORDS)
	 * @param nextOrPrevious
	 *            indicates if the other involved view is the predecessor or successor of the
	 *            heatmap/parcoords view
	 */
	private void getSinglePointsIfNotOtherViewIsDynamicView(ArrayList<ArrayList<Vec3f>> dynamicPointsList,
		Vec3f connectionPoint, char type, int nextOrPrevious) {
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;

		if (connectionPoint == null)
			return;
		ArrayList<Vec3f> optimalPoints = null;
		for (ArrayList<Vec3f> dynamicPoints : dynamicPointsList) {
			Vec3f predecessorPoint = dynamicPoints.get(0);
			Vec3f distanceToPredecessor = predecessorPoint.minus(connectionPoint);
			currentPath = distanceToPredecessor.length();
			if (currentPath < minPath) {
				minPath = currentPath;
				optimalPoints = dynamicPoints;
			}
		}
		if (type == HEATMAP && nextOrPrevious == PREDECESSOR)
			heatmapPredecessor.add(optimalPoints);
		else if (type == HEATMAP && nextOrPrevious == SUCCESSOR)
			heatmapSuccessor.add(optimalPoints);
		else if (type == PARCOORDS && nextOrPrevious == PREDECESSOR)
			parCoordsPredecessor.add(optimalPoints);
		else if (type == PARCOORDS && nextOrPrevious == SUCCESSOR)
			parCoordsSuccessor.add(optimalPoints);
	}

	/**
	 * Calculates the optimal control point for the curve between two given points Algorithm: Calculate vector
	 * between src and dst calculate center point between src and dst take line whose direction is the normal
	 * vector of the focus plain (0,0,1) calculate the point that lies 0.5 units of length closer to the focus
	 * plain
	 * 
	 * @param src
	 *            first point
	 * @param dst
	 *            second point
	 * @return control point for this connection line
	 */
	private Vec3f calculateControlPoint(Vec3f src, Vec3f dst) {
		if (src == dst || (src == null || dst == null))
			return null;
		Vec3f centerPoint = src.minus(dst);
		centerPoint.scale(0.5f);
		centerPoint.add(dst);

		float zVal = (float) (centerPoint.z() * Math.sqrt(0.5));
		Vec3f controlPoint = centerPoint;
		controlPoint.setZ(zVal);
		return controlPoint;

		/*
		 * Vec3f controlPoint = null; if (src == dst) return null; Vec3f connectionVec = src.minus(dst);
		 * connectionVec.scale(0.5f); connectionVec = connectionVec.plus(src); float minDistance =
		 * Float.MAX_VALUE; float currentDistance = -1; for (Vec3f element : controlPoints) { Vec3f distance =
		 * connectionVec.minus(element); currentDistance = distance.length(); if (currentDistance <
		 * minDistance){ minDistance = currentDistance; controlPoint = element; } } return controlPoint;
		 */
	}

	/**
	 * This method gets the successor of a given view
	 * 
	 * @param list
	 *            list containing all views
	 * @param iD
	 *            id of the view to which the successor should be found
	 * @return ID of the succeeding view
	 */
	private int getNextView(ArrayList<Integer> list, int iD) {
		int position = list.indexOf(iD);

		if (position == list.size() - 1)
			return -1;
		else if (position == -1)
			return -1;

		while (position < list.size() - 1) {
			if (list.get(position + 1) != -1)
				return list.get(position + 1);
			position++;
		}
		return -1;
	}

	/**
	 * This method gets the predecessor of a given view
	 * 
	 * @param list
	 *            list containing all views
	 * @param iD
	 *            id of the view to which the predecessor should be found
	 * @return ID of the precedent view
	 */
	private Integer getPreviousView(ArrayList<Integer> list, int iD) {
		int position = list.indexOf(iD);
		if (position == 0)
			return -1;
		else if (position == -1)
			return -1;

		while (position > 0) {
			if (list.get(position - 1) != -1)
				return list.get(position - 1);
			position--;
		}
		return -1;

	}

	// this method is not needed for this kind of graph type cause optimal points are calculated in a
	// different way
	@Override
	protected ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		return null;
	}

	// this method is not needed for this kind of graph type cause optimal points are calculated in a
	// different way
	@Override
	protected ArrayList<ArrayList<Vec3f>> calculateOptimalSinglePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		return null;
	}
}