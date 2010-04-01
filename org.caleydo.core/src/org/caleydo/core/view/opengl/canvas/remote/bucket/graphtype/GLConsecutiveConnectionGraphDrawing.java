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
		setControlPoints();
	}

	private void setControlPoints() {
		controlPoints.add(new Vec3f(-2, -2, 0));
		controlPoints.add(new Vec3f(-2, 2, 0));
		controlPoints.add(new Vec3f(2, 2, 0));
		controlPoints.add(new Vec3f(2, -2, 0));
		controlPoints.add(new Vec3f(-2, 0, 0));
		controlPoints.add(new Vec3f(0, 2, 0));
		controlPoints.add(new Vec3f(2, 0, 0));
		controlPoints.add(new Vec3f(0, -2, 0));
		controlPoints.add(new Vec3f(0, 0, 0));
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
				hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}

		//getting list of views that belong to the current graph, sorted by sequence of visiting
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null && focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID){
			viewsToBeVisited = getViewsOfCurrentPathStartingAtFocus(hashViewToCenterPoint);
			renderFromCenter(gl, idType,  hashViewToCenterPoint, viewsToBeVisited, heatMapPoints, parCoordsPoints);
		}
		else{
			viewsToBeVisited = getViewsOfCurrentPathStartingAtStack(hashViewToCenterPoint);
			renderFromStack(gl, idType, hashViewToCenterPoint, viewsToBeVisited, heatMapPoints, parCoordsPoints);
		}	
	}

	
	private void renderFromStack(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<Integer> viewsToBeVisited, ArrayList<ArrayList<Vec3f>> heatMapPoints, ArrayList<ArrayList<Vec3f>> parCoordsPoints) {
		
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		int positionOfGap = checkIfGapAvailableWhenRenderingFromStack(viewsToBeVisited);
		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;
		//TODO: render from stack
	}

	private void renderFromCenter(GL gl, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<Integer> viewsToBeVisited, ArrayList<ArrayList<Vec3f>> heatMapPoints, ArrayList<ArrayList<Vec3f>> parCoordsPoints) {
		
		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;

		if (gapSuccessorID == -1){
			heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
			heatMapSuccessorID = getNext(viewsToBeVisited, heatMapID);
			parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
			parCoordsSuccessorID = getNext(viewsToBeVisited, parCoordID);
		}
		else{
			if (heatMapID == activeViewID){
				heatMapPredecessorID = getNext(viewsToBeVisited, heatMapID);
				heatMapSuccessorID = gapSuccessorID;
				parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
				if(beforeGap(viewsToBeVisited, parCoordID))
					parCoordsSuccessorID = -1;
				else
					parCoordsSuccessorID = getNext(viewsToBeVisited, parCoordID);
			}
			else if (parCoordID == activeViewID){
				heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
				if (beforeGap(viewsToBeVisited, heatMapID))
					heatMapSuccessorID = -1;
				else
					heatMapSuccessorID = getNext(viewsToBeVisited, heatMapID);
				parCoordsPredecessorID = getNext(viewsToBeVisited, parCoordID);
				parCoordsSuccessorID = gapSuccessorID;
			}
			else{
				//TODO what if Pathway in focus
			}
		}
		
		//calculating optimal points for heatmap and/or parCoords
		getPointsFromHeatMapAndParCoords(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints, parCoordsPredecessorID, parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint);

		renderLinesFromCenter(gl, idType, heatMapPredecessorID, heatMapSuccessorID, parCoordsPredecessorID, parCoordsSuccessorID, viewsToBeVisited, hashViewToCenterPoint);
		
	}

	private boolean beforeGap(ArrayList<Integer> viewsToBeVisited, int iD) {
		int count = 0;
		while (count < gapPosition){
			if (allviews.get(count) == iD)
				return true;
			count++;
		}	
		return false;
	}

	/** rendering lines when mouse is at focus level
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
	private void renderLinesFromCenter(GL gl, EIDType idType, int heatMapPredecessorID, int heatMapSuccessorID, int parCoordsPredecessorID, int parCoordsSuccessorID, ArrayList<Integer> viewsToBeVisited, HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>();
		
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
						VisLinkAnimationStage bundling = null;
						if (heatMapPredecessorID == activeViewID && heatMapPredecessorID != parCoordID)
							bundling = new VisLinkAnimationStage(true);
						else
							bundling = new VisLinkAnimationStage();
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
						VisLinkAnimationStage bundling = null;
						if (parCoordsPredecessorID == activeViewID && parCoordsPredecessorID != heatMapID)
							bundling = new VisLinkAnimationStage(true);
						else
							bundling = new VisLinkAnimationStage();
						if (parCoordsPredecessorID == heatMapID)
							bundling.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), parCoordsPredecessor.get(0).get(0), controlPoint));
						else
							bundling.addLine(createControlPoints(hashViewToCenterPoint.get(parCoordsPredecessorID), parCoordsPredecessor.get(0).get(0), controlPoint));
						connectionLinesAllViews.add(bundling);
					}
					if (parCoordsSuccessor.size() > 0){
						VisLinkAnimationStage currentStage = new VisLinkAnimationStage();
						
						if (parCoordsSuccessorID != heatMapID && parCoordsSuccessorID != -1)
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
					VisLinkAnimationStage currentStage = null;
					if (key == activeViewID)
						currentStage = new VisLinkAnimationStage();
					else
						currentStage = new VisLinkAnimationStage(true);
					if (key == parCoordsSuccessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), parCoordsSuccessor.get(0).get(0));
					else if (key == heatMapSuccessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), heatmapSuccessor.get(0).get(0));
					else if (key == parCoordsPredecessorID)
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), parCoordsPredecessor.get(0).get(0));
					else if (key == heatMapPredecessorID){
						if (heatmapPredecessor.get(0) == null)
							return;
						controlPoint = calculateControlPoint(hashViewToCenterPoint.get(key), heatmapPredecessor.get(0).get(0));
					}
					
					vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(key), controlPoint);
					for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(key)) {
						if (alCurrentPoints.size() > 1)
							renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
						else
							pointsToDepthSort.add(alCurrentPoints.get(0));
					}
					for (Vec3f currentPoint : depthSort(pointsToDepthSort))
						currentStage.addLine(createControlPoints(vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(key)));

					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1 && key == activeViewID)
						connectionLinesAllViews.add(currentStage);
					VisLinkAnimationStage bundling = new VisLinkAnimationStage();
					if (key == heatMapSuccessorID){
						if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
							bundling.addLine(createControlPoints(vecViewBundlingPoint, heatmapSuccessor.get(0).get(0), controlPoint));
						else
							bundling.addLine(createControlPoints(hashViewToCenterPoint.get(key), heatmapSuccessor.get(0).get(0), controlPoint));
					}
					else if (key == parCoordsSuccessorID)
						if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
							bundling.addLine(createControlPoints(vecViewBundlingPoint, parCoordsSuccessor.get(0).get(0), controlPoint));
						else
							bundling.addLine(createControlPoints(hashViewToCenterPoint.get(key), parCoordsSuccessor.get(0).get(0), controlPoint));
					connectionLinesAllViews.add(bundling);
					if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
						connectionLinesAllViews.add(currentStage);
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
		allviews.clear();
		ArrayList<Integer> viewsOfCurrentPath = new ArrayList<Integer>();
		ArrayList<Integer> notLoadedPosition = new ArrayList<Integer>();
		
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++){
			if ((stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID())) || 
				(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && heatMapID == stackLevel.getElementByPositionIndex(stackCount).getGLView().getID()) ||
				(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && parCoordID == stackLevel.getElementByPositionIndex(stackCount).getGLView().getID())){
				viewsOfCurrentPath.add(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID());
				allviews.add(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID());
			}
			else{
				notLoadedPosition.add(stackCount);
				allviews.add(-1);
			}
		}
		
		if (viewsOfCurrentPath.size() == 2)
			checkIfGapPresent(allviews, notLoadedPosition);
		else if (viewsOfCurrentPath.size() == 3)
			viewsOfCurrentPath = revertElements(viewsOfCurrentPath, notLoadedPosition.get(0));
		
		viewsOfCurrentPath.add(0, focusLevel.getElementByPositionIndex(0).getGLView().getID());
		
		return viewsOfCurrentPath;
	}

	private void checkIfGapPresent(ArrayList<Integer> allviews, ArrayList<Integer> notLoadedPosition) {
		if (notLoadedPosition.get(0) != (notLoadedPosition.get(1)-1))
			if (notLoadedPosition.get(0) == 1){
				gapSuccessorID = allviews.get(notLoadedPosition.get(0)+1);
				gapPosition = 1;
			}
			else{
				gapSuccessorID = allviews.get(notLoadedPosition.get(1)+1);
				gapPosition = 2;
			}
	}
	

	/**
	 *  if three elements are loaded at stack level, we revert the direction of the graph to avoid complex gap calculation
	 * @param viewsOfCurrentPath views that belong to the current path
	 * @param nullElement position of the not loaded element 
	 */
	private ArrayList<Integer> revertElements(ArrayList<Integer> viewsOfCurrentPath, Integer nullElement) {
		ArrayList<Integer> views = new ArrayList<Integer>();
		for (int count = nullElement-1; count >= 0; count--) {
			views.add(viewsOfCurrentPath.get(count));
		}
		for (int count = 3; count > nullElement; count--) {
			views.add(viewsOfCurrentPath.get(count));
		}
		return views;
		
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
	 * Algorithm: Calculate vector between src and dst, getting center point of this vector, add src vector and check which control point is closest to this point
	 * 
	 * @param src first point
	 * @param dst second point
	 * @return control point for this connection line
	 */
	private Vec3f calculateControlPoint(Vec3f src, Vec3f dst) {
		Vec3f controlPoint = null;
		Vec3f connectionVec = src.minus(dst);
		connectionVec.scale(0.5f);
		connectionVec = connectionVec.plus(src);
		float minDistance = Float.MAX_VALUE;
		float currentDistance = -1;
		for (Vec3f element : controlPoints) {
			Vec3f distance = connectionVec.minus(element);
			currentDistance = distance.length();
			if (currentDistance < minDistance){
				minDistance = currentDistance;
				controlPoint = element;
			}
		}
		return controlPoint;
	}
	
	
	/** This method gets the successor of a given view
	 * 
	 * @param list list containing all views
	 * @param iD id of the view to which the successor should be found
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
	 * @param iD id of the view to which the predecessor should be found
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