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
	private boolean heatMapOnStackAndHasPredecessor = false;
	private boolean heatMapOnStackAndHasSucessor = false;
	private boolean parCoordsOnStackAndHavePredecessor = false;
	private boolean parCoordsOnStackAndHaveSucessor = false;
	private boolean isGapParCoordsOnStackAndHeatMapOnStack = false;
	private boolean isGapParCoordsCenteredAndHeatMapOnStack = false;
	private boolean isGapParCoordsOnStackAndHeatMapCentered = false;
	private boolean multiplePoints = false;

	private ArrayList<ArrayList<Vec3f>> heatmapPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> heatmapSuccessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsSuccessor = new ArrayList<ArrayList<Vec3f>>();
	private HashMap<Integer, VisLinkAnimationStage> heatMapStages =	new HashMap<Integer, VisLinkAnimationStage>();
	private HashMap<Integer, VisLinkAnimationStage> parCoordStages = new HashMap<Integer, VisLinkAnimationStage>();

	boolean gap = false;
	private int heatMapID = getSpecialViewID(HEATMAP);
	private int parCoordID = getSpecialViewID(PARCOORDS);
	private Vec3f vecCenter = new Vec3f();

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

		heatMapOnStackAndHasPredecessor = false;
		heatMapOnStackAndHasSucessor = false;
		parCoordsOnStackAndHavePredecessor = false;
		parCoordsOnStackAndHaveSucessor = false;
		multiplePoints = false;
