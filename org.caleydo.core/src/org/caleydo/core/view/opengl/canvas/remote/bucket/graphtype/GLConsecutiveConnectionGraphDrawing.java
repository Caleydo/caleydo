package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GraphDrawingUtils;
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

	final static int PREDECESSOR = 0;
	final static int SUCCESSOR = 1;
	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;

	private ArrayList<ArrayList<Vec3f>> heatmapPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> heatmapSuccessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsSuccessor = new ArrayList<ArrayList<Vec3f>>();

	boolean gap = false;
	private int heatMapID = getSpecialViewID(HEATMAP);
	private int parCoordID = getSpecialViewID(PARCOORDS);

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
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		ArrayList<ArrayList<Vec3f>> heatMapPoints = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> parCoordsPoints = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Integer> viewsToBeVisited = null;
		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;
		int positionOfGap = -1;
		
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();

		int heatMapID = getSpecialViewID(HEATMAP);
		int parCoordID = getSpecialViewID(PARCOORDS);

		for (Integer iKey : keySet) {
			if (iKey.equals(heatMapID))
				heatMapPoints = hashIDTypeToViewToPointLists.get(idType).get(iKey);
			else if (iKey.equals(parCoordID))
				parCoordsPoints = hashIDTypeToViewToPointLists.get(idType).get(iKey);
			else
				hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}

		//getting list of views that belong to the current graph, sorted by sequence of visiting
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null && focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID)
			viewsToBeVisited = getViewsOfCurrentPathStartingAtFocus(hashViewToCenterPoint);
		else
			viewsToBeVisited = getViewsOfCurrentPathStartingAtStack(hashViewToCenterPoint);
		
		heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
		heatMapSuccessorID = getNext(viewsToBeVisited, heatMapID);
		parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
		parCoordsSuccessorID = getNext(viewsToBeVisited, parCoordID);
		
		//calculating optimal points for heatmap and/or parCoords
		getPointsFromHeatMapAndParCoords(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints, parCoordsPredecessorID, parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint);

		//getPosition Of Gap
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null && focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID)
			positionOfGap = checkIfGapAvailableWhenRenderingFromCenter(viewsToBeVisited);
		else
			positionOfGap = checkIfGapAvailableWhenRenderingFromStack(viewsToBeVisited);
		
		ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
		Vec3f vecViewBundlingPoint = null;
		Vec3f controlPoint = null;
		
		for (Integer key : viewsToBeVisited) {
			if (key != -1){	
				if (key == heatMapID){
					if (heatmapPredecessor.size() > 0){
						VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
						
						if (heatMapPredecessorID != parCoordID)
							controlPoint = calculateControlPoint(heatmapPredecessor.get(0).get(0), hashViewToCenterPoint.get(heatMapPredecessorID));
						else
							controlPoint = calculateControlPoint(heatmapPredecessor.get(0).get(0), parCoordsSuccessor.get(0).get(0));
						
						vecViewBundlingPoint = calculateBundlingPoint(heatmapPredecessor.get(0).get(0), controlPoint);
						for (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor) {
							if (alCurrentPoints.size() > 1)
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
						for (Vec3f currentPoint : depthSort(pointsToDepthSort))
							currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint, controlPoint));

						if (heatmapPredecessor.size() >1)
							connectionLinesAllViews.add(currentStage);
						pointsToDepthSort.clear();
						VisLinkAnimationStage bundling = new VisLinkAnimationStage();
						if (heatMapPredecessorID != parCoordID)
							bundling.addLine(createControlPoints(hashViewToCenterPoint.get(heatMapPredecessorID), heatmapPredecessor.get(0).get(0), controlPoint));
						else
							bundling.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), heatmapPredecessor.get(0).get(0), controlPoint));
						connectionLinesAllViews.add(bundling);
						
					}
					if (heatmapSuccessor.size() > 0){
						VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
						if (heatMapSuccessorID != parCoordID)
							controlPoint = calculateControlPoint(heatmapSuccessor.get(0).get(0), hashViewToCenterPoint.get(heatMapSuccessorID));
						else
							controlPoint = calculateControlPoint(heatmapSuccessor.get(0).get(0), parCoordsPredecessor.get(0).get(0));
						vecViewBundlingPoint = calculateBundlingPoint(heatmapSuccessor.get(0).get(0), controlPoint);
						for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor) {
							if (alCurrentPoints.size() > 1)
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
						for (Vec3f currentPoint : depthSort(pointsToDepthSort))
							currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint, heatmapSuccessor.get(0).get(0)));

						if (heatmapSuccessor.size() > 1)
							connectionLinesAllViews.add(currentStage);
						pointsToDepthSort.clear();
					}

				}
				else if (key == parCoordID){
					if (parCoordsPredecessor.size() > 0){
						VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
						if (parCoordsPredecessorID != heatMapID)
							controlPoint = calculateControlPoint(parCoordsPredecessor.get(0).get(0), hashViewToCenterPoint.get(parCoordsPredecessorID));
						else
							controlPoint = calculateControlPoint(parCoordsPredecessor.get(0).get(0), heatmapSuccessor.get(0).get(0));
						vecViewBundlingPoint = calculateBundlingPoint(parCoordsPredecessor.get(0).get(0), controlPoint);
						for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor) {
							if (alCurrentPoints.size() > 1)
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
						for (Vec3f currentPoint : depthSort(pointsToDepthSort))
								currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint, parCoordsPredecessor.get(0).get(0)));

						if (parCoordsPredecessor.size() > 1)
							connectionLinesAllViews.add(currentStage);
						pointsToDepthSort.clear();
						VisLinkAnimationStage bundling = new VisLinkAnimationStage();
						if (parCoordsPredecessorID == heatMapID)
							bundling.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), parCoordsPredecessor.get(0).get(0), controlPoint));
						else
							bundling.addLine(createControlPoints(hashViewToCenterPoint.get(parCoordsPredecessorID), parCoordsPredecessor.get(0).get(0), controlPoint));
						connectionLinesAllViews.add(bundling);
					}
					if (parCoordsSuccessor.size() > 0){
						VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
						
						if (parCoordsSuccessorID != heatMapID)
							controlPoint = calculateControlPoint(parCoordsSuccessor.get(0).get(0), hashViewToCenterPoint.get(parCoordsSuccessorID));
						else
							controlPoint = calculateControlPoint(parCoordsSuccessor.get(0).get(0), heatmapPredecessor.get(0).get(0));
						vecViewBundlingPoint = calculateBundlingPoint(parCoordsSuccessor.get(0).get(0), controlPoint);
						
						for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor) {
							if (alCurrentPoints.size() > 1)
								renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
							else
								pointsToDepthSort.add(alCurrentPoints.get(0));
						}
						for (Vec3f currentPoint : depthSort(pointsToDepthSort))
							currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint, parCoordsSuccessor.get(0).get(0)));

						if (parCoordsSuccessor.size() > 1)
							connectionLinesAllViews.add(currentStage);
						pointsToDepthSort.clear();
					}
				}
				else{
					VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
					if (key == parCoordsSuccessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), parCoordsSuccessor.get(0).get(0));
					else if (key == heatMapSuccessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), heatmapSuccessor.get(0).get(0));
					else if (key == parCoordsPredecessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), parCoordsPredecessor.get(0).get(0));
					else if (key == heatMapPredecessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), heatmapPredecessor.get(0).get(0));
					
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(key), controlPoint);
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(key)) {
						if (alCurrentPoints.size() > 1)
							renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
						else
							pointsToDepthSort.add(alCurrentPoints.get(0));
					}
					for (Vec3f currentPoint : depthSort(pointsToDepthSort))
						currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(key)));

					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
						connectionLinesAllViews.add(currentStage);
					VisLinkAnimationStage bundling = new VisLinkAnimationStage();
					if (key == heatMapSuccessorID){
						if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
							bundling.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), vecViewBundlingPoint, controlPoint));
						else
							bundling.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), hashViewToCenterPoint.get(key), controlPoint));
					}
					else if (key == parCoordsSuccessorID)
						if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
							bundling.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), vecViewBundlingPoint, controlPoint));
						else
							bundling.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), hashViewToCenterPoint.get(key), controlPoint));
					connectionLinesAllViews.add(bundling);
					pointsToDepthSort.clear();

				}
			}
		}
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	
	/** check if a "gap" exists if rendering from the stack
	 * 
	 * @param viewsToBeVisited list of views that belong to the current graph
	 * @return int position where the gap lies, otherwise -1
	 */
	private int checkIfGapAvailableWhenRenderingFromStack(ArrayList<Integer> viewsToBeVisited) {
		if (focusLevel.getElementByPositionIndex(0).getGLView() == null)
			return -1;
		if (viewsToBeVisited.size() != 4)
			return -1;
		for (Integer count : viewsToBeVisited) {
			if (viewsToBeVisited.get(count) == -1)
				return count;
		}
		return -1;
	}


	/** check if a "gap" exists if rendering from the focus
	 * 
	 * @param viewsToBeVisited list of views that belong to the current graph
	 * @return int position where the gap lies, otherwise -1
	 */
	private int checkIfGapAvailableWhenRenderingFromCenter(ArrayList<Integer> viewsToBeVisited) {
		if (viewsToBeVisited.get(2) == -1 && viewsToBeVisited.get(1) != -1 && viewsToBeVisited.get(3) != -1)
			return 2;
		else if (viewsToBeVisited.get(3) == -1 && viewsToBeVisited.get(2) != -1 && viewsToBeVisited.get(4) != -1)
			return 3;
		return -1;
	}

	
	/** getting the IDs of the views which are involved in the current path when rendering the path from the stack
	 * 
	 * @param hashViewToCenterPoint list of center points of the views
	 * @return list of IDs which belong to the current graph
	 */
	private ArrayList<Integer> getViewsOfCurrentPathStartingAtStack(HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<Integer> viewsOfCurrentPath = new ArrayList<Integer>();
		ArrayList<RemoteLevelElement> stackElements = stackLevel.getAllElements();
		int stackCount = 0;
		while (stackElements.get(stackCount).getGLView() != null && stackElements.get(stackCount).getGLView().getID() != activeViewID){
			RemoteLevelElement tempElement = stackElements.get(stackCount);
			stackElements.remove(stackCount);
			if (tempElement != null)
				stackElements.add(tempElement);
		}
		for (int count = 0; count < stackElements.size(); count++) {
			if (stackElements.get(count).getGLView() != null)
				viewsOfCurrentPath.add(stackElements.get(count).getGLView().getID());
		}
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null)
			viewsOfCurrentPath.add(1, focusLevel.getElementByPositionIndex(0).getGLView().getID());
		return viewsOfCurrentPath;
	}

	
	/** getting the IDs of the views which are involved in the current path when rendering the path from the focus
	 * 
	 * @param hashViewToCenterPoint list of center points of the views
	 * @return list of IDs which belong to the current graph
	 */
	private ArrayList<Integer> getViewsOfCurrentPathStartingAtFocus(HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<Integer> viewsOfCurrentPath = new ArrayList<Integer>();
		viewsOfCurrentPath.add(focusLevel.getElementByPositionIndex(0).getGLView().getID());
		
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++){
			if ((stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID())) || 
				(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && heatMapID == stackLevel.getElementByPositionIndex(stackCount).getGLView().getID()) ||
				(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && parCoordID == stackLevel.getElementByPositionIndex(stackCount).getGLView().getID())){
				viewsOfCurrentPath.add(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID());
			}
			else
				viewsOfCurrentPath.add(-1);
		}
		
		return 		viewsOfCurrentPath;
	}

	
	/** getting the optimal points of the heatmap/parcoords view
	 * 
	 * @param heatMapPredecessorID ID of the heatmap precedent view 
	 * @param heatMapSuccessorID  ID of the heatmap succeeding view
	 * @param heatMapPoints list of heatmap points
	 * @param parCoordsPredecessorID  of the parcoord precedent view
	 * @param parCoordsSuccessorID ID of the parcoord succeeding view
	 * @param parCoordsPoints list of parcoord points
	 * @param hashViewToCenterPoint center points of other views
	 */
	private void getPointsFromHeatMapAndParCoords(int heatMapPredecessorID, int heatMapSuccessorID,
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int parCoordsPredecessorID, int parCoordsSuccessorID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();

		if (heatMapPredecessorID != -1){
			if (heatMapPredecessorID == parCoordID)
				getPointsIfOtherViewIsDynamicView(parCoordsPoints, heatMapPoints, HEATMAP, PREDECESSOR);
			else
				getPointsIfNotOtherViewIsDynamicView(heatMapPoints, hashViewToCenterPoint.get(heatMapPredecessorID), HEATMAP, PREDECESSOR);
		}
		if (heatMapSuccessorID != -1){
			if (heatMapSuccessorID == parCoordID)
				getPointsIfOtherViewIsDynamicView(heatMapPoints, parCoordsPoints, HEATMAP, SUCCESSOR);				
			else
				getPointsIfNotOtherViewIsDynamicView(heatMapPoints, hashViewToCenterPoint.get(heatMapSuccessorID), HEATMAP, SUCCESSOR);		
		}
		if (parCoordsPredecessorID != -1){
			if (parCoordsPredecessorID == heatMapID)
				getPointsIfOtherViewIsDynamicView(heatMapPoints, parCoordsPoints, PARCOORDS, PREDECESSOR);
			else
				getPointsIfNotOtherViewIsDynamicView(parCoordsPoints, hashViewToCenterPoint.get(parCoordsPredecessorID), PARCOORDS, PREDECESSOR);
		}
		if (parCoordsSuccessorID != -1){
			if (parCoordsSuccessorID == heatMapID)
				getPointsIfOtherViewIsDynamicView(parCoordsPoints, heatMapPoints, PARCOORDS, SUCCESSOR);		
			else
				getPointsIfNotOtherViewIsDynamicView(parCoordsPoints, hashViewToCenterPoint.get(parCoordsSuccessorID), PARCOORDS, SUCCESSOR);	
		}
	}
	
	
	/** calculates shortest path between heatmap and parcoords view
	 * 
	 * @param predecessorPointsList points list to choose the best point from
	 * @param successorPointsList points list to choose the best point from
	 * @param type type of dynamic view (either HEATMAP or PARCOORD)
	 * @param nextOrPrevious indicates if the other involved view is the predecessor or successor of the heatmap/parcoords view
	 */
	private void getPointsIfOtherViewIsDynamicView(ArrayList<ArrayList<Vec3f>> predecessorPointsList, ArrayList<ArrayList<Vec3f>> successorPointsList, char type, int nextOrPrevious) {
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
				if (currentPath < minPath){
					minPath = currentPath;
					optimalPredecessorPoints = predecessorPoints;
					optimalSuccessorPoints = successorPoints;
				}
			}
		}
		if (type == HEATMAP && nextOrPrevious == PREDECESSOR){
			if (!parCoordsSuccessor.contains(optimalSuccessorPoints))
				parCoordsSuccessor.add(optimalSuccessorPoints);
			if (!heatmapPredecessor.contains(optimalPredecessorPoints))
				heatmapPredecessor.add(optimalPredecessorPoints);
		}
		else if (type == HEATMAP && nextOrPrevious == SUCCESSOR){
			if(!parCoordsPredecessor.contains(optimalPredecessorPoints))
				parCoordsPredecessor.add(optimalPredecessorPoints);
			if (!heatmapSuccessor.contains(optimalSuccessorPoints))
				heatmapSuccessor.add(optimalSuccessorPoints);
		}
		else if (type == PARCOORDS && nextOrPrevious == PREDECESSOR){
			if(!heatmapSuccessor.contains(optimalSuccessorPoints))
				heatmapSuccessor.add(optimalSuccessorPoints);
			if(!parCoordsPredecessor.contains(optimalPredecessorPoints))
				parCoordsPredecessor.add(optimalPredecessorPoints);
		}
		else if (type == PARCOORDS && nextOrPrevious == SUCCESSOR){
			if (!heatmapPredecessor.contains(optimalPredecessorPoints))
				heatmapPredecessor.add(optimalPredecessorPoints);
			if (!parCoordsSuccessor.contains(optimalSuccessorPoints))
				parCoordsSuccessor.add(optimalSuccessorPoints);
		}
	}
	

	/** Calculates the shortest path between two views if one of them is either the heatmap view or the parcoords view 
	 * 
	 * @param dynamicPointsList set of points to choose the best from
	 * @param connectionPoint connection point to which the parcoord/heatmap view should be connected to  
	 * @param type type of dynamic view (either HEATMAP or PARCOORDS)
	 * @param nextOrPrevious indicates if the other involved view is the predecessor or successor of the heatmap/parcoords view
	 */
	private void getPointsIfNotOtherViewIsDynamicView(ArrayList<ArrayList<Vec3f>> dynamicPointsList, Vec3f connectionPoint, char type, int nextOrPrevious){
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;
		
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

	
	/** Calculates the optimal control point for the curve between two given points
	 * 
	 * @param src first point
	 * @param dst second point
	 * @return control point for this connection line
	 */
	private Vec3f calculateControlPoint(Vec3f src, Vec3f dst) {
		Vec3f controlPoint = new Vec3f();
		if (src.x() < 0 && src.y() < 0 && dst.x() < 0 && dst.y() < 0)
			controlPoint = new Vec3f(-2, -2, 0);
		else if (src.x() < 0 && src.y() > 0 && dst.x() < 0 && dst.y() > 0)
			controlPoint = new Vec3f(-2, 2, 0);
		else if (src.x() > 0 && src.y() > 0 && dst.x() > 0 && dst.y() > 0)
			controlPoint = new Vec3f(2, 2, 0);
		else if (src.x() > 0 && src.y() < 0 && dst.x() > 0 && dst.y() < 0)
			controlPoint = new Vec3f(2, -2, 0);

		else if ((src.x() < 0 && src.y() > 0 && dst.x() < 0 && dst.y() < 0)
			|| (src.x() < 0 && src.y() < 0 && dst.x() < 0 && dst.y() > 0))
			controlPoint = new Vec3f(-2, 0, 0);
		else if ((src.x() < 0 && src.y() > 0 && dst.x() > 0 && dst.y() > 0)
			|| (src.x() > 0 && src.y() > 0 && dst.x() < 0 && dst.y() > 0))
			controlPoint = new Vec3f(0, 2, 0);
		else if ((src.x() > 0 && src.y() < 0 && dst.x() > 0 && dst.y() > 0)
			|| (src.x() > 0 && src.y() > 0 && dst.x() > 0 && dst.y() < 0))
			controlPoint = new Vec3f(2, 0, 0);
		else if ((src.x() > 0 && src.y() < 0 && dst.x() < 0 && dst.y() < 0)
			|| (src.x() < 0 && src.y() < 0 && dst.x() > 0 && dst.y() < 0))
			controlPoint = new Vec3f(0, -2, 0);

		else if ((src.x() < 0 && src.y() > 0 && dst.x() > 0 && dst.y() < 0)
			|| (src.x() > 0 && src.y() < 0 && dst.x() < 0 && dst.y() > 0))
			controlPoint = new Vec3f(0, 0, 0);
		else if ((src.x() < 0 && src.y() < 0 && dst.x() > 0 && dst.y() > 0)
			|| (src.x() > 0 && src.y() > 0 && dst.x() < 0 && dst.y() < 0))
			controlPoint = new Vec3f(0, 0, 0);

		return controlPoint;
	}
	
	
	/** This method gets the successor of a given view
	 * 
	 * @param list list containing all views
	 * @param ID id of the view to which the successor should be found
	 * @return ID of the succeeding view
	 */
	private int getNext(ArrayList<Integer> list, int iD){
		int position = list.indexOf(iD);

		if (position == list.size()-1)
			return -1;
		else if (position == -1)
			return -1;
		
		while (position < list.size()-1){
			if (list.get(position+1) != -1)
				return list.get(position+1);
			position++;
		}
		return -1;
	}

	
	/** This method gets the predecessor of a given view
	 * 
	 * @param list list containing all views
	 * @param ID id of the view to which the predecessor should be found
	 * @return ID of the precedent view
	 */
	private Integer getPreviousView(ArrayList<Integer> list, int iD){
		int position = list.indexOf(iD);
		if (position == 0)
			return -1;
		else if (position == -1)
			return -1;
		
		while(position >0){
			if (list.get(position-1) != -1)
				return list.get(position-1);
			position--;
		}
		return -1;

	}

	
	// this method is not needed for this kind of graph type
	@Override
	protected ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		return null;
	}

	
	// this method is not needed for this kind of graph type
	@Override
	protected ArrayList<ArrayList<Vec3f>> calculateOptimalSinglePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		return null;
	}
}