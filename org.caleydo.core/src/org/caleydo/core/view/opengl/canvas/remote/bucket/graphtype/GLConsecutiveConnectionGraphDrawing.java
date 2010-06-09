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

	private final int PREDECESSOR = 0;
	private final int SUCCESSOR = 1;
	private final int PARCOORDELEMENTS = 3;
	private final int HEATMAPELEMENTS = 6;
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
	private int gapPredecessorID = -1;
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
	}

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
		heatMapPoints = cleanUpPointsList(HEATMAP, heatMapPoints);
		parCoordsPoints = cleanUpPointsList(PARCOORDS, parCoordsPoints);

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

	
	/** a helper method that removes duplicate entries from the points list
	 * @param type either heatmap or parcoords
	 * @param pointsList list of points to be cleaned
	 * @return the cleaned points list
	 */
	//TODO find a finer solution ;)
	private ArrayList<ArrayList<Vec3f>> cleanUpPointsList(char type, ArrayList<ArrayList<Vec3f>> pointsList) {
		ArrayList<ArrayList<Vec3f>> cleanPointsList = pointsList;
		
		int matches = 0;
		int stepsize = 0;
		if (type == PARCOORDS)
			stepsize = PARCOORDELEMENTS;
		else
			stepsize = HEATMAPELEMENTS;
	
		ArrayList<ArrayList<Vec3f>> firstTempArray = new ArrayList<ArrayList<Vec3f>>(stepsize);
		ArrayList<ArrayList<Vec3f>> secondTempArray = new ArrayList<ArrayList<Vec3f>>(stepsize);
		
		for (int count = 0; count < pointsList.size()-stepsize; count +=stepsize){
			firstTempArray.clear();
			secondTempArray.clear();
			matches = 0;
			for (int tempCount = 0; tempCount < stepsize; tempCount++)
				firstTempArray.add(pointsList.get(count+tempCount));
			
			for (int innerCount = 0; innerCount < stepsize; innerCount++)
				secondTempArray.add(pointsList.get(count+stepsize+innerCount));
			for (int checkCount = 0; checkCount < stepsize; checkCount++){
				if (firstTempArray.get(checkCount).get(0).x() == secondTempArray.get(checkCount).get(0).x() &&
					firstTempArray.get(checkCount).get(0).y() == secondTempArray.get(checkCount).get(0).y() &&
					firstTempArray.get(checkCount).get(0).z() == secondTempArray.get(checkCount).get(0).z())
					matches++;
			}
			if (matches == stepsize){
				for (int counter = count; counter < count+stepsize; counter++) {
					cleanPointsList.remove(counter);
				}
			}
		}
		return cleanPointsList;
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
		gapPredecessorID = viewsToBeVisited.get(1);
		Vec3f centerPoint = null;

			
		// if rendering of graph starts at focus level
		if (startingAtCenter) {
			
			//weighted center point if a pathway is on focus level
			if ((activeViewID != parCoordID) && (activeViewID != heatMapID)){
				centerPoint = getWeightedPointForCenteredPathway(hashViewToCenterPoint);
			}
			
			//select case 
			if (activeViewID == parCoordID){
				if (heatMapID == gapPredecessorID)
					connectionLinesAllViews = renderDynamicViewCentered(gl, idType, PARCOORDS, true, hashViewToCenterPoint, viewsToBeVisited, controlPoint);
				else
					connectionLinesAllViews = renderDynamicViewCentered(gl, idType, PARCOORDS, false, hashViewToCenterPoint, viewsToBeVisited, controlPoint);
			}
			else if(activeViewID == heatMapID){
				if (parCoordID == gapPredecessorID)
					connectionLinesAllViews = renderDynamicViewCentered(gl, idType, HEATMAP, true, hashViewToCenterPoint, viewsToBeVisited, controlPoint);
				else
					connectionLinesAllViews = renderDynamicViewCentered(gl, idType, HEATMAP, false, hashViewToCenterPoint, viewsToBeVisited, controlPoint);
			}
			
			else
				connectionLinesAllViews = renderPathwayCentered(gl, idType, hashViewToCenterPoint, viewsToBeVisited, centerPoint);

		}
		else {
			if (viewsToBeVisited.size() == 3){
				if (heatMapID == activeViewID)
					connectionLinesAllViews = renderDyamicViewActive(gl, idType, viewsToBeVisited, hashViewToCenterPoint, controlPoint);
				else if (parCoordID == activeViewID)
					connectionLinesAllViews = renderDyamicViewActive(gl, idType, viewsToBeVisited, hashViewToCenterPoint, controlPoint);
				else{
					//parcoords and heatmap connect to the pathway so a weighted bundling point is needed
					controlPoint = getWeightedPointForCenteredPathway(hashViewToCenterPoint);
					connectionLinesAllViews = renderPathWayActive(gl, idType, hashViewToCenterPoint, controlPoint);
				}
			}
			else if (viewsToBeVisited.size() == 4){
				int centerViewID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
				if (heatMapID == centerViewID)
					connectionLinesAllViews = renderToTheMiddle(HEATMAP, gl, idType, heatMapPredecessorID, heatMapSuccessorID, viewsToBeVisited, hashViewToCenterPoint);
				else if (parCoordID == centerViewID)
					connectionLinesAllViews = renderToTheMiddle(PARCOORDS, gl, idType, parCoordsPredecessorID, parCoordsSuccessorID, viewsToBeVisited, hashViewToCenterPoint);
				else
					connectionLinesAllViews = renderToTheMiddle(PATHWAY, gl, idType, -1, -1, viewsToBeVisited, hashViewToCenterPoint);
			}
		}

		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);

	}

	
	/** render lines if active view is on stack, 4 views are loaded and a gap occurs
	 * 
	 * @param type type of center view
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param predecessorID id of center predeceeding view
	 * @param successorID ID id of center succeeding view
	 * @param viewsToBeVisited list of views that belong to the current graph
	 * @param hashViewToCenterPoint list of center points
	 * @return the vislink container 
	 */
	private ArrayList<VisLinkAnimationStage> renderToTheMiddle(char type, GL gl, EIDType idType,
		int predecessorID, int successorID, ArrayList<Integer> viewsToBeVisited, HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
			
		if (type == PATHWAY)
			connectionLinesAllViews = renderFromStackWithGapPathwayCentered(gl, idType, viewsToBeVisited, hashViewToCenterPoint);
		else if (type == PARCOORDS)
			connectionLinesAllViews = renderFromStackWithGapDynamicViewCentered(gl, PARCOORDS, idType, predecessorID, successorID, viewsToBeVisited, hashViewToCenterPoint);
		else if (type == HEATMAP)
			connectionLinesAllViews = renderFromStackWithGapDynamicViewCentered(gl, HEATMAP, idType, predecessorID, successorID, viewsToBeVisited, hashViewToCenterPoint);
		
		return connectionLinesAllViews;
	}

	
	/** render lines if rendering from stack, a gap exists and a dynamic view is in focus
	 * 
	 * @param gl the gl object
	 * @param type special view type that is in center 
	 * @param idType type of genome
	 * @param predecessorID id of the view that precedes the center view
	 * @param successorID id of the view that succeeds the center view
	 * @param viewsToBeVisited list of views that belong to the current graph
	 * @param hashViewToCenterPoint list of center points
	 * @return the vislink container
	 */
	private ArrayList<VisLinkAnimationStage> renderFromStackWithGapDynamicViewCentered(GL gl, char type,
		EIDType idType, int predecessorID, int successorID, ArrayList<Integer> viewsToBeVisited, HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		VisLinkAnimationStage connectionLinesActiveView = new VisLinkAnimationStage();

		if (type == HEATMAP){
			for (Integer iKey : viewsToBeVisited){
				VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
				VisLinkAnimationStage connectionLinesOtherView = new VisLinkAnimationStage();
				Vec3f vecViewBundlingPoint = null;
				//parcoords predeceed or succeed middle view
				if (iKey == parCoordID && (iKey == predecessorID || iKey == successorID)){
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					Vec3f controlPoint = null;
					if (activeViewID == parCoordID){
						controlPoint = calculateControlPoint(calculateCenter(parCoordsSuccessor), calculateCenter(heatmapPredecessor));
						vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
					}
					else{
						controlPoint = calculateControlPoint(calculateCenter(parCoordsPredecessor), calculateCenter(heatmapSuccessor));
						vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);
					}
					if (activeViewID == parCoordID){
						for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor) {
							if (alCurrentPoints.size() > 1) {
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							}
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
					}
					else{
						for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor) {
							if (alCurrentPoints.size() > 1) {
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							}
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
					}
					for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {
						if(activeViewID != -1 && iKey == activeViewID){
							if (activeViewID == parCoordID)
								connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(parCoordsSuccessor) ) );					
						}
						else
							connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(parCoordsPredecessor)) );
					}
				
					if (iKey == activeViewID){
						bundlingLine.addLine(createControlPoints(vecViewBundlingPoint, calculateCenter(heatmapPredecessor), controlPoint));
						connectionLinesAllViews.add(bundlingLine);
						connectionLinesActiveView.setReverseLineDrawingDirection(true);
						connectionLinesAllViews.add(connectionLinesActiveView);
					}
					else{
						bundlingLine.addLine(createControlPoints(calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint), calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint), controlPoint));
						connectionLinesAllViews.add(connectionLinesOtherView);
						connectionLinesOtherView = new VisLinkAnimationStage(true);
						for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor)
							pointsToDepthSort.add(alCurrentPoints.get(0));
						for(Vec3f currentPoint : depthSort(pointsToDepthSort))
							connectionLinesOtherView.addLine( createControlPoints( calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint), currentPoint, controlPoint) );					
						connectionLinesAllViews.add(connectionLinesOtherView);
	
					}
				}
				//pathway that predeceeds middle view
				else if (iKey != parCoordID && (iKey == predecessorID)){
					Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(activeViewID), calculateCenter(heatmapPredecessor));
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), controlPoint);
					
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey))
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {	
						if (iKey == activeViewID)
							connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
						else
							connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
					}
	
					connectionLinesAllViews.add(connectionLinesActiveView);
					connectionLinesActiveView = new VisLinkAnimationStage(true);
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
							connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, controlPoint ) );
					connectionLinesAllViews.add(connectionLinesActiveView);
				}
				//pathway that succeeds the middle view
				else if (iKey != parCoordID && iKey == successorID){
					Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(successorID), calculateCenter(heatmapSuccessor));
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), controlPoint);
					connectionLinesOtherView = new VisLinkAnimationStage(true);
					
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey))
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))	
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
					
					VisLinkAnimationStage connectionLinesToCenter = new VisLinkAnimationStage();
					ArrayList<ArrayList<Vec3f>> centerPoints = null;
					if (multiplePoints)
						centerPoints = getMultipleCenterPoints(HEATMAP, hashIDTypeToViewToPointLists.get(idType).get(heatMapID), vecViewBundlingPoint);
					else
						centerPoints = getCenterPoints(hashIDTypeToViewToPointLists.get(idType).get(heatMapID), vecViewBundlingPoint);
					pointsToDepthSort = new ArrayList<Vec3f>();
					//controlPoint = calculateControlPoint(calculateCenter(centerPoints), vecViewBundlingPoint);
					for (ArrayList<Vec3f> alCurrentPoints : centerPoints)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						connectionLinesToCenter.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, controlPoint ) );
					connectionLinesAllViews.add(connectionLinesToCenter);
					connectionLinesAllViews.add(connectionLinesOtherView);
					
				}
				//pathway is neither predecessor nor successor of middle view
				else if (iKey != heatMapID && iKey != parCoordID && iKey != predecessorID && iKey != successorID){
					Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(viewsToBeVisited.get(3)), calculateCenter(parCoordsPredecessor));
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), controlPoint);
					connectionLinesOtherView = new VisLinkAnimationStage(true);
					//controlPoint = calculateControlPoint(calculateCenter(centerPoints), vecViewBundlingPoint);
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey))
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))	
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
					
					VisLinkAnimationStage connectionLinesToCenter = new VisLinkAnimationStage();
					ArrayList<ArrayList<Vec3f>> centerPoints = null;
					if (multiplePoints)
						centerPoints = getMultipleCenterPoints(HEATMAP, hashIDTypeToViewToPointLists.get(idType).get(heatMapID), vecViewBundlingPoint);
					else
						centerPoints = getCenterPoints(hashIDTypeToViewToPointLists.get(idType).get(heatMapID), vecViewBundlingPoint);
					//controlPoint = calculateControlPoint(calculateCenter(centerPoints), vecViewBundlingPoint);
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : centerPoints)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						connectionLinesToCenter.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, controlPoint ) );
					connectionLinesAllViews.add(connectionLinesToCenter);
					connectionLinesAllViews.add(connectionLinesOtherView);
				}
				else if (iKey == parCoordID && iKey != predecessorID && iKey != successorID){
					ArrayList<ArrayList<ArrayList<Vec3f>>> pointContainer;
					ArrayList<ArrayList<Vec3f>> heatMapPoints = hashIDTypeToViewToPointLists.get(idType).get(heatMapID);
					ArrayList<ArrayList<Vec3f>> parCoordPoints = hashIDTypeToViewToPointLists.get(idType).get(parCoordID);
					pointContainer = getPredecessorAndSuccessorPoints(PARCOORDS, heatMapPoints, parCoordPoints);
					ArrayList<ArrayList<Vec3f>> optimalHeatMapSuccessor = pointContainer.get(1);
					ArrayList<ArrayList<Vec3f>> optimalParCoordPredecessor = pointContainer.get(0);

					VisLinkAnimationStage heatMapLines = new VisLinkAnimationStage();
					VisLinkAnimationStage bundling = new VisLinkAnimationStage(true);
					VisLinkAnimationStage parCoordLines = new VisLinkAnimationStage(true);
					Vec3f pCCenterPoint = calculateCenter(optimalParCoordPredecessor); 
					Vec3f hMCenterPoint = calculateCenter(optimalHeatMapSuccessor);
					Vec3f controlPoint = calculateControlPoint(pCCenterPoint, hMCenterPoint);
					Vec3f hMBundlingPoint = calculateBundlingPoint(hMCenterPoint, controlPoint);
					Vec3f pCBundlingPoint = calculateBundlingPoint(pCCenterPoint, controlPoint);
					
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : optimalHeatMapSuccessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						heatMapLines.addLine( createControlPoints( hMBundlingPoint, currentPoint, hMCenterPoint ) );
	
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : optimalParCoordPredecessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						parCoordLines.addLine( createControlPoints( pCBundlingPoint, currentPoint, pCCenterPoint ) );

					bundling.addLine(createControlPoints(hMBundlingPoint, pCBundlingPoint, controlPoint));
					
					connectionLinesAllViews.add(heatMapLines);
					connectionLinesAllViews.add(bundling);
					connectionLinesAllViews.add(parCoordLines);
				}
			}
		}
		else if (type == PARCOORDS){
			for (Integer iKey : viewsToBeVisited){
				VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
				VisLinkAnimationStage connectionLinesOtherView = new VisLinkAnimationStage();
				Vec3f vecViewBundlingPoint = null;
				//heatmap predeceeds or succeeds middle view
				if (iKey == heatMapID  && (iKey == predecessorID || iKey == successorID)){
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					Vec3f controlPoint = null;
					if (activeViewID == heatMapID){
						controlPoint = calculateControlPoint(calculateCenter(heatmapSuccessor), calculateCenter(parCoordsPredecessor));
						vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
					}
					else{
						controlPoint = calculateControlPoint(calculateCenter(heatmapPredecessor), calculateCenter(parCoordsSuccessor));
						vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);
					}
					if (activeViewID == heatMapID){
						for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor) {
							if (alCurrentPoints.size() > 1) {
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							}
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
					}
					else{
						for (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor) {
							if (alCurrentPoints.size() > 1) {
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							}
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
					}
					for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {
						if(activeViewID != -1 && iKey == activeViewID){
							if (activeViewID == heatMapID){
								connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(heatmapSuccessor) ) );
								connectionLinesActiveView.setReverseLineDrawingDirection(true);
							}
						}
						else
							connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(heatmapPredecessor)) );
					}
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint), calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint), controlPoint));
					connectionLinesAllViews.add(connectionLinesOtherView);
					connectionLinesOtherView = new VisLinkAnimationStage(true);
					for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						connectionLinesOtherView.addLine( createControlPoints( calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint), currentPoint, controlPoint) );					
						connectionLinesAllViews.add(connectionLinesOtherView);
				}
				//pathway predeceed middle view
				else if (iKey != heatMapID && (iKey == predecessorID)){
					Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(activeViewID), calculateCenter(parCoordsPredecessor));
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), controlPoint);
					
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey))
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {	
						if (iKey == activeViewID)
							connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
						else
							connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
					}
	
					connectionLinesAllViews.add(connectionLinesActiveView);
					connectionLinesActiveView = new VisLinkAnimationStage(true);
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
							connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, controlPoint ) );
					connectionLinesAllViews.add(connectionLinesActiveView);
				}
				//pathway succeed middle view
				else if (iKey != heatMapID && iKey == successorID){
					Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(successorID), calculateCenter(parCoordsSuccessor));
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), controlPoint);
					connectionLinesOtherView = new VisLinkAnimationStage(true);
					
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey))
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))	
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
					
					VisLinkAnimationStage connectionLinesToCenter = new VisLinkAnimationStage();
					ArrayList<ArrayList<Vec3f>> centerPoints = null;
					if (multiplePoints)
						centerPoints = getMultipleCenterPoints(PARCOORDS, hashIDTypeToViewToPointLists.get(idType).get(parCoordID), vecViewBundlingPoint);
					else
						centerPoints = getCenterPoints(hashIDTypeToViewToPointLists.get(idType).get(parCoordID), vecViewBundlingPoint);
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : centerPoints)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						connectionLinesToCenter.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, controlPoint ) );
					connectionLinesAllViews.add(connectionLinesToCenter);
					connectionLinesAllViews.add(connectionLinesOtherView);
					
				}
				//pathway that is neither successor nor predecessor of middle view
				else if (iKey != parCoordID && iKey != heatMapID && iKey != predecessorID && iKey != successorID){
					Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(viewsToBeVisited.get(3)), calculateCenter(heatmapPredecessor));
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), controlPoint);
					connectionLinesOtherView = new VisLinkAnimationStage(true);
					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey))
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))	
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
					
					VisLinkAnimationStage connectionLinesToCenter = new VisLinkAnimationStage();
					ArrayList<ArrayList<Vec3f>> centerPoints = null;
					if (multiplePoints)
						centerPoints = getMultipleCenterPoints(PARCOORDS, hashIDTypeToViewToPointLists.get(idType).get(parCoordID), vecViewBundlingPoint);
					else
						centerPoints = getCenterPoints(hashIDTypeToViewToPointLists.get(idType).get(parCoordID), vecViewBundlingPoint);
					//controlPoint = calculateControlPoint(calculateCenter(centerPoints), vecViewBundlingPoint);
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : centerPoints)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						connectionLinesToCenter.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, controlPoint ) );
					connectionLinesAllViews.add(connectionLinesToCenter);
					connectionLinesAllViews.add(connectionLinesOtherView);
				}				
				//heatmap neither successor nor predecessor of middle view
				if (iKey == heatMapID && iKey != predecessorID && iKey != successorID){
					ArrayList<ArrayList<ArrayList<Vec3f>>> pointContainer;
					ArrayList<ArrayList<Vec3f>> heatMapPoints = hashIDTypeToViewToPointLists.get(idType).get(heatMapID);
					ArrayList<ArrayList<Vec3f>> parCoordPoints = hashIDTypeToViewToPointLists.get(idType).get(parCoordID);
					pointContainer = getPredecessorAndSuccessorPoints(HEATMAP, heatMapPoints, parCoordPoints);
					ArrayList<ArrayList<Vec3f>> optimalParCoordSuccessor = pointContainer.get(1);
					ArrayList<ArrayList<Vec3f>> optimalHeatMapPredecessor = pointContainer.get(0);

					VisLinkAnimationStage parCoordLines = new VisLinkAnimationStage();
					VisLinkAnimationStage heatMapLines = new VisLinkAnimationStage(true);
					VisLinkAnimationStage bundling = new VisLinkAnimationStage(true);
					Vec3f heatMapCenterPoint = calculateCenter(optimalHeatMapPredecessor);
					Vec3f parCoordCenterPoint = calculateCenter(optimalParCoordSuccessor);
					Vec3f controlPoint = calculateControlPoint(parCoordCenterPoint, heatMapCenterPoint);
					Vec3f heatMapBundlingPoint = calculateBundlingPoint(heatMapCenterPoint, controlPoint);
					Vec3f parCoordBundlingPoint = calculateBundlingPoint(parCoordCenterPoint, controlPoint);

					ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : optimalHeatMapPredecessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						heatMapLines.addLine( createControlPoints( heatMapBundlingPoint, currentPoint,  heatMapCenterPoint) );
	
					pointsToDepthSort = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> alCurrentPoints : optimalParCoordSuccessor)
						pointsToDepthSort.add(alCurrentPoints.get(0));
					for(Vec3f currentPoint : depthSort(pointsToDepthSort))
						parCoordLines.addLine( createControlPoints( parCoordBundlingPoint, currentPoint, parCoordCenterPoint ) );
					bundling.addLine(createControlPoints(parCoordBundlingPoint, heatMapBundlingPoint, controlPoint));
					
					connectionLinesAllViews.add(parCoordLines);
					connectionLinesAllViews.add(bundling);
					connectionLinesAllViews.add(heatMapLines);
				}
			}
		}
		return connectionLinesAllViews;
	}

	
	/** calculate the optimal points between heatmap and parcoordws (needed for special case)
	 * 
	 * @param typeOfSuccessor type of view that succeeds the center view
	 * @param heatMapPoints list of heatmap points
	 * @param parCoordPoints list of parcoord points
	 * @return optimal heatmap and parcoord points
	 */
	private ArrayList<ArrayList<ArrayList<Vec3f>>> getPredecessorAndSuccessorPoints(
		char typeOfSuccessor, ArrayList<ArrayList<Vec3f>> heatMapPoints, ArrayList<ArrayList<Vec3f>> parCoordPoints) {

		ArrayList<ArrayList<ArrayList<Vec3f>>> container = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		ArrayList<ArrayList<Vec3f>> optimalHeatMapPoints = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> optimalParCoordPoints = new ArrayList<ArrayList<Vec3f>>();
		
		
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;					
		if (multiplePoints){		
			ArrayList<ArrayList<ArrayList<Vec3f>>> multipleHeatMapPoints =
				new ArrayList<ArrayList<ArrayList<Vec3f>>>();
			ArrayList<ArrayList<ArrayList<Vec3f>>> multipleParCoordPoints =
				new ArrayList<ArrayList<ArrayList<Vec3f>>>();
			ArrayList<Vec3f> heatMapCenterPoints = new ArrayList<Vec3f>();
			ArrayList<Vec3f> parCoordCenterPoints = new ArrayList<Vec3f>();

			for (int count = 0; count < PARCOORDELEMENTS; count++)
				multipleParCoordPoints.add(new ArrayList<ArrayList<Vec3f>>());
			for (int count = 0; count < HEATMAPELEMENTS; count++)
				multipleHeatMapPoints.add(new ArrayList<ArrayList<Vec3f>>());

			for (int pointCount = 0; pointCount < heatMapPoints.size(); pointCount++)
				multipleHeatMapPoints.get(pointCount % HEATMAPELEMENTS).add(heatMapPoints.get(pointCount));
			for (int pointCount = 0; pointCount < parCoordPoints.size(); pointCount++)
				multipleParCoordPoints.get(pointCount % PARCOORDELEMENTS).add(parCoordPoints.get(pointCount));

			for (int count = 0; count < multipleHeatMapPoints.size(); count++)
				heatMapCenterPoints.add(calculateCenter(multipleHeatMapPoints.get(count)));
			for (int count = 0; count < multipleParCoordPoints.size(); count++)
				parCoordCenterPoints.add(calculateCenter(multipleParCoordPoints.get(count)));
			
			
			for (Vec3f hMPoint : heatMapCenterPoints) {
				for (Vec3f pCPoint : parCoordCenterPoints) {
					Vec3f distanceVec = hMPoint.minus(pCPoint);
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalParCoordPoints = multipleParCoordPoints.get(parCoordCenterPoints.indexOf(pCPoint));
						optimalHeatMapPoints = multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(hMPoint));
					}
				}				
			}					
		}
		else{
			ArrayList<Vec3f> optimalHMPoint = new ArrayList<Vec3f>();
			ArrayList<Vec3f> optimalPCPoint = new ArrayList<Vec3f>();
			for (ArrayList<Vec3f> hMPoint : heatMapPoints) {
				for (ArrayList<Vec3f> pCPoint : parCoordPoints) {
					Vec3f distanceVec = hMPoint.get(0).minus(pCPoint.get(0));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						
						optimalPCPoint = pCPoint;
						optimalHMPoint  = hMPoint;
					}
				}				
			}
			optimalHeatMapPoints.add(optimalHMPoint);
			optimalParCoordPoints.add(optimalPCPoint);
		}
		

		if (typeOfSuccessor == PARCOORDS){
			container.add(optimalParCoordPoints);
			container.add(optimalHeatMapPoints);
		}
		else{
			container.add(optimalHeatMapPoints);
			container.add(optimalParCoordPoints);
		}
		return container;
	}

	/** get a point of center lying dynamic view if needed
	 * 
	 * @param pointsList list of dynamic view points
	 * @param vecViewBundlingPoint bundling point of remote view
	 * @return the optimal point
	 */
	private ArrayList<ArrayList<Vec3f>> getCenterPoints(ArrayList<ArrayList<Vec3f>> pointsList,
		Vec3f vecViewBundlingPoint) {
		
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;
		ArrayList<ArrayList<Vec3f>> point = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Vec3f> optimalPoints = null;
		for (ArrayList<Vec3f> dynamicPoints : pointsList) {
			Vec3f predecessorPoint = dynamicPoints.get(0);
			Vec3f distanceToPredecessor = predecessorPoint.minus(vecViewBundlingPoint);
			currentPath = distanceToPredecessor.length();
			if (currentPath < minPath) {
				minPath = currentPath;
				optimalPoints = dynamicPoints;
			}
		}		
		point.add(optimalPoints);
		return point;
	}

	
	/** get a list of optimal points from a centered dynamic view 
	 * @param type 
	 * 
	 * @param pointsList list of points
	 * @param vecViewBundlingPoint bundling point of remote view
	 * @return the list of optimal points
	 */
	private ArrayList<ArrayList<Vec3f>> getMultipleCenterPoints(char type, ArrayList<ArrayList<Vec3f>> pointsList,
		Vec3f vecViewBundlingPoint) {
		
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;
		ArrayList<ArrayList<ArrayList<Vec3f>>> multiplePoints =
			new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		ArrayList<Vec3f> centerPoints = new ArrayList<Vec3f>();
		int nrElements;
		if (type == PARCOORDS)
			nrElements = PARCOORDELEMENTS;
		else
			nrElements = HEATMAPELEMENTS;

		for (int count = 0; count < nrElements; count++)
			multiplePoints.add(new ArrayList<ArrayList<Vec3f>>());

		for (int pointCount = 0; pointCount < pointsList.size(); pointCount++)
			multiplePoints.get(pointCount % nrElements).add(pointsList.get(pointCount));

		for (int count = 0; count < multiplePoints.size(); count++)
			centerPoints.add(calculateCenter(multiplePoints.get(count)));

		ArrayList<ArrayList<Vec3f>> optimalPoints = null;
		for (Vec3f dynamicPoint : centerPoints) {
			Vec3f distanceToPredecessor = dynamicPoint.minus(vecViewBundlingPoint);
			currentPath = distanceToPredecessor.length();
			if (currentPath < minPath) {
				minPath = currentPath;
				optimalPoints = multiplePoints.get(centerPoints.indexOf(dynamicPoint));
			}
		}
		return  optimalPoints;
	}

	/** calculate lines if rendered from stack and a pathway resides in focus level
	 * 
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param viewsToBeVisited list of views that belong to the current graph
	 * @param hashViewToCenterPoint list of center points
	 * @return the vislink container
	 */
	private ArrayList<VisLinkAnimationStage> renderFromStackWithGapPathwayCentered(GL gl, EIDType idType, ArrayList<Integer> viewsToBeVisited, HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		VisLinkAnimationStage connectionLinesActiveView = new VisLinkAnimationStage();
		int centerID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
		
		vecCenter = getWeightedPointRenderFromStackPathwayCenterd(hashViewToCenterPoint);
		Vec3f centerPointBundling = calculateBundlingPoint(hashViewToCenterPoint.get(centerID), vecCenter);
		
		
		for (Integer iKey : viewsToBeVisited) {
			VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage();
			VisLinkAnimationStage connectionLinesOtherView = new VisLinkAnimationStage(true);
			Vec3f vecViewBundlingPoint = null;
			if (iKey == parCoordID){
				if (activeViewID == parCoordID)
					vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
				else
					vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter);
			}
			else if (iKey == heatMapID){
				if (activeViewID == heatMapID)
					vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
				else
					vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter);
			}
			else
				vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
			
			ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();

			if (iKey == parCoordID){
				if (activeViewID == parCoordID){
					for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor) {
						if (alCurrentPoints.size() > 1) {
							renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
						}
						else
							pointsToDepthSort.add(alCurrentPoints.get(0));
					}
				}
				else{
					for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor) {
						if (alCurrentPoints.size() > 1) {
							renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
						}
						else
							pointsToDepthSort.add(alCurrentPoints.get(0));
					}
				}
			}
			else if (iKey == heatMapID){
				if (activeViewID == heatMapID){
					for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor) {
						if (alCurrentPoints.size() > 1) {
							renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
						}
						else
							pointsToDepthSort.add(alCurrentPoints.get(0));
					}
				}
				else{
					for (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor) {
						if (alCurrentPoints.size() > 1) {
							renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
						}
						else
							pointsToDepthSort.add(alCurrentPoints.get(0));
					}
				}
			}
			else{
				for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
					if (alCurrentPoints.size() > 1) {
						renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
					}
					else
						pointsToDepthSort.add(alCurrentPoints.get(0));
				}
			}

			for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {
				if(activeViewID != -1 && iKey == activeViewID){
					if (activeViewID == heatMapID)
						connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(heatmapSuccessor) ) );
					else if (activeViewID == parCoordID)
						connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(parCoordsSuccessor) ) );
					else
					connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );					
				}
				else{
					if (iKey == parCoordID)
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(parCoordsPredecessor)) );
					else if (iKey == heatMapID)
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, calculateCenter(heatmapPredecessor) ) );
					else
						connectionLinesOtherView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				}
			}
		
			if (iKey == activeViewID){
				connectionLinesAllViews.add(connectionLinesActiveView);
				bundlingLine.setReverseLineDrawingDirection(true);
				bundlingLine.addLine(createControlPoints(vecViewBundlingPoint, centerPointBundling, vecCenter));
				connectionLinesAllViews.add(bundlingLine);
			}
			else{
				if (iKey != centerID){
					bundlingLine.addLine(createControlPoints(vecViewBundlingPoint, centerPointBundling, vecCenter));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(connectionLinesOtherView);
				}
				else{
					connectionLinesAllViews.add(connectionLinesOtherView);
				}
			}
		}		
		return connectionLinesAllViews;
	}

	/** render vislink if rendered from the stack and a pathway is the active view
	 * 
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param hashViewToCenterPoint list of center points
	 * @param controlPoint control point for calculating bundling line
	 * @return the vislink container
	 */
	private ArrayList<VisLinkAnimationStage> renderPathWayActive(GL gl, EIDType idType,
		HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f controlPoint) {
		VisLinkAnimationStage activeStage = null;
		
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(activeViewID), controlPoint);
		
		activeStage =
			renderLinesOfCurrentStage(gl, idType, activeViewID, vecViewBundlingPoint, controlPoint,
				PATHWAY, hashViewToCenterPoint);
		connectionLinesAllViews.add(activeStage);
		
		if (gapSuccessorID == parCoordID){
			getlinesOfDynamicViewActiveViewOnStack(gl, PARCOORDS, vecViewBundlingPoint, controlPoint, connectionLinesAllViews);
			getlinesOfDynamicViewActiveViewOnStack(gl, HEATMAP, vecViewBundlingPoint, controlPoint, connectionLinesAllViews);
		}
		else{
			getlinesOfDynamicViewActiveViewOnStack(gl, HEATMAP, vecViewBundlingPoint, controlPoint, connectionLinesAllViews);
			getlinesOfDynamicViewActiveViewOnStack(gl, PARCOORDS, vecViewBundlingPoint, controlPoint, connectionLinesAllViews);
		}
		return connectionLinesAllViews;
	}

	
	/** renderl lines if a gap exists, the graph is rendered starting at a stack level and a pathway is the active view
	 * 
	 * @param gl the gl object
	 * @param specialViewType type of the special view (heatmap/parcoords)
	 * @param connectToPoint bundling point of active view
	 * @param controlPoint the global control point for calculating the curve
	 * @param connectionLinesAllViews the vislink container
	 */
	private void getlinesOfDynamicViewActiveViewOnStack(GL gl, char specialViewType, Vec3f connectToPoint,
		Vec3f controlPoint, ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {
		
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;
		ArrayList<ArrayList<Vec3f>> pointsList = null;
		if (controlPoint == null)
			return;

		if (specialViewType == PARCOORDS)
			pointsList = heatmapPredecessor;
		else
			pointsList = parCoordsPredecessor;
		
		// calculating control points and local bundling points
		Vec3f vecViewBundlingPoint = calculateBundlingPoint(calculateCenter(pointsList), controlPoint);

		if (multiplePoints) {
			if (specialViewType == PARCOORDS)
				currentStage = renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint, HEATMAP, null);
			else
				currentStage = renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint, PARCOORDS, null);

			bundling = new VisLinkAnimationStage(true);
			bundling.addLine(createControlPoints(connectToPoint, vecViewBundlingPoint, controlPoint));
			
			
			connectionLinesAllViews.add(bundling);
			connectionLinesAllViews.add(currentStage);

		}
		else {
			// calculating bundling line
			bundling = new VisLinkAnimationStage(true);

			bundling.addLine(createControlPoints(connectToPoint, calculateCenter(pointsList),
				controlPoint));
			connectionLinesAllViews.add(bundling);
		}
		
	}

	/** render vislink if rendered from the stack and a either heatmap or parcoord is the active view
	 * 
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param viewsToBeVisited list of views that belong to the current graph
	 * @param hashViewToCenterPoint list of center points
	 * @param controlPoint control point for calculating bundling line
	 * @return the vislink container
	 */
	private ArrayList<VisLinkAnimationStage> renderDyamicViewActive(GL gl, EIDType idType, ArrayList<Integer> viewsToBeVisited, HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f controlPoint) {
		
		Vec3f connectionToOtherDynamic = null;
		Vec3f connectionToPathway = null;

		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		char specialViewType = 0;
		
		
		//check where dynamic views lie 
		if (heatMapID == activeViewID && parCoordID != gapSuccessorID){
			specialViewType = PARCOORDS;
			if (multiplePoints){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
			}
			else{
				connectionToOtherDynamic = parCoordsSuccessor.get(0).get(0);
				connectionToPathway = heatmapSuccessor.get(0).get(0);
			}
		}
		else if (parCoordID == activeViewID && heatMapID != gapSuccessorID){
			specialViewType = HEATMAP;
			if (multiplePoints){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
			}
			else{
				connectionToOtherDynamic = heatmapSuccessor.get(0).get(0);
				connectionToPathway = parCoordsSuccessor.get(0).get(0);
			}
		}
		else if (heatMapID == activeViewID && parCoordID == gapSuccessorID){
			specialViewType = PARCOORDS;
			if (multiplePoints){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);
			}
			else{
				connectionToOtherDynamic = parCoordsPredecessor.get(0).get(0);
				connectionToPathway = heatmapPredecessor.get(0).get(0);
			}
		}
		else{
			specialViewType = HEATMAP;
			if (multiplePoints){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);
			}
			else{
				connectionToOtherDynamic = heatmapPredecessor.get(0).get(0);
				connectionToPathway = parCoordsPredecessor.get(0).get(0);
			}
		}


		if (parCoordID == gapSuccessorID || heatMapID == gapSuccessorID){
			getLineOfPathwayActiveViewOnStack(gl, idType, viewsToBeVisited.get(2), hashViewToCenterPoint, connectionToPathway, connectionLinesAllViews);
			getlinesOfOtherDynamicViewActiveViewOnStack(gl, specialViewType, connectionToOtherDynamic, connectionLinesAllViews);
		}
		else{
			getLineOfPathwayActiveViewOnStack(gl, idType, viewsToBeVisited.get(1), hashViewToCenterPoint, connectionToPathway, connectionLinesAllViews);
			getlinesOfOtherDynamicViewActiveViewOnStack(gl, specialViewType, connectionToOtherDynamic, connectionLinesAllViews);
		}
		return connectionLinesAllViews;
	}

	
	/** get lines of dynamic view if the other one is the first view that is rendered
	 * 
	 * @param gl the gl object
	 * @param specialViewType type of dynamic view (either heatmap or parcoord)
	 * @param connectionToOtherDynamic bundling point
	 * @param connectionLinesAllViews the vislink container
	 * @param isPathwayActive indicator if a pathway is the active view
	 */
	private void getlinesOfOtherDynamicViewActiveViewOnStack(GL gl, char specialViewType,
		Vec3f connectionToOtherDynamic, ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {
		
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;
		ArrayList<ArrayList<Vec3f>> pointsList = null;
		if (gapSuccessorID == heatMapID || gapSuccessorID == parCoordID){
			if (specialViewType == PARCOORDS)
				pointsList = heatmapSuccessor;
			else
				pointsList = parCoordsSuccessor;
		}
		else{
			if (specialViewType == PARCOORDS)
				pointsList = parCoordsPredecessor;
			else
				pointsList = heatmapPredecessor;
		}
		
		// calculating control points and local bundling points
		Vec3f controlPoint = calculateControlPoint(calculateCenter(pointsList), connectionToOtherDynamic);
		if (controlPoint == null)
			return;

		Vec3f vecViewBundlingPoint =
			calculateBundlingPoint(calculateCenter(pointsList), controlPoint);

		if (multiplePoints) {
			if (specialViewType == PARCOORDS)
				currentStage = renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint, HEATMAP, null);
			else
				currentStage = renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint, PARCOORDS, null);
			currentStage.setReverseLineDrawingDirection(true);

			bundling = new VisLinkAnimationStage(true);
			bundling
				.addLine(createControlPoints(connectionToOtherDynamic, vecViewBundlingPoint, controlPoint));
			connectionLinesAllViews.add(currentStage);
			bundling = new VisLinkAnimationStage(true);

			bundling.addLine(createControlPoints(connectionToOtherDynamic, calculateCenter(pointsList),
				controlPoint));
			connectionLinesAllViews.add(bundling);
		}
		else {
			// calculating bundling line
			bundling = new VisLinkAnimationStage(true);

			bundling.addLine(createControlPoints(connectionToOtherDynamic, calculateCenter(pointsList),
				controlPoint));
			connectionLinesAllViews.add(bundling);
		}
	}

	
	/** get lines of pathway if the active view is a dynamic view
	 * 
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param pathwayID id of the current pathway
	 * @param hashViewToCenterPoint list of center points
	 * @param connectionToPathway bundlin point
	 * @param connectionLinesAllViews the vislink container
	 */
	private void getLineOfPathwayActiveViewOnStack(GL gl, EIDType idType, Integer pathwayID, HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f connectionToPathway, ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {
		
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;
		
		Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(pathwayID), connectionToPathway);
		if (controlPoint == null)
			return;
		Vec3f vecViewBundlingPoint =
			calculateBundlingPoint(hashViewToCenterPoint.get(pathwayID), controlPoint);

		currentStage =
			renderLinesOfCurrentStage(gl, idType, pathwayID, vecViewBundlingPoint, controlPoint,
				PATHWAY, hashViewToCenterPoint);

		// calculating bundling line
		bundling = new VisLinkAnimationStage();
		if (hashIDTypeToViewToPointLists.get(idType).get(pathwayID).size() > 1)
			bundling.addLine(createControlPoints(vecViewBundlingPoint, connectionToPathway, controlPoint));
		else
			bundling.addLine(createControlPoints(hashViewToCenterPoint.get(pathwayID), connectionToPathway,
				controlPoint));

		connectionLinesAllViews.add(bundling);

		if (hashIDTypeToViewToPointLists.get(idType).get(pathwayID).size() > 1) {
			currentStage.setReverseLineDrawingDirection(true);
			connectionLinesAllViews.add(currentStage);
			connectionToPathway = vecViewBundlingPoint;
		}
		
	}


	/** calculate weighted control point if pathway lies on focus
	 * 
	 * @param hashViewToCenterPoint
	 * @return
	 */
	private Vec3f getWeightedPointForCenteredPathway(HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<Vec3f> heatMapPointsList = new ArrayList<Vec3f>();
		ArrayList<Vec3f> parCoordsPointsList = new ArrayList<Vec3f>();
		ArrayList<Vec3f> centerPathwayList = new ArrayList<Vec3f>();
		Vec3f heatmapCenter = calculateCenter(heatmapPredecessor);
		Vec3f parCoordsCenter = calculateCenter(parCoordsPredecessor);
		heatMapPointsList.add(heatmapCenter);
		parCoordsPointsList.add(parCoordsCenter);
		centerPathwayList.add(hashViewToCenterPoint.get(activeViewID));
		ArrayList<ArrayList<Vec3f>> pointsCollection = new ArrayList<ArrayList<Vec3f>>();
		pointsCollection.add(heatMapPointsList);
		pointsCollection.add(parCoordsPointsList);
		pointsCollection.add(centerPathwayList);
		Vec3f centerPoint = calculateCenter(pointsCollection);
		return centerPoint;
	}
	
	
	/** calculate weighted point if rendering from stack, gap exists and pathway lies in focus
	 * 
	 * @param hashViewToCenterPoint list of center points
	 * @return the weighted control point
	 */
	private Vec3f getWeightedPointRenderFromStackPathwayCenterd(HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<Vec3f> heatMapPointsList = new ArrayList<Vec3f>();
		ArrayList<Vec3f> parCoordsPointsList = new ArrayList<Vec3f>();
		ArrayList<Vec3f> centerPathwayList = new ArrayList<Vec3f>();
		Vec3f heatmapCenter = calculateCenter(heatmapPredecessor);
		Vec3f parCoordsCenter = calculateCenter(parCoordsPredecessor);
		heatMapPointsList.add(heatmapCenter);
		parCoordsPointsList.add(parCoordsCenter);
		for (Vec3f point : hashViewToCenterPoint.values())
			centerPathwayList.add(point);
		ArrayList<ArrayList<Vec3f>> pointsCollection = new ArrayList<ArrayList<Vec3f>>();
		pointsCollection.add(heatMapPointsList);
		pointsCollection.add(parCoordsPointsList);
		pointsCollection.add(centerPathwayList);
		Vec3f centerPoint = calculateCenter(pointsCollection);
		return centerPoint;
	}
	
	
	/** render lines if a gap occurs, a pathway is in focus and the vislink is rendered from the center
	 * 
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param hashViewToCenterPoint list of center points except parcoords and heatmap
	 * @param viewsToBeVisited list of views that belong to the graph sorted by order of visiting
	 * @param centerPoint the weighted control point
	 * @return the vislink container
	 */
	private ArrayList<VisLinkAnimationStage> renderPathwayCentered(GL gl, EIDType idType,
		HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<Integer> viewsToBeVisited, Vec3f centerPoint) {

		Vec3f connectionParCoordsPathway = null;
		Vec3f connectionHeatMapPathway = null;
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;
		
		if (multiplePoints){
			connectionParCoordsPathway = calculateBundlingPoint(calculateCenter(heatmapPredecessor), centerPoint);
			connectionHeatMapPathway = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), centerPoint);
		}
		else{
			connectionParCoordsPathway = parCoordsPredecessor.get(0).get(0);
			connectionHeatMapPathway = heatmapPredecessor.get(0).get(0);
		}
		Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(activeViewID), centerPoint);
		
		currentStage =
			renderLinesOfCurrentStage(gl, idType, activeViewID, vecViewBundlingPoint, centerPoint,
				PATHWAY, hashViewToCenterPoint);
		connectionLinesAllViews.add(currentStage);
		currentStage = null;
		
		if (gapPredecessorID == parCoordID){
			getLinesOfOtherDynamicViewPathWayCentered(gl, PARCOORDS, connectionParCoordsPathway, vecViewBundlingPoint, bundling, currentStage, connectionLinesAllViews, centerPoint);
			getLinesOfOtherDynamicViewPathWayCentered(gl, HEATMAP, connectionHeatMapPathway, vecViewBundlingPoint, bundling, currentStage, connectionLinesAllViews, centerPoint);
		}
		else{
			getLinesOfOtherDynamicViewPathWayCentered(gl, HEATMAP, connectionHeatMapPathway, vecViewBundlingPoint, bundling, currentStage, connectionLinesAllViews, centerPoint);
			getLinesOfOtherDynamicViewPathWayCentered(gl, PARCOORDS, connectionParCoordsPathway, vecViewBundlingPoint, bundling, currentStage, connectionLinesAllViews, centerPoint);
			
		}

		return connectionLinesAllViews;
	}

	
	/** calcaulate vislinks of heatmap and parcoords if a pathway is in center, a gap exists and it is rendered from the center
	 * 
	 * @param gl the gl object
	 * @param type type of the special view (either heatmap or parcoord)
	 * @param connectionToPathway the local bundling point of the special view
	 * @param pathwayBundling the bundling point of the centered pathway
	 * @param bundling the bundling vislink between the special view and the centered pathway
	 * @param currentStage the local vislink of the special view
	 * @param connectionLinesAllViews the vislink container
	 * @param centerPoint the weighted control point
	 */
	private void getLinesOfOtherDynamicViewPathWayCentered(GL gl, char type,
		Vec3f connectionToPathway, Vec3f pathwayBundling, VisLinkAnimationStage bundling, VisLinkAnimationStage currentStage,
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews, Vec3f centerPoint) {
		
		
		ArrayList<ArrayList<Vec3f>> pointsList = null;
		if (type == PARCOORDS){
			pointsList = parCoordsPredecessor;
		}
		else{
			pointsList = heatmapPredecessor;
		}
		
		if (pointsList.size() > 0) {
			// calculating control points and local bundling points
			Vec3f controlPoint = calculateControlPoint(connectionToPathway, pathwayBundling);
			if (controlPoint == null)
				return;

			if (multiplePoints) {
				if (type == PARCOORDS)
					currentStage = renderLinesOfCurrentStage(gl, null, -1, connectionToPathway, controlPoint, HEATMAP, null);
				else
					currentStage = renderLinesOfCurrentStage(gl, null, -1, connectionToPathway, controlPoint, PARCOORDS, null);

				bundling = new VisLinkAnimationStage();
				bundling
					.addLine(createControlPoints(connectionToPathway, pathwayBundling, controlPoint));
				connectionLinesAllViews.add(bundling);
				connectionLinesAllViews.add(currentStage);
			}
			else {
				// calculating bundling line
				bundling = new VisLinkAnimationStage();

				bundling.addLine(createControlPoints(connectionToPathway, pathwayBundling,
					controlPoint));
				connectionLinesAllViews.add(bundling);
			}
		}
		
	}

	
	/** render lines if gap is available, it is rendered from the center and a special view lies at that center position
	 * 
	 * @param gl the gl object 
	 * @param idType type of genome
	 * @param type type of view that is in center
	 * @param otherDynamicViewBeforeGap tells if the other dynamic view is before the gap
	 * @param hashViewToCenterPoint list of center points
	 * @param viewsToBeVisited list of views that belong to the current vislink
	 * @param controlPoint the local control point
	 * @return the vislink container
	 */
	private ArrayList<VisLinkAnimationStage> renderDynamicViewCentered(GL gl, EIDType idType, char type,
		boolean otherDynamicViewBeforeGap, HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<Integer> viewsToBeVisited, Vec3f controlPoint) {
		
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		VisLinkAnimationStage currentStage = null;
		VisLinkAnimationStage bundling = null;

		Vec3f connectionToOtherDynamic = null;
		Vec3f connectionToPathway = null;
		if (multiplePoints){
			if (type == PARCOORDS && otherDynamicViewBeforeGap){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
			}
			else if (type == PARCOORDS && !otherDynamicViewBeforeGap){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(parCoordsPredecessor), controlPoint);
			}
			else if (type == HEATMAP && otherDynamicViewBeforeGap){
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
			}
			else{
				connectionToOtherDynamic = calculateBundlingPoint(calculateCenter(heatmapSuccessor), controlPoint);
				connectionToPathway = calculateBundlingPoint(calculateCenter(heatmapPredecessor), controlPoint);
			}
		}
		else{
			if (type == PARCOORDS && otherDynamicViewBeforeGap){
				connectionToOtherDynamic = calculateCenter(parCoordsPredecessor);
				connectionToPathway = parCoordsSuccessor.get(0).get(0);
			}
			else if (type == PARCOORDS && !otherDynamicViewBeforeGap){
				connectionToOtherDynamic = calculateCenter(parCoordsSuccessor);
				connectionToPathway = parCoordsPredecessor.get(0).get(0);
			}
			else if (type == HEATMAP && otherDynamicViewBeforeGap){
				connectionToOtherDynamic = calculateCenter(heatmapPredecessor);
				connectionToPathway = heatmapSuccessor.get(0).get(0);
			}
			else{
				connectionToOtherDynamic = calculateCenter(heatmapSuccessor);
				connectionToPathway = heatmapPredecessor.get(0).get(0);
			}
		}

		if (otherDynamicViewBeforeGap){
			if (type == PARCOORDS){
				getlinesOfOtherDynamicView(gl, HEATMAP, connectionToOtherDynamic, bundling, currentStage, connectionLinesAllViews);
				getLinesOfPathway(gl, idType, hashViewToCenterPoint, connectionToPathway, viewsToBeVisited.get(2), bundling, currentStage, connectionLinesAllViews);
			}
			else{
				getlinesOfOtherDynamicView(gl, PARCOORDS, connectionToOtherDynamic, bundling, currentStage, connectionLinesAllViews);
				getLinesOfPathway(gl, idType, hashViewToCenterPoint, connectionToPathway, viewsToBeVisited.get(2), bundling, currentStage, connectionLinesAllViews);
			}
		}
		else{
			if (type == PARCOORDS){
				getLinesOfPathway(gl, idType, hashViewToCenterPoint, connectionToPathway, viewsToBeVisited.get(1), bundling, currentStage, connectionLinesAllViews);		
				getlinesOfOtherDynamicView(gl, HEATMAP, connectionToOtherDynamic, bundling, currentStage, connectionLinesAllViews);
			}
			else{
				getLinesOfPathway(gl, idType, hashViewToCenterPoint, connectionToPathway, viewsToBeVisited.get(1), bundling, currentStage, connectionLinesAllViews);		
				getlinesOfOtherDynamicView(gl, PARCOORDS, connectionToOtherDynamic, bundling, currentStage, connectionLinesAllViews);
			}
		}
		return connectionLinesAllViews;
	}

	
	/** get lines of pathway if gap and pathway is on stack
	 *  
	 * @param gl the gl object
	 * @param idType type of genome
	 * @param hashViewToCenterPoint list of center points
	 * @param connectionToPathway bundling point of center view
	 * @param pathwayID id of current pathway
	 * @param bundling bundling line between center view and current pathway
	 * @param currentStage local lines of current pathway
	 * @param connectionLinesAllViews the vislink container
	 */
	private void getLinesOfPathway(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f connectionToPathway, int pathwayID, VisLinkAnimationStage bundling,
		VisLinkAnimationStage currentStage, ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {

		Vec3f controlPoint = calculateControlPoint(hashViewToCenterPoint.get(pathwayID), connectionToPathway);
		if (controlPoint == null)
			return;
		Vec3f vecViewBundlingPoint =
			calculateBundlingPoint(hashViewToCenterPoint.get(pathwayID), controlPoint);

		currentStage =
			renderLinesOfCurrentStage(gl, idType, pathwayID, vecViewBundlingPoint, controlPoint,
				PATHWAY, hashViewToCenterPoint);

		// calculating bundling line
		bundling = new VisLinkAnimationStage();
		if (hashIDTypeToViewToPointLists.get(idType).get(pathwayID).size() > 1)
			bundling.addLine(createControlPoints(vecViewBundlingPoint, connectionToPathway, controlPoint));
		else
			bundling.addLine(createControlPoints(hashViewToCenterPoint.get(pathwayID), connectionToPathway,
				controlPoint));

		connectionLinesAllViews.add(bundling);

		if (hashIDTypeToViewToPointLists.get(idType).get(pathwayID).size() > 1) {
			currentStage.setReverseLineDrawingDirection(true);
			connectionLinesAllViews.add(currentStage);
			connectionToPathway = vecViewBundlingPoint;
		}
	}

	
	/** get lines if special view is in center, a gap exists and current special view is on stack
	 * 
	 * @param gl the gl object
	 * @param type type of special view
	 * @param connectionToOtherDynamic bundling point of centered view
	 * @param bundling bundling line between center view and current view
	 * @param currentStage local lines of current view
	 * @param connectionLinesAllViews the vislink container
	 */
	private void getlinesOfOtherDynamicView(GL gl, char type, Vec3f connectionToOtherDynamic, VisLinkAnimationStage bundling,
		VisLinkAnimationStage currentStage, ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {
		
		ArrayList<ArrayList<Vec3f>> pointsList = null;
		ArrayList<ArrayList<Vec3f>> reversePointsList = null;
		if (type == PARCOORDS){
			pointsList = parCoordsPredecessor;
			reversePointsList = heatmapSuccessor;
		}
		else{
			pointsList = heatmapPredecessor;
			reversePointsList = parCoordsSuccessor;
		}
		
		if (pointsList.size() > 0) {
			// calculating control points and local bundling points
			Vec3f controlPoint = calculateControlPoint(calculateCenter(pointsList), connectionToOtherDynamic);
			if (controlPoint == null)
				return;

			Vec3f vecViewBundlingPoint =
				calculateBundlingPoint(calculateCenter(pointsList), controlPoint);

			if (multiplePoints) {
				if (type == PARCOORDS)
					currentStage = renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint, HEATMAP, null);
				else
					currentStage = renderLinesOfCurrentStage(gl, null, -1, vecViewBundlingPoint, controlPoint, PARCOORDS, null);
				currentStage.setReverseLineDrawingDirection(true);

				bundling = new VisLinkAnimationStage();
				bundling
					.addLine(createControlPoints(connectionToOtherDynamic, vecViewBundlingPoint, controlPoint));
				connectionLinesAllViews.add(currentStage);

				currentStage = new VisLinkAnimationStage();
				ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
				for (ArrayList<Vec3f> alCurrentPoints : reversePointsList) {
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
			else {
				// calculating bundling line
				bundling = new VisLinkAnimationStage();

				bundling.addLine(createControlPoints(connectionToOtherDynamic, calculateCenter(pointsList),
					controlPoint));
				connectionLinesAllViews.add(bundling);
			}
		}
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

		boolean arePathwaysConsecutive = checkIfPathwaysConsecutive(viewsToBeVisited);
		
		for (Integer key : viewsToBeVisited) {
			if (key == heatMapID) {
				if (heatmapPredecessor.size() > 0) {
					// calculating control points and local bundling points
					/*if (arePathwaysConsecutive)
						controlPoint = globalControlPoint;
					else*/
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
								currentStage.addLine(createControlPoints(currentPoint, vecViewBundlingPoint, controlPoint));
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
				/*	if (arePathwaysConsecutive)
						controlPoint = globalControlPoint;
					else*/
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
				if (key != activeViewID){
					//get global bundling point
					if (arePathwaysConsecutive){
						//controlPoint = getWeightedControlPoint(hashViewToCenterPoint, src, viewsToBeVisited, key);
						hashViewToCenterPoint.put(1, src);
						controlPoint = calculateCenter(hashViewToCenterPoint.values());
						hashViewToCenterPoint.remove(1);
					}
					else
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), src);
				}
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

	/*private Vec3f getWeightedControlPoint(HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f src,
		ArrayList<Integer> viewsToBeVisited, Integer key) {
		ArrayList<Vec3f> pointList = new ArrayList<Vec3f>();
		pointList.add(src);
		pointList.add(hashViewToCenterPoint.get(key));
		int positionOfKey = viewsToBeVisited.indexOf(key);
		Vec3f controlPoint = null;
		if (positionOfKey != viewsToBeVisited.size()-1){
			int nextID = viewsToBeVisited.get(positionOfKey+1);
			pointList.add(hashViewToCenterPoint.get(nextID));
		}
		controlPoint = calculateCenter(pointList);
		return controlPoint;
	}*/

	private boolean checkIfPathwaysConsecutive(ArrayList<Integer> viewsToBeVisited) {
		
		for (int count = 1; count < viewsToBeVisited.size();  count++){ 
			if (viewsToBeVisited.get(count-1) != heatMapID && viewsToBeVisited.get(count-1) != parCoordID && viewsToBeVisited.get(count) != parCoordID && viewsToBeVisited.get(count) != heatMapID)
				return true;
		}
		return false;
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
							.getID())){
						gapPosition = 4;
						gapSuccessorID = stackLevel.getElementByPositionIndex(3).getGLView().getID();
					}
				}
			}
			else if (positionOfActiveView == 1 || positionOfActiveView == 3) {
				if ((stackLevel.getElementByPositionIndex(0).getGLView() != null)
					&& (stackLevel.getElementByPositionIndex(2).getGLView() != null)) {
					if (viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(0).getGLView()
						.getID())
						&& viewsOfCurrentPath.contains(stackLevel.getElementByPositionIndex(2).getGLView()
							.getID())){
						gapPosition = 4;
						if (positionOfActiveView == 1)
							gapSuccessorID = stackLevel.getElementByPositionIndex(2).getGLView().getID(); 
						else
							gapSuccessorID = stackLevel.getElementByPositionIndex(0).getGLView().getID();
					}
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