/*		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		HashMap<Integer, VisLinkAnimationStage> connections = new HashMap<Integer, VisLinkAnimationStage>();
		HashMap<Integer, Vec3f> bundlingPoints = new HashMap<Integer, Vec3f>();*/
		renderFromCenter(gl, idType);
		return;
	}

	/*
	 * hashViewToCenterPoint = getOptimalDynamicPoints(idType); if (hashViewToCenterPoint == null) return;
	 * vecCenter = calculateCenter(hashViewToCenterPoint.values()); for (Integer iKey : keySet) {
	 * VisLinkAnimationStage connectionLinesCurrentView = new VisLinkAnimationStage(); Vec3f
	 * vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
	 * bundlingPoints.put(iKey, vecViewBundlingPoint); ArrayList<Vec3f> pointsToDepthSort = new
	 * ArrayList<Vec3f>(); if (iKey == heatMapID && multiplePoints){ if (heatmapPredecessor.size() > 0){ for
	 * (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor){ if (alCurrentPoints.size() > 1)
	 * renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints); else
	 * pointsToDepthSort.add(alCurrentPoints.get(0)); } for(Vec3f currentPoint :
	 * depthSort(pointsToDepthSort)){ if (iKey == activeViewID){ connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter),
	 * currentPoint, calculateCenter(heatmapPredecessor) ) ); } else{
	 * connectionLinesCurrentView.setReverseLineDrawingDirection(true); connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter),
	 * currentPoint, calculateCenter(heatmapPredecessor) ) ); } } heatMapStages.put(predecessor,
	 * connectionLinesCurrentView); pointsToDepthSort.clear(); } if (heatmapSuccessor.size() > 0){
	 * connectionLinesCurrentView = new VisLinkAnimationStage(); for (ArrayList<Vec3f> alCurrentPoints :
	 * heatmapSuccessor){ if (alCurrentPoints.size() > 1) renderPlanes(gl, vecViewBundlingPoint,
	 * alCurrentPoints); else pointsToDepthSort.add(alCurrentPoints.get(0)); } for(Vec3f currentPoint :
	 * depthSort(pointsToDepthSort)){ if (iKey == activeViewID){ connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter),
	 * currentPoint, calculateCenter(heatmapSuccessor) ) ); } else{ connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter),
	 * currentPoint, calculateCenter(heatmapSuccessor) ) ); } } heatMapStages.put(successor,
	 * connectionLinesCurrentView); pointsToDepthSort.clear(); } } else if (iKey == parCoordID &&
	 * multiplePoints){ if (parCoordsPredecessor.size() > 0){ connectionLinesCurrentView = new
	 * VisLinkAnimationStage(); for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor){ if
	 * (alCurrentPoints.size() > 1) renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints); else
	 * pointsToDepthSort.add(alCurrentPoints.get(0)); } for(Vec3f currentPoint :
	 * depthSort(pointsToDepthSort)){ if (iKey == activeViewID){ connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter),
	 * currentPoint, calculateCenter(parCoordsPredecessor) ) ); } else{
	 * connectionLinesCurrentView.setReverseLineDrawingDirection(true); connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter),
	 * currentPoint, calculateCenter(parCoordsPredecessor) ) ); } } parCoordStages.put(predecessor,
	 * connectionLinesCurrentView); pointsToDepthSort.clear(); } if (parCoordsSuccessor.size() > 0){
	 * connectionLinesCurrentView = new VisLinkAnimationStage(); for (ArrayList<Vec3f> alCurrentPoints :
	 * parCoordsSuccessor){ if (alCurrentPoints.size() > 1) renderPlanes(gl, vecViewBundlingPoint,
	 * alCurrentPoints); else pointsToDepthSort.add(alCurrentPoints.get(0)); } for(Vec3f currentPoint :
	 * depthSort(pointsToDepthSort)){ if (iKey == activeViewID){ connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter),
	 * currentPoint, calculateCenter(parCoordsSuccessor) ) ); } else{ connectionLinesCurrentView.addLine(
	 * createControlPoints( calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter),
	 * currentPoint, calculateCenter(parCoordsSuccessor) ) ); } } parCoordStages.put(successor,
	 * connectionLinesCurrentView); pointsToDepthSort.clear(); } } else{ for (ArrayList<Vec3f> alCurrentPoints
	 * : hashIDTypeToViewToPointLists.get(idType).get(iKey)) { if (alCurrentPoints.size() > 1) {
	 * renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints); } else
	 * pointsToDepthSort.add(alCurrentPoints.get(0)); } for(Vec3f currentPoint :
	 * depthSort(pointsToDepthSort)){ if (iKey == activeViewID){ connectionLinesCurrentView.addLine(
	 * createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) ); } else{
	 * connectionLinesCurrentView.setReverseLineDrawingDirection(true); connectionLinesCurrentView.addLine(
	 * createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) ); } }
	 * connections.put(iKey, connectionLinesCurrentView); pointsToDepthSort.clear(); } } if
	 * (focusLevel.getElementByPositionIndex(0).getGLView() != null){ if
	 * (focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID){ renderFromCenter(gl,
	 * connections, bundlingPoints, idType, hashViewToCenterPoint); return; } } for (int stackElement = 0;
	 * stackElement < stackLevel.getCapacity(); stackElement++) { if
	 * (stackLevel.getElementByPositionIndex(stackElement).getGLView() != null){
	 * if(stackLevel.getElementByPositionIndex(stackElement).getGLView().getID() == activeViewID){
	 * renderFromStackLevel(gl, connections, bundlingPoints, idType, hashViewToCenterPoint); break; } } } }
	 */

	private void renderFromCenter(GL gl, EIDType idType) {
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
		int parCoordsSucessorID = -1;

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
		viewsToBeVisited = getViewsOfCurrentPathStartingAtFocus(hashViewToCenterPoint);
		heatMapPredecessorID = getPreviousView(viewsToBeVisited, heatMapID);
		heatMapSuccessorID = getNext(viewsToBeVisited, heatMapID);
		parCoordsPredecessorID = getPreviousView(viewsToBeVisited, parCoordID);
		parCoordsSucessorID = getNext(viewsToBeVisited, parCoordID);

		//calculating optimal points for heatmap and/or parCoords
		getPointsFromHeatMapAndParCoordsWhenRenderingFromCenter(heatMapPredecessorID, heatMapSuccessorID, heatMapPoints, parCoordsPredecessorID, parCoordsSucessorID, parCoordsPoints, hashViewToCenterPoint);
		
		int positionOfGap = checkIfGapAvailableWhenRenderingFromCenter(viewsToBeVisited);
		
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
						
						if (parCoordsSucessorID != heatMapID)
							controlPoint = calculateControlPoint(parCoordsSuccessor.get(0).get(0), hashViewToCenterPoint.get(parCoordsSucessorID));
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
					if (key == parCoordsSucessorID)
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
					else if (key == parCoordsSucessorID)
						if (hashIDTypeToViewToPointLists.get(idType).get(key).size() > 1)
							bundling.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), vecViewBundlingPoint, controlPoint));
						else
							bundling.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), hashViewToCenterPoint.get(key), controlPoint));
					connectionLinesAllViews.add(bundling);
					pointsToDepthSort.clear();

				}
			}
		}
		
		
		
		/*
		
		
		vecCenter = calculateCenter(hashViewToCenterPoint.values());
		VisLinkAnimationStage temp = new VisLinkAnimationStage();
		for (Integer iKey : keySet) {
			VisLinkAnimationStage connectionLinesCurrentView = new VisLinkAnimationStage();
			Vec3f vecViewBundlingPoint = null;
			if (iKey != heatMapID && iKey != parCoordID)
				vecViewBundlingPoint = hashViewToCenterPoint.get(iKey);
			else if (iKey == heatMapID)
				vecViewBundlingPoint = heatmapSuccessor.get(0).get(0);
			else if (iKey == parCoordID)
				vecViewBundlingPoint = parCoordsPredecessor.get(0).get(0);
			ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();

			
			if (heatmapPredecessor.size() > 0) {
				for (ArrayList<Vec3f> alCurrentPoints : heatmapPredecessor) {
					if (alCurrentPoints.size() > 1)
						renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
					else
						pointsToDepthSort.add(alCurrentPoints.get(0));
				}
				for (Vec3f currentPoint : depthSort(pointsToDepthSort)) {
					if (iKey == activeViewID) {
						connectionLinesCurrentView.addLine(createControlPoints(calculateBundlingPoint(
							calculateCenter(heatmapPredecessor), vecCenter), currentPoint,
							calculateCenter(heatmapPredecessor)));
					}
					else {
						connectionLinesCurrentView.setReverseLineDrawingDirection(true);
						connectionLinesCurrentView.addLine(createControlPoints(calculateBundlingPoint(
							calculateCenter(heatmapPredecessor), vecCenter), currentPoint,
							calculateCenter(heatmapPredecessor)));
					}
				}
				heatMapStages.put(PREDECESSOR, connectionLinesCurrentView);
				pointsToDepthSort.clear();
			}
			if (heatmapSuccessor.size() > 0) {
				connectionLinesCurrentView = new VisLinkAnimationStage();
				for (ArrayList<Vec3f> alCurrentPoints : heatmapSuccessor) {
					if (alCurrentPoints.size() > 1)
						renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
					else
						pointsToDepthSort.add(alCurrentPoints.get(0));
				}
				for (Vec3f currentPoint : depthSort(pointsToDepthSort)) {
					if (heatMapSuccessorID == parCoordID)
						vecCenter =
							calculateControlPoint(heatmapSuccessor.get(0).get(0), parCoordsPredecessor.get(0)
								.get(0));
					else
						vecCenter =
							calculateControlPoint(heatmapSuccessor.get(0).get(0), hashViewToCenterPoint
								.get(heatMapSuccessorID));
					connectionLinesCurrentView.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(heatmapSuccessor), vecCenter), currentPoint,
						calculateCenter(heatmapSuccessor)));

				}
				heatMapStages.put(SUCCESSOR, connectionLinesCurrentView);
				pointsToDepthSort.clear();
			}

			if (parCoordsPredecessor.size() > 0) {
				connectionLinesCurrentView = new VisLinkAnimationStage();
				for (ArrayList<Vec3f> alCurrentPoints : parCoordsPredecessor) {
					if (alCurrentPoints.size() > 1)
						renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
					else
						pointsToDepthSort.add(alCurrentPoints.get(0));
				}
				for (Vec3f currentPoint : depthSort(pointsToDepthSort)) {
					if (iKey == activeViewID) {
						connectionLinesCurrentView.addLine(createControlPoints(calculateBundlingPoint(
							calculateCenter(parCoordsPredecessor), vecCenter), currentPoint,
							calculateCenter(parCoordsPredecessor)));
					}
					else {
						connectionLinesCurrentView.setReverseLineDrawingDirection(true);
						connectionLinesCurrentView.addLine(createControlPoints(calculateBundlingPoint(
							calculateCenter(parCoordsPredecessor), vecCenter), currentPoint,
							calculateCenter(parCoordsPredecessor)));
					}
				}
				parCoordStages.put(PREDECESSOR, connectionLinesCurrentView);
				pointsToDepthSort.clear();
			}
			if (parCoordsSuccessor.size() > 0) {
				connectionLinesCurrentView = new VisLinkAnimationStage();
				for (ArrayList<Vec3f> alCurrentPoints : parCoordsSuccessor) {
					if (alCurrentPoints.size() > 1)
						renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
					else
						pointsToDepthSort.add(alCurrentPoints.get(0));
				}
				for (Vec3f currentPoint : depthSort(pointsToDepthSort)) {
					connectionLinesCurrentView.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(parCoordsSuccessor), vecCenter), currentPoint,
						calculateCenter(parCoordsSuccessor)));
				}
				parCoordStages.put(SUCCESSOR, connectionLinesCurrentView);
				pointsToDepthSort.clear();
			}

			if (iKey != heatMapID && iKey != parCoordID) {
				for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
					if (alCurrentPoints.size() > 1) {
						renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
					}
					else
						pointsToDepthSort.add(alCurrentPoints.get(0));
				}
				for (Vec3f currentPoint : depthSort(pointsToDepthSort)) {
					if (iKey == activeViewID) {
						connectionLinesCurrentView.addLine(createControlPoints(vecViewBundlingPoint,
							currentPoint, hashViewToCenterPoint.get(iKey)));
					}
					else {
						temp.setReverseLineDrawingDirection(true);
						temp.addLine(createControlPoints(vecViewBundlingPoint, currentPoint,
							hashViewToCenterPoint.get(iKey)));
					}
				}
				pointsToDepthSort.clear();
			}
		}
		VisLinkAnimationStage anim = new VisLinkAnimationStage();
		if (heatMapSuccessorID == parCoordID)
			anim
				.addLine(createControlPoints(parCoordsPredecessor.get(0).get(0), heatmapSuccessor.get(0).get(
					0), calculateControlPoint(heatmapSuccessor.get(0).get(0), parCoordsPredecessor.get(0)
					.get(0))));
		else
			anim.addLine(createControlPoints(hashViewToCenterPoint.get(heatMapSuccessorID), heatmapSuccessor
				.get(0).get(0), new Vec3f(2, 2, 0)));
		connectionLinesAllViews.add(anim);
		anim = new VisLinkAnimationStage();
		if (parCoordsSucessorID != -1) {
			anim.addLine(createControlPoints(hashViewToCenterPoint.get(parCoordsSucessorID),
				parCoordsSuccessor.get(0).get(0), calculateControlPoint(parCoordsSuccessor.get(0).get(0),
					hashViewToCenterPoint.get(parCoordsSucessorID))));
			connectionLinesAllViews.add(anim);
			if (hashIDTypeToViewToPointLists.get(idType).get(parCoordsSucessorID).size() > 1)
				connectionLinesAllViews.add(temp);
		}
		else
			anim.addLine(createControlPoints(parCoordsPredecessor.get(0).get(0), hashViewToCenterPoint
				.get(parCoordsPredecessorID), new Vec3f(-2, 2, 0)));

		/*
		 * ArrayList<Integer> ids = new ArrayList<Integer>(); for (int stackCount = 0; stackCount <
		 * stackLevel.getCapacity(); stackCount++) {
		 * if(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null){ int id =
		 * stackLevel.getElementByPositionIndex(stackCount).getGLView().getID(); if
		 * (bundlingPoints.containsKey(id)) ids.add(id); } } if (multiplePoints){ if(ids.size()>1){ if
		 * ((activeViewID != parCoordID) && (activeViewID != heatMapID)){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1)
		 * connectionLinesAllViews.add(connections.get(activeViewID)); } Vec3f src = new Vec3f(); if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size()>1) src =
		 * bundlingPoints.get(activeViewID); else src = hashViewToCenterPoint.get(activeViewID); if
		 * ((activeViewID == parCoordID) || (activeViewID == heatMapID)) src =
		 * hashViewToCenterPoint.get(activeViewID); for (Integer currentID : ids) { if (currentID ==
		 * heatMapID){ if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor &&
		 * isGapParCoordsOnStackAndHeatMapOnStack){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(bundlingPoints.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
		 * connectionLinesAllViews.add(heatMapStages.get(predecessor));
		 * connectionLinesAllViews.add(heatMapStages.get(successor)); } else{ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
		 * connectionLinesAllViews.add(heatMapStages.get(predecessor));
		 * connectionLinesAllViews.add(heatMapStages.get(successor)); } } else if
		 * (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(src,
		 * calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
		 * connectionLinesAllViews.add(heatMapStages.get(predecessor));
		 * connectionLinesAllViews.add(heatMapStages.get(successor)); } else if
		 * (heatMapOnStackAndHasPredecessor && (isGapParCoordsCenteredAndHeatMapOnStack == true)){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(src,
		 * calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(heatMapStages.get(predecessor)); } else if
		 * (heatMapOnStackAndHasPredecessor && (isGapParCoordsOnStackAndHeatMapOnStack == true)){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(heatMapStages.get(predecessor)); } else if
		 * (heatMapOnStackAndHasPredecessor){ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true); bundlingLine.addLine(createControlPoints(src,
		 * calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(heatMapStages.get(predecessor)); } } else if (currentID == parCoordID){
		 * if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor &&
		 * isGapParCoordsOnStackAndHeatMapOnStack){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() >1){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(bundlingPoints.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor));
		 * connectionLinesAllViews.add(parCoordStages.get(successor)); } else{ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor));
		 * connectionLinesAllViews.add(parCoordStages.get(successor)); } } else if
		 * (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(src,
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor));
		 * connectionLinesAllViews.add(parCoordStages.get(successor)); } else if
		 * (parCoordsOnStackAndHavePredecessor && isGapParCoordsOnStackAndHeatMapCentered){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints
		 * (calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter),
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor)); } else if
		 * (parCoordsOnStackAndHavePredecessor && (isGapParCoordsOnStackAndHeatMapOnStack == true)){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1){ VisLinkAnimationStage
		 * bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor)); } else { VisLinkAnimationStage
		 * bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor)); } } else if
		 * (parCoordsOnStackAndHavePredecessor){ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true); bundlingLine.addLine(createControlPoints(src,
		 * calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine);
		 * connectionLinesAllViews.add(parCoordStages.get(predecessor)); } } else{ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && (currentID != parCoordID)){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); connectionLinesAllViews.add(connections.get(currentID));
		 * src = bundlingPoints.get(currentID); } else if
		 * (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && (currentID != heatMapID)){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); connectionLinesAllViews.add(connections.get(currentID));
		 * src = bundlingPoints.get(currentID); } else{ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true); if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size() >
		 * 1){ bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); src = bundlingPoints.get(currentID); } else
		 * bundlingLine.addLine(createControlPoints(src, hashViewToCenterPoint.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); src = hashViewToCenterPoint.get(currentID); } } } } else
		 * if (ids.size() == 1){ int remoteId = ids.get(0);
		 * renderLineOnlyOneOtherViewOnStackAvailable(remoteId, idType, hashViewToCenterPoint, bundlingPoints,
		 * connections, connectionLinesAllViews); } } else{ if(ids.size()>1){ if ((activeViewID != parCoordID)
		 * && (activeViewID != heatMapID)){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1)
		 * connectionLinesAllViews.add(connections.get(activeViewID)); } Vec3f src = new Vec3f(); if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size()>1) src =
		 * bundlingPoints.get(activeViewID); else src = hashViewToCenterPoint.get(activeViewID); if
		 * ((activeViewID == parCoordID) || (activeViewID == heatMapID)) src =
		 * hashViewToCenterPoint.get(activeViewID); for (Integer currentID : ids) { if (currentID ==
		 * heatMapID){ if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor &&
		 * isGapParCoordsOnStackAndHeatMapOnStack){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(bundlingPoints.get(activeViewID),
		 * heatmapPredecessor.get(0).get(0), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src = heatmapSuccessor.get(0).get(0); }
		 * else{ VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * heatmapPredecessor.get(0).get(0), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src = heatmapSuccessor.get(0).get(0); } }
		 * else if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(src, heatmapPredecessor.get(0).get(0),
		 * vecCenter)); connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * heatmapSuccessor.get(0).get(0); } else if (heatMapOnStackAndHasPredecessor &&
		 * (isGapParCoordsCenteredAndHeatMapOnStack == true)){ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(parCoordsPredecessor.get(0).get(0),
		 * heatmapPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } else if
		 * (heatMapOnStackAndHasPredecessor && (isGapParCoordsOnStackAndHeatMapOnStack == true)){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * heatmapPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } else if
		 * (heatMapOnStackAndHasPredecessor){ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true); bundlingLine.addLine(createControlPoints(src,
		 * heatmapPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } } else
		 * if (currentID == parCoordID){ if (parCoordsOnStackAndHavePredecessor &&
		 * parCoordsOnStackAndHaveSucessor && isGapParCoordsOnStackAndHeatMapOnStack){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() >1){ VisLinkAnimationStage
		 * bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(bundlingPoints.get(activeViewID),
		 * parCoordsPredecessor.get(0).get(0), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src = parCoordsSuccessor.get(0).get(0); }
		 * else{ VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * parCoordsPredecessor.get(0).get(0), vecCenter));
		 * connectionLinesAllViews.add(bundlingLineToPredecessor); src = parCoordsSuccessor.get(0).get(0); } }
		 * else if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor){
		 * VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
		 * bundlingLineToPredecessor.addLine(createControlPoints(src, parCoordsPredecessor.get(0).get(0),
		 * vecCenter)); connectionLinesAllViews.add(bundlingLineToPredecessor); src =
		 * parCoordsSuccessor.get(0).get(0); } else if (parCoordsOnStackAndHavePredecessor &&
		 * isGapParCoordsOnStackAndHeatMapCentered){ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(heatmapPredecessor.get(0).get(0),
		 * parCoordsPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } else
		 * if (parCoordsOnStackAndHavePredecessor && (isGapParCoordsOnStackAndHeatMapOnStack == true)){ if
		 * (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1){ VisLinkAnimationStage
		 * bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
		 * parCoordsPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } else
		 * { VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
		 * parCoordsPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } }
		 * else if (parCoordsOnStackAndHavePredecessor){ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true); bundlingLine.addLine(createControlPoints(src,
		 * parCoordsPredecessor.get(0).get(0), vecCenter)); connectionLinesAllViews.add(bundlingLine); } }
		 * else{ if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && (currentID !=
		 * parCoordID)){ VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); connectionLinesAllViews.add(connections.get(currentID));
		 * src = bundlingPoints.get(currentID); } else if
		 * (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && (currentID != heatMapID)){
		 * VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		 * bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); connectionLinesAllViews.add(connections.get(currentID));
		 * src = bundlingPoints.get(currentID); } else{ VisLinkAnimationStage bundlingLine = new
		 * VisLinkAnimationStage(true); if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size() >
		 * 1){ bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); src = bundlingPoints.get(currentID); } else
		 * bundlingLine.addLine(createControlPoints(src, hashViewToCenterPoint.get(currentID), vecCenter));
		 * connectionLinesAllViews.add(bundlingLine); src = hashViewToCenterPoint.get(currentID); } } } } else
		 * if (ids.size() == 1){ int remoteId = ids.get(0);
		 * renderLineOnlyOneOtherViewOnStackAvailable(remoteId, idType, hashViewToCenterPoint, bundlingPoints,
		 * connections, connectionLinesAllViews); } }
		 */

		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	private int checkIfGapAvailableWhenRenderingFromCenter(ArrayList<Integer> viewsToBeVisited) {
		if (viewsToBeVisited.get(2) == -1 && viewsToBeVisited.get(1) != -1 && viewsToBeVisited.get(3) != -1)
			return 2;
		else if (viewsToBeVisited.get(3) == -1 && viewsToBeVisited.get(2) != -1 && viewsToBeVisited.get(4) != -1)
			return 3;
		return -1;
	}

	private ArrayList<Integer> getViewsOfCurrentPathStartingAtFocus(HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<Integer> getViewsOfCurrentPath = new ArrayList<Integer>();
		getViewsOfCurrentPath.add(focusLevel.getElementByPositionIndex(0).getGLView().getID());
		
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++){
			if ((stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID())) || 
				(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && heatMapID == stackLevel.getElementByPositionIndex(stackCount).getGLView().getID()) ||
				(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null && parCoordID == stackLevel.getElementByPositionIndex(stackCount).getGLView().getID())){
					getViewsOfCurrentPath.add(stackLevel.getElementByPositionIndex(stackCount).getGLView().getID());
			}
			else
				getViewsOfCurrentPath.add(-1);
		}
		
		return getViewsOfCurrentPath;
	}

	private void getPointsFromHeatMapAndParCoordsWhenRenderingFromCenter(int heatMapPredecessorID, int heatMapSuccessorID,
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
				getPointsIfNotOtherViewIsDynamicView(heatMapPredecessorID, heatMapPoints, hashViewToCenterPoint, HEATMAP, PREDECESSOR);
		}
		if (heatMapSuccessorID != -1){
			if (heatMapSuccessorID == parCoordID)
				getPointsIfOtherViewIsDynamicView(heatMapPoints, parCoordsPoints, HEATMAP, SUCCESSOR);				
			else
				getPointsIfNotOtherViewIsDynamicView(heatMapSuccessorID, heatMapPoints, hashViewToCenterPoint, HEATMAP, SUCCESSOR);		
		}
		if (parCoordsPredecessorID != -1){
			if (parCoordsPredecessorID == heatMapID)
				getPointsIfOtherViewIsDynamicView(heatMapPoints, parCoordsPoints, PARCOORDS, PREDECESSOR);
			else
				getPointsIfNotOtherViewIsDynamicView(parCoordsPredecessorID, parCoordsPoints, hashViewToCenterPoint, PARCOORDS, PREDECESSOR);
		}
		if (parCoordsSuccessorID != -1){
			if (parCoordsSuccessorID == heatMapID)
				getPointsIfOtherViewIsDynamicView(parCoordsPoints, heatMapPoints, PARCOORDS, SUCCESSOR);		
			else
				getPointsIfNotOtherViewIsDynamicView(parCoordsSuccessorID, parCoordsPoints, hashViewToCenterPoint, PARCOORDS, SUCCESSOR);	
		}
	}
	
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

	private void getPointsIfNotOtherViewIsDynamicView(int IDOfOtherView, ArrayList<ArrayList<Vec3f>> dynamicPointsList, HashMap<Integer, Vec3f> hashViewToCenterPoint, char type, int nextOrPrevious){
		float currentPath = -1;
		float minPath = Float.MAX_VALUE;
		
		ArrayList<Vec3f> optimalPoints = null;
		for (ArrayList<Vec3f> dynamicPoints : dynamicPointsList) {
			Vec3f predecessorPoint = dynamicPoints.get(0);
			Vec3f distanceToPredecessor = predecessorPoint.minus(hashViewToCenterPoint.get(IDOfOtherView));
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
		
		
/*		
		
		if (heatMapSuccessorID != parCoordID) {
			// getting heatmap successor

			currentSuccessorPath = -1;
			minSuccessorPath = Float.MAX_VALUE;
			if (parCoordsPredecessorID != -1 && parCoordsSucessorID != -1) {
				ArrayList<Vec3f> optimalparCoordsPredecessor = null;
				ArrayList<Vec3f> optimalparCoordsSuccessor = null;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f successorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessor =
						predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					Vec3f distanceToSuccessor =
						successorPoint.minus(hashViewToCenterPoint.get(parCoordsSucessorID));
					currentPredecessorPath = distanceToPredecessor.length();
					currentSuccessorPath = distanceToSuccessor.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalparCoordsPredecessor = parCoordsList;
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalparCoordsSuccessor = parCoordsList;
					}
				}
				parCoordsPredecessor.add(optimalparCoordsPredecessor);
				parCoordsSuccessor.add(optimalparCoordsSuccessor);
			}
			else if (parCoordsPredecessorID != -1) {
				ArrayList<Vec3f> optimalparCoordsPredecessor = null;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessor =
						predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					currentPredecessorPath = distanceToPredecessor.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalparCoordsPredecessor = parCoordsList;
					}
				}
				parCoordsPredecessor.add(optimalparCoordsPredecessor);
			}
		}
		else {
			if (parCoordsSucessorID != -1) {
				ArrayList<Vec3f> optimalparCoordsPredecessor = null;
				ArrayList<Vec3f> optimalHeatMapSucessor = null;
				ArrayList<Vec3f> optimalParCoordsSuccessor = null;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						Vec3f predecessorPoint = parCoordsList.get(0);
						Vec3f successorPoint = hashViewToCenterPoint.get(parCoordsSucessorID);
						Vec3f distanceToPredecessor = predecessorPoint.minus(heatMapList.get(0));
						Vec3f distanceToSuccessor = null;
						if (gap) {
							distanceToSuccessor = successorPoint.minus(heatMapList.get(0));
						}
						else {
							distanceToSuccessor = successorPoint.minus(parCoordsList.get(0));
						}
						currentSuccessorPath = distanceToSuccessor.length();
						currentPredecessorPath = distanceToPredecessor.length();
						if (currentPredecessorPath < minPredecessorPath) {
							minPredecessorPath = currentPredecessorPath;
							optimalparCoordsPredecessor = parCoordsList;
							optimalHeatMapSucessor = heatMapList;
						}
						if (currentSuccessorPath < minSuccessorPath) {
							minSuccessorPath = currentSuccessorPath;
							if (gap)
								optimalParCoordsSuccessor = heatMapList;
							else
								optimalParCoordsSuccessor = parCoordsList;
						}
					}
				}
				parCoordsPredecessor.add(optimalparCoordsPredecessor);
				parCoordsSuccessor.add(optimalParCoordsSuccessor);
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
		}

	}*/

	/**
	 * selects which lines have to be rendered if the only two views involved are the focus element and one
	 * stack element
	 * 
	 * @param remoteId
	 * @param idType
	 * @param hashViewToCenterPoint
	 * @param bundlingPoints
	 * @param connections
	 * @param connectionLinesAllViews
	 */
	private void renderLineOnlyOneOtherViewOnStackAvailable(int remoteId, EIDType idType,
		HashMap<Integer, Vec3f> hashViewToCenterPoint, HashMap<Integer, Vec3f> bundlingPoints,
		HashMap<Integer, VisLinkAnimationStage> connections,
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {
		VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		if (activeViewID == heatMapID) {
			if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size() > 1) {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(heatmapSuccessor), vecCenter), bundlingPoints.get(remoteId),
						vecCenter));
				else
					bundlingLine.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), bundlingPoints
						.get(remoteId), vecCenter));
			}
			else {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(heatmapSuccessor), vecCenter), hashViewToCenterPoint.get(remoteId),
						vecCenter));
				else
					bundlingLine.addLine(createControlPoints(heatmapSuccessor.get(0).get(0),
						hashViewToCenterPoint.get(remoteId), vecCenter));
			}
		}
		else if (activeViewID == parCoordID) {
			if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size() > 1) {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(parCoordsSuccessor), vecCenter), bundlingPoints.get(remoteId),
						vecCenter));
				else
					bundlingLine.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), bundlingPoints
						.get(remoteId), vecCenter));
			}
			else {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(parCoordsSuccessor), vecCenter), hashViewToCenterPoint.get(remoteId),
						vecCenter));
				else
					bundlingLine.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0),
						hashViewToCenterPoint.get(remoteId), vecCenter));
			}
		}
		else if (remoteId == heatMapID) {
			if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1) {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
				else
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						heatmapPredecessor.get(0).get(0), vecCenter));
			}
			else {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
				else
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						heatmapPredecessor.get(0).get(0), vecCenter));
			}
		}
		else if (remoteId == parCoordID) {
			if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1) {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
				else
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						parCoordsPredecessor.get(0).get(0), vecCenter));
			}
			else {
				if (multiplePoints)
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
				else
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						parCoordsPredecessor.get(0).get(0), vecCenter));
			}
		}
		else {
			bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID), bundlingPoints
				.get(remoteId), vecCenter));
		}
		if (activeViewID != heatMapID && activeViewID != parCoordID
			&& hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1) {
			connectionLinesAllViews.add(connections.get(activeViewID));
		}
		connectionLinesAllViews.add(bundlingLine);
		if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size() > 1) {
			if (remoteId == heatMapID) {
				VisLinkAnimationStage heatMapStage = heatMapStages.get(0);
				heatMapStage.setReverseLineDrawingDirection(true);
				connectionLinesAllViews.add(heatMapStage);
			}
			else if (remoteId == parCoordID) {
				VisLinkAnimationStage parCoordStage = parCoordStages.get(0);
				parCoordStage.setReverseLineDrawingDirection(true);
				connectionLinesAllViews.add(parCoordStage);
			}
			else
				connectionLinesAllViews.add(connections.get(remoteId));
		}
		else
			connectionLinesAllViews.add(new VisLinkAnimationStage());
	}

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

	private void renderFromStackLevel(GL gl, HashMap<Integer, VisLinkAnimationStage> connections,
		HashMap<Integer, Vec3f> bundlingPoints, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		int focusID = -1;
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null)
			focusID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if (stackLevel.getElementByPositionIndex(stackCount).getGLView() != null) {
				int id = stackLevel.getElementByPositionIndex(stackCount).getGLView().getID();
				if (bundlingPoints.containsKey(id))
					ids.add(id);
			}
		}
		int lastStackViewID = -1;
		while (ids.get(0) != activeViewID) {
			lastStackViewID = ids.get(0);
			ids.remove(0);
			ids.add(lastStackViewID);
		}
		if (focusID != -1)
			ids.add(1, focusID);

		if (multiplePoints) {
			if (((ids.size() > 1) && (focusID == -1)) || ((ids.size() > 2) && (focusID != -1))) {
				Vec3f src = null;
				if (activeViewID == heatMapID)
					src = calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
				else if (activeViewID == parCoordID)
					src = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
				else
					src = bundlingPoints.get(activeViewID);

				for (Integer currentID : ids) {
					VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
					if (currentID == heatMapID) {
						bundlingLine.addLine(createControlPoints(src, calculateBundlingPoint(
							calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						connectionLinesAllViews.add(heatMapStages.get(PREDECESSOR));
						if (currentID != lastStackViewID) {
							connectionLinesAllViews.add(heatMapStages.get(SUCCESSOR));
							src = calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
						}
					}
					else if (currentID == parCoordID) {
						bundlingLine.addLine(createControlPoints(src, calculateBundlingPoint(
							calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						connectionLinesAllViews.add(parCoordStages.get(PREDECESSOR));
						if (currentID != lastStackViewID) {
							connectionLinesAllViews.add(parCoordStages.get(SUCCESSOR));
							src = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
						}
					}
					else {
						bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID),
							vecCenter));
						connectionLinesAllViews.add(connections.get(currentID));
						src = bundlingPoints.get(currentID);
					}
				}
			}
			else {
				if (focusLevel.getElementByPositionIndex(0).getGLView() != null) {
					renderLineOnlyOneOtherViewOnFocusAvailable(idType, hashViewToCenterPoint, bundlingPoints,
						connections, connectionLinesAllViews);
				}
			}
		}
		else {
			if (((ids.size() > 1) && (focusID == -1)) || ((ids.size() > 2) && (focusID != -1))) {
				Vec3f src = null;
				if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1) {
					if (activeViewID == heatMapID)
						src = calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter);
					else if (activeViewID == parCoordID)
						src = calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter);
					else
						src = bundlingPoints.get(activeViewID);
				}
				else if (activeViewID == heatMapID)
					src = heatmapSuccessor.get(0).get(0);
				else if (activeViewID == parCoordID)
					src = parCoordsSuccessor.get(0).get(0);
				else
					src = hashViewToCenterPoint.get(activeViewID);

				for (Integer currentID : ids) {
					VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
					if (currentID == heatMapID) {
						bundlingLine.addLine(createControlPoints(src, heatmapPredecessor.get(0).get(0),
							vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						if (currentID != lastStackViewID)
							src = heatmapSuccessor.get(0).get(0);
					}
					else if (currentID == parCoordID) {
						bundlingLine.addLine(createControlPoints(src, parCoordsPredecessor.get(0).get(0),
							vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						src = parCoordsSuccessor.get(0).get(0);
					}
					else {
						if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size() > 1) {
							bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID),
								vecCenter));
							connectionLinesAllViews.add(connections.get(currentID));
							src = bundlingPoints.get(currentID);
						}
						else {
							bundlingLine.addLine(createControlPoints(src, hashViewToCenterPoint
								.get(currentID), vecCenter));
							connectionLinesAllViews.add(bundlingLine);
							src = hashViewToCenterPoint.get(currentID);
						}
					}
				}
			}
			else {
				if (focusLevel.getElementByPositionIndex(0).getGLView() != null) {
					renderLineOnlyOneOtherViewOnFocusAvailable(idType, hashViewToCenterPoint, bundlingPoints,
						connections, connectionLinesAllViews);
				}
			}
		}
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	/**
	 * Method that selects the lines to be drawn if the only view except the active view is located in focus
	 * level
	 * 
	 * @param remoteId
	 * @param idType
	 * @param hashViewToCenterPoint
	 * @param bundlingPoints
	 * @param connections
	 * @param connectionLinesAllViews
	 */
	private void renderLineOnlyOneOtherViewOnFocusAvailable(EIDType idType,
		HashMap<Integer, Vec3f> hashViewToCenterPoint, HashMap<Integer, Vec3f> bundlingPoints,
		HashMap<Integer, VisLinkAnimationStage> connections,
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews) {
		VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
		VisLinkAnimationStage heatMapStage = null;
		int remoteId = focusLevel.getElementByPositionIndex(0).getGLView().getID();
		if (hashViewToCenterPoint.get(remoteId) == null)
			return;
		if (remoteId == heatMapID) {
			if (multiplePoints) {
				heatMapStage = heatMapStages.get(PREDECESSOR);
				heatMapStage.setReverseLineDrawingDirection(true);
			}
			if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() == 1) {
				if (multiplePoints) {
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(heatMapStage);
				}
				else {
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						heatmapSuccessor.get(0).get(0), vecCenter));
					connectionLinesAllViews.add(bundlingLine);
				}
			}
			else {
				if (multiplePoints) {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(heatMapStage);
				}
				else {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						heatmapSuccessor.get(0).get(0), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
				}
			}
		}
		else if (remoteId == parCoordID) {
			VisLinkAnimationStage parCoordStage = null;
			if (multiplePoints) {
				parCoordStage = parCoordStages.get(PREDECESSOR);
				parCoordStage.setReverseLineDrawingDirection(true);
			}
			if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() == 1) {
				if (multiplePoints) {
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(parCoordStage);
				}
				else {
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						parCoordsSuccessor.get(0).get(0), vecCenter));
					connectionLinesAllViews.add(bundlingLine);

				}
			}
			else {
				if (multiplePoints) {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(parCoordStage);
				}
				else {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						parCoordsSuccessor.get(0).get(0), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
				}
			}
		}
		else {
			if ((hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() == 1)
				&& (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size() == 1)) {
				bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
					hashViewToCenterPoint.get(remoteId), vecCenter));
				connectionLinesAllViews.add(bundlingLine);
			}
			else if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() == 1) {
				if (activeViewID == heatMapID)
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						calculateBundlingPoint(calculateCenter(heatmapPredecessor), vecCenter), vecCenter));
				else if (activeViewID == parCoordID)
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						calculateBundlingPoint(calculateCenter(parCoordsPredecessor), vecCenter), vecCenter));
				else
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID),
						bundlingPoints.get(activeViewID), vecCenter));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(remoteId));
			}
			else if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size() == 1) {
				if (activeViewID == heatMapID) {
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(heatmapPredecessor), vecCenter), hashViewToCenterPoint.get(remoteId),
						vecCenter));
					connectionLinesAllViews.add(heatMapStages.get(PREDECESSOR));
				}
				else if (activeViewID == parCoordID) {
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(parCoordsPredecessor), vecCenter), hashViewToCenterPoint
						.get(remoteId), vecCenter));
					connectionLinesAllViews.add(parCoordStages.get(PREDECESSOR));
				}
				else {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						hashViewToCenterPoint.get(remoteId), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
				}
				connectionLinesAllViews.add(bundlingLine);
			}
			else {
				if ((activeViewID == heatMapID) && (remoteId == parCoordID)) {
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(heatmapPredecessor), vecCenter), calculateBundlingPoint(
						calculateCenter(parCoordsSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(heatMapStages.get(PREDECESSOR));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(parCoordStages.get(SUCCESSOR));
				}
				else if ((activeViewID == parCoordID) && (remoteId == heatMapID)) {
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(parCoordsPredecessor), vecCenter), calculateBundlingPoint(
						calculateCenter(heatmapSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(heatMapStages.get(PREDECESSOR));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(heatMapStages.get(SUCCESSOR));
				}
				else if (activeViewID == heatMapID) {
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(heatmapPredecessor), vecCenter), bundlingPoints.get(remoteId),
						vecCenter));
					connectionLinesAllViews.add(heatMapStages.get(PREDECESSOR));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(connections.get(remoteId));
				}
				else if (remoteId == heatMapID) {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						calculateBundlingPoint(calculateCenter(heatmapSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(heatMapStages.get(SUCCESSOR));
				}
				else if (activeViewID == parCoordID) {
					bundlingLine.addLine(createControlPoints(calculateBundlingPoint(
						calculateCenter(parCoordsPredecessor), vecCenter), bundlingPoints.get(remoteId),
						vecCenter));
					connectionLinesAllViews.add(heatMapStages.get(PREDECESSOR));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(connections.get(remoteId));
				}
				else if (remoteId == parCoordID) {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID),
						calculateBundlingPoint(calculateCenter(parCoordsSuccessor), vecCenter), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(parCoordStages.get(SUCCESSOR));
				}
				else {
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID), bundlingPoints
						.get(remoteId), vecCenter));
					connectionLinesAllViews.add(connections.get(activeViewID));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(connections.get(remoteId));
				}
			}
		}
	}

	@Override
	/**
	 *  Calculates the optimal heatmap and parcoord connection point if not more than one entry is available
	 * @param heatMapPoints set of heatmap points to choose from
	 * @param heatMapID id of heatmap view
	 * @param parCoordsPoints set of parcoord points to choose from
	 * @param parCoordID id of parcoord view
	 * @param hashViewToCenterPoint
	 * @return
	 */
	protected ArrayList<ArrayList<Vec3f>> calculateOptimalSinglePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		multiplePoints = false;
		boolean isHeatMapCentered = false;
		boolean areParCoordsCentered = false;
		if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLHeatMap)
			isHeatMapCentered = true;
		else if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLParallelCoordinates)
			areParCoordsCentered = true;
		ArrayList<ArrayList<Vec3f>> optimalDynamicPoints = null;

		if (isHeatMapCentered)
			optimalDynamicPoints =
				getOptimalSingleHeatMapPointsCenter(hashViewToCenterPoint, heatMapPoints, parCoordsPoints);
		else if (areParCoordsCentered)
			optimalDynamicPoints =
				getOptimalSingleParCoordPointsCenter(hashViewToCenterPoint, heatMapPoints, parCoordsPoints);
		else
			optimalDynamicPoints =
				getOptimalSinglePointsStack(hashViewToCenterPoint, heatMapPoints, parCoordsPoints);

		return optimalDynamicPoints;
	}

	private ArrayList<ArrayList<Vec3f>> getOptimalSinglePointsStack(
		HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<ArrayList<Vec3f>> heatMapPoints,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints) {
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Vec3f> optimalHeatMapPredecessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalHeatMapSucessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoordsPredecessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoordsSucessor = new ArrayList<Vec3f>();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;
		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		float minPredecessorPath = Float.MAX_VALUE;
		float minSuccessorPath = Float.MAX_VALUE;
		float currentPredecessorPath = -1;
		float currentSuccessorPath = -1;

		// check if there's a gap
		for (int count = 0; count < stackLevel.getCapacity() - 1; count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
				for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
					if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
						if (reverseCount == count + 2)
							isGapParCoordsOnStackAndHeatMapOnStack = true;
					}
				}
			}
		}

		// get id of predecessor/successor to heatmap and/or parCoords
		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
				if (count > 0) {
					for (int predecessorCount = count - 1; predecessorCount >= 0; predecessorCount--) {
						if ((stackLevel.getElementByPositionIndex(predecessorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								predecessorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(predecessorCount).getGLView().getID() == parCoordID))) {
							heatMapPredecessorID =
								stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID();
							heatMapOnStackAndHasPredecessor = true;
							break;
						}
					}
					if (heatMapOnStackAndHasPredecessor == false) {
						if ((focusLevel.getElementByPositionIndex(0).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(focusLevel.getElementByPositionIndex(0)
								.getGLView().getID())) || (focusLevel.getElementByPositionIndex(0)
								.getGLView().getID() == parCoordID))) {
							heatMapPredecessorID =
								focusLevel.getElementByPositionIndex(0).getGLView().getID();
							heatMapOnStackAndHasPredecessor = true;
						}
					}
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null) {
						heatMapPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						heatMapOnStackAndHasPredecessor = true;
					}
					for (int successorCount = 1; successorCount < stackLevel.getCapacity(); successorCount++) {
						if ((stackLevel.getElementByPositionIndex(successorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								successorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(successorCount).getGLView().getID() == parCoordID))) {
							heatMapSuccessorID =
								stackLevel.getElementByPositionIndex(successorCount).getGLView().getID();
							heatMapOnStackAndHasSucessor = true;
							break;
						}
					}
				}
				if (count < (stackLevel.getCapacity() - 1)) {
					for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if ((stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID() == parCoordID))) {
							heatMapSuccessorID =
								stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							heatMapOnStackAndHasSucessor = true;
							break;
						}
					}
				}
			}
			else if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
				if (count > 0) {
					for (int predecessorCount = count - 1; predecessorCount >= 0; predecessorCount--) {
						if ((stackLevel.getElementByPositionIndex(predecessorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								predecessorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(predecessorCount).getGLView().getID() == heatMapID))) {
							parCoordsPredecessorID =
								stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID();
							parCoordsOnStackAndHavePredecessor = true;
							break;
						}
					}
					if (parCoordsOnStackAndHavePredecessor == false) {
						if ((focusLevel.getElementByPositionIndex(0).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(focusLevel.getElementByPositionIndex(0)
								.getGLView().getID())) || (focusLevel.getElementByPositionIndex(0)
								.getGLView().getID() == heatMapID))) {
							parCoordsPredecessorID =
								focusLevel.getElementByPositionIndex(0).getGLView().getID();
							parCoordsOnStackAndHavePredecessor = true;
						}
					}
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null) {
						parCoordsPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						parCoordsOnStackAndHavePredecessor = true;
					}
					for (int successorCount = 1; successorCount < stackLevel.getCapacity(); successorCount++) {
						if ((stackLevel.getElementByPositionIndex(successorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								successorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(successorCount).getGLView().getID() == heatMapID))) {
							parCoordsSuccessorID =
								stackLevel.getElementByPositionIndex(successorCount).getGLView().getID();
							parCoordsOnStackAndHaveSucessor = true;
							break;
						}
					}
				}
				if (count < (stackLevel.getCapacity() - 1)) {
					for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if ((stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID() == heatMapID))) {
							parCoordsSuccessorID =
								stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							parCoordsOnStackAndHaveSucessor = true;
							break;
						}
					}
				}
			}
		}

		if ((heatMapPredecessorID != parCoordID) && (heatMapSuccessorID != parCoordID)) {
			minPredecessorPath = Float.MAX_VALUE;
			currentPredecessorPath = -1;
			if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor
				&& isGapParCoordsOnStackAndHeatMapOnStack) {
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f successorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
			else if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor) {
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f successorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
			else if (heatMapOnStackAndHasPredecessor && !isGapParCoordsOnStackAndHeatMapOnStack) {
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
			}
			else if (heatMapOnStackAndHasPredecessor && isGapParCoordsOnStackAndHeatMapOnStack) {
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec = null;
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null)
						distanceToPredecessorVec =
							predecessorPoint.minus(hashViewToCenterPoint.get(focusLevel
								.getElementByPositionIndex(0).getGLView().getID()));
					else
						distanceToPredecessorVec = hashViewToCenterPoint.get(heatMapPredecessorID);
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
			}
		}
		if ((parCoordsPredecessorID != heatMapID) && (parCoordsSuccessorID != heatMapID)) {
			minPredecessorPath = Float.MAX_VALUE;
			currentPredecessorPath = -1;
			if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor) {
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f successorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(parCoordsSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsPredecessor = parCoordsList;
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalParCoordsSucessor = parCoordsList;
					}
				}
				parCoordsSuccessor.add(optimalParCoordsSucessor);
				parCoordsPredecessor.add(optimalParCoordsPredecessor);
			}
			else if (parCoordsOnStackAndHavePredecessor && isGapParCoordsOnStackAndHeatMapOnStack) {
				minPredecessorPath = Float.MAX_VALUE;
				currentPredecessorPath = -1;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsPredecessor = parCoordsList;
					}
				}
				parCoordsPredecessor.add(optimalParCoordsPredecessor);
			}
			else if (parCoordsOnStackAndHavePredecessor) {
				minPredecessorPath = Float.MAX_VALUE;
				currentPredecessorPath = -1;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsPredecessor = parCoordsList;
					}
				}
				parCoordsPredecessor.add(optimalParCoordsPredecessor);
			}
		}
		if (heatMapPredecessorID == parCoordID) {
			currentPredecessorPath = -1;
			minPredecessorPath = Float.MAX_VALUE;
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
					currentPredecessorPath = distanceVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsSucessor = parCoordsList;
						optimalHeatMapPredecessor = heatMapList;

					}
				}
			}
			parCoordsSuccessor.add(optimalParCoordsSucessor);
			heatmapPredecessor.add(optimalHeatMapPredecessor);
			currentPredecessorPath = -1;
			minPredecessorPath = Float.MAX_VALUE;
			for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
				Vec3f predecessorPoint = parCoordsList.get(0);
				if (hashViewToCenterPoint.get(parCoordsPredecessor) == null)
					return null;
				Vec3f distanceToPredecessorVec =
					predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
				currentPredecessorPath = distanceToPredecessorVec.length();
				if (currentPredecessorPath < minPredecessorPath) {
					minPredecessorPath = currentPredecessorPath;
					optimalParCoordsPredecessor = parCoordsList;
				}
			}
			parCoordsPredecessor.add(optimalParCoordsPredecessor);
			if (heatMapOnStackAndHasSucessor) {
				currentSuccessorPath = -1;
				minSuccessorPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f successorPoint = heatMapList.get(0);
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
		}
		if (heatMapSuccessorID == parCoordID) {
			currentSuccessorPath = -1;
			minSuccessorPath = Float.MAX_VALUE;
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
					currentSuccessorPath = distanceVec.length();
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalParCoordsPredecessor = parCoordsList;
						optimalHeatMapSucessor = heatMapList;

					}
				}
			}
			parCoordsPredecessor.add(optimalParCoordsPredecessor);
			heatmapSuccessor.add(optimalHeatMapSucessor);
			currentPredecessorPath = -1;
			minPredecessorPath = Float.MAX_VALUE;
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
				Vec3f predecessorPoint = heatMapList.get(0);
				if (heatMapPredecessorID == -1) {
					int tempID = -1;
					for (int counter = stackLevel.getCapacity() - 1; counter >= 0; counter--) {
						if (stackLevel.getElementByPositionIndex(counter).getGLView() != null) {
							tempID = stackLevel.getElementByPositionIndex(counter).getGLView().getID();
							break;
						}
					}
					if (tempID != heatMapSuccessorID && tempID != -1
						&& hashViewToCenterPoint.containsKey(tempID))
						heatMapPredecessorID = tempID;
				}
				Vec3f distanceToPredecessorVec =
					predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
				currentPredecessorPath = distanceToPredecessorVec.length();
				if (currentPredecessorPath < minPredecessorPath) {
					minPredecessorPath = currentPredecessorPath;
					optimalHeatMapPredecessor = heatMapList;
				}
			}
			heatmapPredecessor.add(optimalHeatMapPredecessor);
			if (parCoordsOnStackAndHaveSucessor) {
				currentSuccessorPath = -1;
				minSuccessorPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f successorPoint = parCoordsList.get(0);
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(parCoordsSuccessorID));
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						optimalParCoordsSucessor = parCoordsList;
					}
				}
				parCoordsSuccessor.add(optimalParCoordsPredecessor);
			}
		}

		if (heatMapOnStackAndHasPredecessor)
			pointsList.add(optimalHeatMapPredecessor);
		else if (heatMapOnStackAndHasSucessor)
			pointsList.add(optimalHeatMapSucessor);
		if (parCoordsOnStackAndHavePredecessor)
			pointsList.add(optimalParCoordsPredecessor);
		else
			pointsList.add(optimalParCoordsSucessor);

		return pointsList;
	}

	/**
	 * calculates shortest path between parcoords and another pathway, if parcoords are in focus
	 * 
	 * @param hashViewToCenterPoint
	 * @param heatMapPoints
	 * @param heatMapID
	 * @param parCoordsPoints
	 * @param parCoordID
	 * @return
	 */
	private ArrayList<ArrayList<Vec3f>> getOptimalSingleParCoordPointsCenter(
		HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<ArrayList<Vec3f>> heatMapPoints,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints) {
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Vec3f> optimalHeatMapPredecessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalHeatMapSucessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoords = new ArrayList<Vec3f>();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();

		boolean isHeatMap = false;
		float minPath = Float.MAX_VALUE;
		float minPathToActiveView = Float.MAX_VALUE;
		int predecessorID = -1;
		int successorID = -1;
		int nextView = -1;
		float currentPath = -1;
		float currentPathToActiveView = -1;

		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
					isHeatMap = true;
					break;
				}
				else if (hashViewToCenterPoint.containsKey(nextView))
					break;
			}
		}
		// heatmap is first view to be visited, heatmap has no predecessor on the stack (only bundling to
		// successor needed to be calculated)
		if (isHeatMap) {
			heatMapOnStackAndHasPredecessor = true;
			for (int count = 0; count < stackLevel.getCapacity(); count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
					if (count < (stackLevel.getCapacity() - 1)) {
						for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
							if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
								if (reverseCount == (count + 2))
									isGapParCoordsCenteredAndHeatMapOnStack = true;
								successorID =
									stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
								heatMapOnStackAndHasSucessor = true;
								break;
							}
						}
					}
				}
			}
			ArrayList<Vec3f> optimalParCoordsPredecessor = null;
			for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f remoteBundlingPoint = heatMapList.get(0);
					Vec3f bundlingToActiveView = hashViewToCenterPoint.get(activeViewID);
					Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
					Vec3f distanceToActiveView = bundlingToActiveView.minus(parCoordsList.get(0));
					currentPathToActiveView = distanceToActiveView.length();
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalHeatMapPredecessor = heatMapList;
						optimalParCoords = parCoordsList;
					}
					if (currentPathToActiveView < minPathToActiveView) {
						minPathToActiveView = currentPathToActiveView;
						optimalParCoordsPredecessor = parCoordsList;
					}
				}
			}
			heatmapPredecessor.add(optimalHeatMapPredecessor);
			parCoordsSuccessor.add(optimalParCoords);
			parCoordsPredecessor.add(optimalParCoordsPredecessor);
			if (heatMapOnStackAndHasSucessor && !isGapParCoordsCenteredAndHeatMapOnStack) {
				minPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f remoteBundlingPoint = heatMapList.get(0);
					if (hashViewToCenterPoint.get(successorID) == null)
						return null;
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
			else if (heatMapOnStackAndHasSucessor && isGapParCoordsCenteredAndHeatMapOnStack) {
				int preID = -1;
				for (int count = stackLevel.getCapacity() - 1; count >= 0; count--) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
						preID = stackLevel.getElementByPositionIndex(count).getGLView().getID();
						break;
					}
				}
				currentPath = -1;
				minPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(preID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalParCoords = parCoordsList;
					}
				}
				heatmapSuccessor.add(optimalParCoords);
			}
		}
		// the first view to be connected to is not the heatmap, so heatmap maybe has a predecessor and/or
		// successor
		else {
			for (int count = 0; count < stackLevel.getCapacity() - 1; count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() != null
					&& stackLevel.getElementByPositionIndex(count).getGLView().getID() == nextView) {
					for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
							if (reverseCount == count + 2) {
								isGapParCoordsCenteredAndHeatMapOnStack = true;
							}
							break;
						}

					}
					break;
				}
			}

			for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
				Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(nextView);
				if (remoteBundlingPoint == null)
					return null;
				Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
				currentPath = distanceVec.length();
				if (currentPath < minPath) {
					minPath = currentPath;
					optimalParCoords = parCoordsList;
				}
			}
			parCoordsSuccessor.add(optimalParCoords);
			if (heatMapID != -1) {
				for (int count = 0; count < stackLevel.getCapacity(); count++) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
						if ((count < (stackLevel.getCapacity() - 1)) && count > 0) {
							for (int reverseCount = (count - 1); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							break;
						}
						else if (count == (stackLevel.getCapacity() - 1)) {
							for (int reverseCount = (stackLevel.getCapacity() - 2); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
						else if (count == 0) {
							for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
					}
				}
				if (hashViewToCenterPoint.containsKey(predecessorID)
					&& !isGapParCoordsCenteredAndHeatMapOnStack) {
					heatMapOnStackAndHasPredecessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					// getting optimal point to predecessor
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(predecessorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							optimalPoints = heatMapList;
							optimalHeatMapPredecessor = heatMapList;
						}
					}
					heatmapPredecessor.add(optimalPoints);
				}
				else if (hashViewToCenterPoint.containsKey(predecessorID)
					&& isGapParCoordsCenteredAndHeatMapOnStack) {
					heatMapOnStackAndHasPredecessor = true;
					currentPath = -1;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalParCoordsPredecessor = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
							hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
							hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
							Vec3f remoteBundlingPoint = parCoordsList.get(0);
							Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
							currentPath = distanceVec.length();
							if (currentPath < minPath) {
								minPath = currentPath;
								optimalParCoordsPredecessor = parCoordsList;
								optimalHeatMapPredecessor = heatMapList;

							}
						}
					}
					parCoordsPredecessor.add(optimalParCoordsPredecessor);
					heatmapPredecessor.add(optimalHeatMapPredecessor);
				}
				if (hashViewToCenterPoint.containsKey(successorID)) {
					heatMapOnStackAndHasSucessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					// getting optimal point to successor
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(successorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							optimalPoints = heatMapList;
							optimalHeatMapSucessor = heatMapList;
						}
					}
					heatmapSuccessor.add(optimalPoints);
				}
			}
		}
		if (heatMapOnStackAndHasPredecessor)
			pointsList.add(optimalHeatMapPredecessor);
		pointsList.add(optimalParCoords);
		return pointsList;

	}

	/**
	 * calculates shortest distance between heatmap and one other view, if heatmap is in focus
	 * 
	 * @param hashViewToCenterPoint
	 * @param heatMapPoints
	 * @param heatMapID
	 * @param parCoordsPoints
	 * @param parCoordID
	 * @return
	 */

	private ArrayList<ArrayList<Vec3f>> getOptimalSingleHeatMapPointsCenter(
		HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<ArrayList<Vec3f>> heatMapPoints,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints) {
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Vec3f> optimalParCoordPredecessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalparCoordSucessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();

		boolean areParCoords = false;
		float minPath = Float.MAX_VALUE;
		float minPathToActiveView = Float.MAX_VALUE;
		int predecessorID = -1;
		int successorID = -1;
		int nextView = -1;
		float currentPath = -1;
		float currentPathToActiveView = -1;

		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
					areParCoords = true;
					break;
				}
				else if (hashViewToCenterPoint.containsKey(nextView))
					break;
			}
		}
		// parCoords are first view to be visited, parCoords have no predecessor on the stack (only bundling
		// to successor needed to be calculated)
		if (areParCoords) {
			parCoordsOnStackAndHavePredecessor = true;
			for (int count = 0; count < stackLevel.getCapacity(); count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
					if (count < (stackLevel.getCapacity() - 1)) {
						for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
							if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
								if (reverseCount == (count + 2))
									isGapParCoordsOnStackAndHeatMapCentered = true;
								successorID =
									stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
								parCoordsOnStackAndHaveSucessor = true;
								break;
							}
						}
					}
				}
			}
			ArrayList<Vec3f> optimalHeatMapPredecessor = null;
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f bundlingToActiveView = hashViewToCenterPoint.get(activeViewID);
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
					Vec3f distanceToActiveView = bundlingToActiveView.minus(heatMapList.get(0));
					currentPath = distanceVec.length();
					currentPathToActiveView = distanceToActiveView.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalParCoordPredecessor = parCoordsList;
						optimalHeatMap = heatMapList;
					}
					if (currentPathToActiveView < minPathToActiveView) {
						minPathToActiveView = currentPathToActiveView;
						optimalHeatMapPredecessor = heatMapList;
					}
				}
			}
			parCoordsPredecessor.add(optimalParCoordPredecessor);
			heatmapSuccessor.add(optimalHeatMap);
			heatmapPredecessor.add(optimalHeatMapPredecessor);
			if (parCoordsOnStackAndHaveSucessor && !isGapParCoordsOnStackAndHeatMapCentered) {
				minPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					if (hashViewToCenterPoint.get(successorID) == null)
						return null;
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalparCoordSucessor = parCoordsList;
					}
				}
				parCoordsSuccessor.add(optimalparCoordSucessor);
			}
			else if (parCoordsOnStackAndHaveSucessor && isGapParCoordsOnStackAndHeatMapCentered) {
				int preID = -1;
				for (int count = stackLevel.getCapacity() - 1; count >= 0; count--) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
						preID = stackLevel.getElementByPositionIndex(count).getGLView().getID();
						break;
					}
				}
				minPath = Float.MAX_VALUE;
				currentPath = -1;
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f remoteBundlingPoint = heatMapList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(preID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalparCoordSucessor = heatMapList;
					}
				}
				parCoordsSuccessor.add(optimalparCoordSucessor);
			}
		}
		// the first view to be connected to are not the parCoords, so parCoords maybe have a predecessor
		// and/or successor
		else {
			for (int count = 0; count < stackLevel.getCapacity() - 1; count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() != null
					&& stackLevel.getElementByPositionIndex(count).getGLView().getID() == nextView) {
					for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
							if (reverseCount == count + 2) {
								isGapParCoordsOnStackAndHeatMapCentered = true;
							}
							break;
						}

					}
					break;
				}
			}

			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
				Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(nextView);
				if (remoteBundlingPoint == null)
					return null;
				Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
				currentPath = distanceVec.length();
				if (currentPath < minPath) {
					minPath = currentPath;
					optimalHeatMap = heatMapList;
				}
			}
			heatmapSuccessor.add(optimalHeatMap);
			if (parCoordID != -1) {
				for (int count = 0; count < stackLevel.getCapacity(); count++) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
						if ((count < (stackLevel.getCapacity() - 1)) && count > 0) {
							for (int reverseCount = (count - 1); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							break;
						}
						else if (count == (stackLevel.getCapacity() - 1)) {
							for (int reverseCount = (stackLevel.getCapacity() - 2); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
						else if (count == 0) {
							for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
					}
				}
				if (hashViewToCenterPoint.containsKey(predecessorID)
					&& !isGapParCoordsOnStackAndHeatMapCentered) {
					parCoordsOnStackAndHavePredecessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					// getting optimal point to predecessor
					for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
						hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(predecessorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							optimalPoints = parCoordsList;
							optimalParCoordPredecessor = parCoordsList;
						}
					}
					parCoordsPredecessor.add(optimalPoints);
				}
				else if (hashViewToCenterPoint.containsKey(predecessorID)
					&& isGapParCoordsOnStackAndHeatMapCentered) {
					parCoordsOnStackAndHavePredecessor = true;
					currentPath = -1;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalParCoordsPredecessor = new ArrayList<Vec3f>();
					ArrayList<Vec3f> optimalHeatMapPredecessor = new ArrayList<Vec3f>();
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
							hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
							hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
							Vec3f remoteBundlingPoint = parCoordsList.get(0);
							Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
							currentPath = distanceVec.length();
							if (currentPath < minPath) {
								minPath = currentPath;
								optimalParCoordsPredecessor = parCoordsList;
								optimalHeatMapPredecessor = heatMapList;
							}
						}
					}
					parCoordsPredecessor.add(optimalParCoordsPredecessor);
					heatmapPredecessor.add(optimalHeatMapPredecessor);
				}

				if (hashViewToCenterPoint.containsKey(successorID)) {
					parCoordsOnStackAndHaveSucessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					// getting optimal point to successor
					for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
						hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(successorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							optimalPoints = parCoordsList;
							optimalparCoordSucessor = parCoordsList;
						}
					}
					parCoordsSuccessor.add(optimalPoints);
				}
			}
		}
		if (heatmapSuccessor.size() > 0)
			pointsList.add(heatmapSuccessor.get(0));
		else
			pointsList.add(heatmapPredecessor.get(0));
		if (parCoordsOnStackAndHavePredecessor)
			pointsList.add(parCoordsPredecessor.get(0));
		return pointsList;
	}

	protected ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		multiplePoints = true;
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();

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
		boolean isHeatMapCentered = false;
		boolean areParCoordsCentered = false;
		if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLHeatMap)
			isHeatMapCentered = true;
		else if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLParallelCoordinates)
			areParCoordsCentered = true;
		ArrayList<ArrayList<ArrayList<Vec3f>>> optimalDynamicPoints = null;

		if (isHeatMapCentered)
			optimalDynamicPoints =
				getOptimalMultipleHeatMapPointsCenter(hashViewToCenterPoint, multipleHeatMapPoints,
					heatMapCenterPoints, multipleParCoordPoints, parCoordCenterPoints);
		else if (areParCoordsCentered)
			optimalDynamicPoints =
				getOptimalMultipleParCoordPointsCenter(hashViewToCenterPoint, multipleHeatMapPoints,
					heatMapCenterPoints, multipleParCoordPoints, parCoordCenterPoints);
		else
			optimalDynamicPoints =
				getOptimalMultiplePointsStack(hashViewToCenterPoint, multipleHeatMapPoints,
					heatMapCenterPoints, multipleParCoordPoints, parCoordCenterPoints);

		return optimalDynamicPoints;

	}

	private ArrayList<ArrayList<ArrayList<Vec3f>>> getOptimalMultiplePointsStack(
		HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleHeatMapPoints, ArrayList<Vec3f> heatMapCenterPoints,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleParCoordPoints, ArrayList<Vec3f> parCoordCenterPoints) {

		ArrayList<ArrayList<ArrayList<Vec3f>>> pointsList = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();
		int heatMapSuccessorID = -1;
		int parCoordsSuccessorID = -1;
		int heatMapPredecessorID = -1;
		int parCoordsPredecessorID = -1;
		float minPredecessorPath = Float.MAX_VALUE;
		float minSuccessorPath = Float.MAX_VALUE;
		float currentPredecessorPath = -1;
		float currentSuccessorPath = -1;

		// check if there's a gap
		for (int count = 0; count < stackLevel.getCapacity() - 1; count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
				for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
					if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
						if (reverseCount == count + 2)
							isGapParCoordsOnStackAndHeatMapOnStack = true;
					}
				}
			}
		}

		// get id of predecessor/successor to heatmap and/or parCoords
		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
				if (count > 0) {
					for (int predecessorCount = count - 1; predecessorCount >= 0; predecessorCount--) {
						if ((stackLevel.getElementByPositionIndex(predecessorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								predecessorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(predecessorCount).getGLView().getID() == parCoordID))) {
							heatMapPredecessorID =
								stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID();
							heatMapOnStackAndHasPredecessor = true;
							break;
						}
					}
					if (heatMapOnStackAndHasPredecessor == false) {
						if ((focusLevel.getElementByPositionIndex(0).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(focusLevel.getElementByPositionIndex(0)
								.getGLView().getID())) || (focusLevel.getElementByPositionIndex(0)
								.getGLView().getID() == parCoordID))) {
							heatMapPredecessorID =
								focusLevel.getElementByPositionIndex(0).getGLView().getID();
							heatMapOnStackAndHasPredecessor = true;
						}
					}
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null) {
						heatMapPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						heatMapOnStackAndHasPredecessor = true;
					}
					for (int successorCount = 1; successorCount < stackLevel.getCapacity(); successorCount++) {
						if ((stackLevel.getElementByPositionIndex(successorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								successorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(successorCount).getGLView().getID() == parCoordID))) {
							heatMapSuccessorID =
								stackLevel.getElementByPositionIndex(successorCount).getGLView().getID();
							heatMapOnStackAndHasSucessor = true;
							break;
						}
					}
				}
				if (count < (stackLevel.getCapacity() - 1)) {
					for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if ((stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID() == parCoordID))) {
							heatMapSuccessorID =
								stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							heatMapOnStackAndHasSucessor = true;
							break;
						}
					}
				}
			}
			else if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
				if (count > 0) {
					for (int predecessorCount = count - 1; predecessorCount >= 0; predecessorCount--) {
						if ((stackLevel.getElementByPositionIndex(predecessorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								predecessorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(predecessorCount).getGLView().getID() == heatMapID))) {
							parCoordsPredecessorID =
								stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID();
							parCoordsOnStackAndHavePredecessor = true;
							break;
						}
					}
					if (parCoordsOnStackAndHavePredecessor == false) {
						if ((focusLevel.getElementByPositionIndex(0).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(focusLevel.getElementByPositionIndex(0)
								.getGLView().getID())) || (focusLevel.getElementByPositionIndex(0)
								.getGLView().getID() == heatMapID))) {
							parCoordsPredecessorID =
								focusLevel.getElementByPositionIndex(0).getGLView().getID();
							parCoordsOnStackAndHavePredecessor = true;
						}
					}
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null) {
						parCoordsPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						parCoordsOnStackAndHavePredecessor = true;
					}
					for (int successorCount = 1; successorCount < stackLevel.getCapacity(); successorCount++) {
						if ((stackLevel.getElementByPositionIndex(successorCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								successorCount).getGLView().getID())) || (stackLevel
								.getElementByPositionIndex(successorCount).getGLView().getID() == heatMapID))) {
							parCoordsSuccessorID =
								stackLevel.getElementByPositionIndex(successorCount).getGLView().getID();
							parCoordsOnStackAndHaveSucessor = true;
							break;
						}
					}
				}
				if (count < (stackLevel.getCapacity() - 1)) {
					for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if ((stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
							&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(
								reverseCount).getGLView().getID() == heatMapID))) {
							parCoordsSuccessorID =
								stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							parCoordsOnStackAndHaveSucessor = true;
							break;
						}
					}
				}
			}
		}

		if ((heatMapPredecessorID != parCoordID) && (heatMapSuccessorID != parCoordID)) {
			minPredecessorPath = Float.MAX_VALUE;
			currentPredecessorPath = -1;
			if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor
				&& isGapParCoordsOnStackAndHeatMapOnStack) {
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f predecessorPoint = heatMapList;
					Vec3f successorPoint = heatMapList;
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						heatmapSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
			else if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor) {
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f predecessorPoint = heatMapList;
					Vec3f successorPoint = heatMapList;
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						heatmapSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
			else if (heatMapOnStackAndHasPredecessor && !isGapParCoordsOnStackAndHeatMapOnStack) {
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f predecessorPoint = heatMapList;
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
			else if (heatMapOnStackAndHasPredecessor && isGapParCoordsOnStackAndHeatMapOnStack) {
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f predecessorPoint = heatMapList;
					Vec3f distanceToPredecessorVec = null;
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null)
						distanceToPredecessorVec =
							predecessorPoint.minus(hashViewToCenterPoint.get(focusLevel
								.getElementByPositionIndex(0).getGLView().getID()));
					else
						distanceToPredecessorVec =
							predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
		}
		if ((parCoordsPredecessorID != heatMapID) && (parCoordsSuccessorID != heatMapID)) {
			minPredecessorPath = Float.MAX_VALUE;
			currentPredecessorPath = -1;
			if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor) {
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f predecessorPoint = parCoordsList;
					Vec3f successorPoint = parCoordsList;
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(parCoordsSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						parCoordsPredecessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						parCoordsSuccessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
			else if (parCoordsOnStackAndHavePredecessor && isGapParCoordsOnStackAndHeatMapOnStack) {
				minPredecessorPath = Float.MAX_VALUE;
				currentPredecessorPath = -1;
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f predecessorPoint = parCoordsList;
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						parCoordsPredecessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
			else if (parCoordsOnStackAndHavePredecessor) {
				minPredecessorPath = Float.MAX_VALUE;
				currentPredecessorPath = -1;
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f predecessorPoint = parCoordsList;
					Vec3f distanceToPredecessorVec =
						predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						parCoordsPredecessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
		}
		if (heatMapPredecessorID == parCoordID) {
			currentPredecessorPath = -1;
			minPredecessorPath = Float.MAX_VALUE;
			for (Vec3f heatMapList : heatMapCenterPoints) {
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f remoteBundlingPoint = parCoordsList;
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
					currentPredecessorPath = distanceVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						parCoordsSuccessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
			currentPredecessorPath = -1;
			minPredecessorPath = Float.MAX_VALUE;
			for (Vec3f parCoordsList : parCoordCenterPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordsList);
				Vec3f predecessorPoint = parCoordsList;
				if (hashViewToCenterPoint.get(parCoordsPredecessorID) == null)
					return null;
				Vec3f distanceToPredecessorVec =
					predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
				currentPredecessorPath = distanceToPredecessorVec.length();
				if (currentPredecessorPath < minPredecessorPath) {
					minPredecessorPath = currentPredecessorPath;
					parCoordsPredecessor =
						multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
				}
			}
			if (heatMapOnStackAndHasSucessor) {
				currentSuccessorPath = -1;
				minSuccessorPath = Float.MAX_VALUE;
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f successorPoint = heatMapList;
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						heatmapSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
		}
		if (heatMapSuccessorID == parCoordID) {
			currentSuccessorPath = -1;
			minSuccessorPath = Float.MAX_VALUE;
			for (Vec3f heatMapList : heatMapCenterPoints) {
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f remoteBundlingPoint = parCoordsList;
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
					currentSuccessorPath = distanceVec.length();
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						parCoordsPredecessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
						heatmapSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));

					}
				}
			}
			currentPredecessorPath = -1;
			minPredecessorPath = Float.MAX_VALUE;
			for (Vec3f heatMapList : heatMapCenterPoints) {
				hashViewToCenterPoint.put(heatMapID, heatMapList);
				Vec3f predecessorPoint = heatMapList;
				Vec3f distanceToPredecessorVec =
					predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
				currentPredecessorPath = distanceToPredecessorVec.length();
				if (currentPredecessorPath < minPredecessorPath) {
					minPredecessorPath = currentPredecessorPath;
					heatmapPredecessor = multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
				}
			}
			if (parCoordsOnStackAndHaveSucessor) {
				currentSuccessorPath = -1;
				minSuccessorPath = Float.MAX_VALUE;
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f successorPoint = parCoordsList;
					Vec3f distanceToSuccessorVec =
						successorPoint.minus(hashViewToCenterPoint.get(parCoordsSuccessorID));
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentSuccessorPath < minSuccessorPath) {
						minSuccessorPath = currentSuccessorPath;
						parCoordsSuccessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
		}

		if (heatMapOnStackAndHasPredecessor)
			pointsList.add(heatmapPredecessor);
		else if (heatMapOnStackAndHasSucessor)
			pointsList.add(heatmapSuccessor);
		if (parCoordsOnStackAndHavePredecessor)
			pointsList.add(parCoordsPredecessor);
		else
			pointsList.add(parCoordsSuccessor);

		return pointsList;
	}

	private ArrayList<ArrayList<ArrayList<Vec3f>>> getOptimalMultipleParCoordPointsCenter(
		HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleHeatMapPoints, ArrayList<Vec3f> heatMapCenterPoints2,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleParCoordPoints, ArrayList<Vec3f> parCoordCenterPoints2) {

		ArrayList<Vec3f> heatMapCenterPoints = new ArrayList<Vec3f>();
		ArrayList<Vec3f> parCoordCenterPoints = new ArrayList<Vec3f>();
		for (int count = 0; count < multipleHeatMapPoints.size(); count++) {
			heatMapCenterPoints.add(calculateCenter(multipleHeatMapPoints.get(count)));
		}

		for (int count = 0; count < multipleParCoordPoints.size(); count++) {
			parCoordCenterPoints.add(calculateCenter(multipleParCoordPoints.get(count)));
		}

		ArrayList<ArrayList<ArrayList<Vec3f>>> pointsList = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();

		boolean isHeatMap = false;
		float minPath = Float.MAX_VALUE;
		float minPathToActiveView = Float.MAX_VALUE;
		int predecessorID = -1;
		int successorID = -1;
		int nextView = -1;
		float currentPath = -1;
		float currentPathToActiveView = -1;

		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
					isHeatMap = true;
					break;
				}
				else if (hashViewToCenterPoint.containsKey(nextView))
					break;
			}
		}
		// heatmap is first view to be visited, heatmap has no predecessor on the stack (only bundling to
		// successor needed to be calculated)
		if (isHeatMap) {
			heatMapOnStackAndHasPredecessor = true;
			for (int count = 0; count < stackLevel.getCapacity(); count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
					if (count < (stackLevel.getCapacity() - 1)) {
						for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
							if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
								if (reverseCount == (count + 2))
									isGapParCoordsCenteredAndHeatMapOnStack = true;
								successorID =
									stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
								heatMapOnStackAndHasSucessor = true;
								break;
							}
						}
					}
				}
			}
			for (Vec3f parCoordsList : parCoordCenterPoints) {
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f activeViewVec = hashViewToCenterPoint.get(activeViewID);
					Vec3f remoteBundlingPoint = heatMapList;
					Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList);
					Vec3f distanceToActiveView = activeViewVec.minus(parCoordsList);
					currentPath = distanceVec.length();
					currentPathToActiveView = distanceToActiveView.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
						parCoordsSuccessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
					if (currentPathToActiveView < minPathToActiveView) {
						minPathToActiveView = currentPathToActiveView;
						parCoordsPredecessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
			if (heatMapOnStackAndHasSucessor && !isGapParCoordsCenteredAndHeatMapOnStack) {
				minPath = Float.MAX_VALUE;
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f remoteBundlingPoint = heatMapList;
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						heatmapSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
			else if (heatMapOnStackAndHasSucessor && isGapParCoordsCenteredAndHeatMapOnStack) {
				int preID = -1;
				for (int count = stackLevel.getCapacity() - 1; count >= 0; count--) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
						preID = stackLevel.getElementByPositionIndex(count).getGLView().getID();
						break;
					}
				}
				currentPath = -1;
				minPath = Float.MAX_VALUE;
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f remoteBundlingPoint = parCoordsList;
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(preID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						heatmapSuccessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
		}
		// the first view to be connected to is not the heatmap, so heatmap maybe has a predecessor and/or
		// successor
		else {
			for (int count = 0; count < stackLevel.getCapacity() - 1; count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() != null
					&& stackLevel.getElementByPositionIndex(count).getGLView().getID() == nextView) {
					for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
							if (reverseCount == count + 2) {
								isGapParCoordsCenteredAndHeatMapOnStack = true;
							}
							break;
						}

					}
					break;
				}
			}

			for (Vec3f parCoordsList : parCoordCenterPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordsList);
				Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(nextView);
				if (remoteBundlingPoint == null)
					return null;
				Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList);
				currentPath = distanceVec.length();
				if (currentPath < minPath) {
					minPath = currentPath;
					parCoordsSuccessor =
						multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
				}
			}
			if (heatMapID != -1) {
				for (int count = 0; count < stackLevel.getCapacity(); count++) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
						if ((count < (stackLevel.getCapacity() - 1)) && count > 0) {
							for (int reverseCount = (count - 1); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							break;
						}
						else if (count == (stackLevel.getCapacity() - 1)) {
							for (int reverseCount = (stackLevel.getCapacity() - 2); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
						else if (count == 0) {
							for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
					}
				}
				if (hashViewToCenterPoint.containsKey(predecessorID)
					&& !isGapParCoordsCenteredAndHeatMapOnStack) {
					heatMapOnStackAndHasPredecessor = true;
					minPath = Float.MAX_VALUE;
					// getting optimal point to predecessor
					for (Vec3f heatMapList : heatMapCenterPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList);
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(predecessorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							heatmapPredecessor =
								multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
						}
					}
				}
				else if (hashViewToCenterPoint.containsKey(predecessorID)
					&& isGapParCoordsCenteredAndHeatMapOnStack) {
					heatMapOnStackAndHasPredecessor = true;
					currentPath = -1;
					minPath = Float.MAX_VALUE;
					for (Vec3f heatMapList : heatMapCenterPoints) {
						for (Vec3f parCoordsList : parCoordCenterPoints) {
							hashViewToCenterPoint.put(heatMapID, heatMapList);
							hashViewToCenterPoint.put(parCoordID, parCoordsList);
							Vec3f remoteBundlingPoint = parCoordsList;
							Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
							currentPath = distanceVec.length();
							if (currentPath < minPath) {
								minPath = currentPath;
								parCoordsPredecessor =
									multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
								heatmapPredecessor =
									multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
							}
						}
					}
				}
				if (hashViewToCenterPoint.containsKey(successorID)) {
					heatMapOnStackAndHasSucessor = true;
					minPath = Float.MAX_VALUE;
					// getting optimal point to successor
					for (Vec3f heatMapList : heatMapCenterPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList);
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(successorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							heatmapSuccessor =
								multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
						}
					}
				}
			}
		}
		if (heatMapOnStackAndHasPredecessor)
			pointsList.add(heatmapPredecessor);
		pointsList.add(parCoordsSuccessor);
		return pointsList;
	}

	private ArrayList<ArrayList<ArrayList<Vec3f>>> getOptimalMultipleHeatMapPointsCenter(
		HashMap<Integer, Vec3f> hashViewToCenterPoint,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleHeatMapPoints, ArrayList<Vec3f> heatMapCenterPoints2,
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleParCoordPoints, ArrayList<Vec3f> parCoordCenterPoints2) {

		ArrayList<Vec3f> heatMapCenterPoints = new ArrayList<Vec3f>();
		ArrayList<Vec3f> parCoordCenterPoints = new ArrayList<Vec3f>();
		for (int count = 0; count < multipleHeatMapPoints.size(); count++) {
			heatMapCenterPoints.add(calculateCenter(multipleHeatMapPoints.get(count)));
		}

		for (int count = 0; count < multipleParCoordPoints.size(); count++) {
			parCoordCenterPoints.add(calculateCenter(multipleParCoordPoints.get(count)));
		}

		ArrayList<ArrayList<ArrayList<Vec3f>>> pointsList = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();

		boolean areParCoords = false;
		float minPath = Float.MAX_VALUE;
		float minPathTOActiveView = Float.MAX_VALUE;
		int predecessorID = -1;
		int successorID = -1;
		int nextView = -1;
		float currentPath = -1;
		float currentPathToActiveView = -1;

		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
					areParCoords = true;
					break;
				}
				else if (hashViewToCenterPoint.containsKey(nextView))
					break;
			}
		}
		// parCoords are first view to be visited, parCoords have no predecessor on the stack (only bundling
		// to successor needed to be calculated)
		if (areParCoords) {
			parCoordsOnStackAndHavePredecessor = true;
			for (int count = 0; count < stackLevel.getCapacity(); count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
					if (count < (stackLevel.getCapacity() - 1)) {
						for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
							if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
								if (reverseCount == (count + 2))
									isGapParCoordsOnStackAndHeatMapCentered = true;
								successorID =
									stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
								parCoordsOnStackAndHaveSucessor = true;
								break;
							}
						}
					}
				}
			}
			for (Vec3f heatMapList : heatMapCenterPoints) {
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f remoteBundlingPoint = parCoordsList;
					Vec3f activeViewVec = hashViewToCenterPoint.get(activeViewID);
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
					Vec3f distanceToActiveView = activeViewVec.minus(heatMapList);
					currentPath = distanceVec.length();
					currentPathToActiveView = distanceToActiveView.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						parCoordsPredecessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
						heatmapSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
					if (currentPathToActiveView < minPathTOActiveView) {
						minPathTOActiveView = currentPathToActiveView;
						heatmapPredecessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
			if (parCoordsOnStackAndHaveSucessor && !isGapParCoordsOnStackAndHeatMapCentered) {
				minPath = Float.MAX_VALUE;
				for (Vec3f parCoordsList : parCoordCenterPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList);
					Vec3f remoteBundlingPoint = parCoordsList;
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						parCoordsSuccessor =
							multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
					}
				}
			}
			else if (parCoordsOnStackAndHaveSucessor && isGapParCoordsOnStackAndHeatMapCentered) {
				int preID = -1;
				for (int count = stackLevel.getCapacity() - 1; count >= 0; count--) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() != null) {
						preID = stackLevel.getElementByPositionIndex(count).getGLView().getID();
						break;
					}
				}
				minPath = Float.MAX_VALUE;
				currentPath = -1;
				for (Vec3f heatMapList : heatMapCenterPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList);
					Vec3f remoteBundlingPoint = heatMapList;
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(preID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						parCoordsSuccessor =
							multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
					}
				}
			}
		}
		// the first view to be connected to are not the parCoords, so parCoords maybe have a predecessor
		// and/or successor
		else {
			for (int count = 0; count < stackLevel.getCapacity() - 1; count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() != null
					&& stackLevel.getElementByPositionIndex(count).getGLView().getID() == nextView) {
					for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
						if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null) {
							if (reverseCount == count + 2) {
								isGapParCoordsOnStackAndHeatMapCentered = true;
							}
							break;
						}

					}
					break;
				}
			}

			for (Vec3f heatMapList : heatMapCenterPoints) {
				hashViewToCenterPoint.put(heatMapID, heatMapList);
				Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(nextView);
				if (remoteBundlingPoint == null)
					return null;
				Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
				currentPath = distanceVec.length();
				if (currentPath < minPath) {
					minPath = currentPath;
					heatmapSuccessor = multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
				}
			}
			if (parCoordID != -1) {
				for (int count = 0; count < stackLevel.getCapacity(); count++) {
					if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
						if ((count < (stackLevel.getCapacity() - 1)) && count > 0) {
							for (int reverseCount = (count - 1); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							for (int reverseCount = (count + 1); reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
							break;
						}
						else if (count == (stackLevel.getCapacity() - 1)) {
							for (int reverseCount = (stackLevel.getCapacity() - 2); reverseCount >= 0; reverseCount--) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
						else if (count == 0) {
							for (int reverseCount = count + 1; reverseCount < stackLevel.getCapacity(); reverseCount++) {
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID =
										stackLevel.getElementByPositionIndex(reverseCount).getGLView()
											.getID();
							}
						}
					}
				}
				if (hashViewToCenterPoint.containsKey(predecessorID)
					&& !isGapParCoordsOnStackAndHeatMapCentered) {
					parCoordsOnStackAndHavePredecessor = true;
					minPath = Float.MAX_VALUE;
					// getting optimal point to predecessor
					for (Vec3f parCoordsList : parCoordCenterPoints) {
						hashViewToCenterPoint.put(parCoordID, parCoordsList);
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(predecessorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList);
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							parCoordsPredecessor =
								multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
						}
					}
				}
				else if (hashViewToCenterPoint.containsKey(predecessorID)
					&& isGapParCoordsOnStackAndHeatMapCentered) {
					parCoordsOnStackAndHavePredecessor = true;
					currentPath = -1;
					minPath = Float.MAX_VALUE;
					for (Vec3f heatMapList : heatMapCenterPoints) {
						for (Vec3f parCoordsList : parCoordCenterPoints) {
							hashViewToCenterPoint.put(heatMapID, heatMapList);
							hashViewToCenterPoint.put(parCoordID, parCoordsList);
							Vec3f remoteBundlingPoint = parCoordsList;
							Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList);
							currentPath = distanceVec.length();
							if (currentPath < minPath) {
								minPath = currentPath;
								parCoordsPredecessor =
									multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
								heatmapPredecessor =
									multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapList));
							}
						}
					}
				}

				if (hashViewToCenterPoint.containsKey(successorID)) {
					parCoordsOnStackAndHaveSucessor = true;
					minPath = Float.MAX_VALUE;
					// getting optimal point to successor
					for (Vec3f parCoordsList : parCoordCenterPoints) {
						hashViewToCenterPoint.put(parCoordID, parCoordsList);
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(successorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList);
						currentPath = distanceVec.length();
						if (currentPath < minPath) {
							minPath = currentPath;
							parCoordsSuccessor =
								multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordsList));
						}
					}
				}
			}
		}
		pointsList.add(heatmapSuccessor);
		if (parCoordsOnStackAndHavePredecessor)
			pointsList.add(parCoordsPredecessor);
		return pointsList;
	}

	private int getNext(ArrayList<Integer> list, int iD){
		int position = list.indexOf(iD);

		if (position == list.size()-1)
			return -1;
		
		while (position < list.size()-1){
			if (list.get(position+1) != -1)
				return list.get(position+1);
			position++;
		}
		return -1;
	}

	private Integer getPreviousView(ArrayList<Integer> list, int iD){
		int position = list.indexOf(iD);
		if (position == 0)
			return -1;
		
		while(position >0){
			if (list.get(position-1) != -1)
				return list.get(position-1);
			position--;
		}
		return -1;

	}
}