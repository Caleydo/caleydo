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
	
	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	private boolean heatMapOnStackAndHasPredecessor = false;
	private boolean heatMapOnStackAndHasSucessor = false;

	private ArrayList<ArrayList<Vec3f>> heatmapPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> heatmapSuccessor = new ArrayList<ArrayList<Vec3f>>();
	
	Vec3f vecCenter = new Vec3f();

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
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		HashMap<Integer, VisLinkAnimationStage> connections = new HashMap<Integer, VisLinkAnimationStage>();
		HashMap<Integer, Vec3f> bundlingPoints = new HashMap<Integer, Vec3f>();
		hashViewToCenterPoint = getOptimalDynamicPoints(idType);
		if (hashViewToCenterPoint == null)
			return;

		vecCenter = calculateCenter(hashViewToCenterPoint.values());

		for (Integer iKey : keySet) {
			VisLinkAnimationStage connectionLinesCurrentView = new VisLinkAnimationStage();
			Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
			bundlingPoints.put(iKey, vecViewBundlingPoint);
			ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
			

			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
				if (alCurrentPoints.size() > 1) {
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				}
				else
					pointsToDepthSort.add(alCurrentPoints.get(0));
			}
			for(Vec3f currentPoint : depthSort(pointsToDepthSort)){
				if (iKey == activeViewID){
					connectionLinesCurrentView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				}
				else{
					connectionLinesCurrentView.setReverseLineDrawingDirection(true);
					connectionLinesCurrentView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				}
			}
			connections.put(iKey, connectionLinesCurrentView);
		}
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
			if (focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID){
				renderFromCenter(gl, connections, bundlingPoints, idType, hashViewToCenterPoint);
				return;
			}
		}
		for (int stackElement = 0; stackElement < stackLevel.getCapacity(); stackElement++) {
			if (stackLevel.getElementByPositionIndex(stackElement).getGLView() != null){
				if(stackLevel.getElementByPositionIndex(stackElement).getGLView().getID() == activeViewID){
					renderFromStackLevel(gl, connections, bundlingPoints);
					break;
				}
			}
		}
	}
	
	private void renderFromCenter(GL gl, HashMap<Integer, VisLinkAnimationStage> connections, HashMap<Integer, Vec3f> bundlingPoints, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint){
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		int heatMapID = getSpecialViewID(HEATMAP);
		int parCoordID = getSpecialViewID(PARCOORDS)
;		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null){
				int id = stackLevel.getElementByPositionIndex(stackCount).getGLView().getID();
				if (bundlingPoints.containsKey(id))
					ids.add(id);
			}
		}
		
		if(ids.size()>1){
			if ((activeViewID != parCoordID) && (activeViewID != heatMapID))
				connectionLinesAllViews.add(connections.get(activeViewID));
			Vec3f src = bundlingPoints.get(activeViewID);
			if ((activeViewID == parCoordID) || (activeViewID == heatMapID))
				src = hashViewToCenterPoint.get(activeViewID);
			for (Integer currentID : ids) {
				if (currentID == heatMapID){
					if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor){
						VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
						bundlingLineToPredecessor.addLine(createControlPoints(src, heatmapPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLineToPredecessor);
						src = heatmapSuccessor.get(0).get(0);
					}
					else if  (heatMapOnStackAndHasPredecessor ^ heatMapOnStackAndHasSucessor){
							VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
							bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
							connectionLinesAllViews.add(bundlingLine);
							connectionLinesAllViews.add(connections.get(currentID));
							src = bundlingPoints.get(currentID);
					}
				}
				else{
					if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && !(currentID == parCoordID)){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						connectionLinesAllViews.add(connections.get(currentID));
						src = bundlingPoints.get(currentID);
					}
					else{
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(src, hashViewToCenterPoint.get(currentID), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						src = bundlingPoints.get(currentID);
					}
				}
			}			
		}
		else if (ids.size() == 1){
			int remoteId = ids.get(0);
			VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage();
			bundlingLine.addLine(createControlPoints(bundlingPoints.get(remoteId), bundlingPoints.get(activeViewID), vecCenter));
			connectionLinesAllViews.add(connections.get(activeViewID));
			connectionLinesAllViews.add(bundlingLine);
			connectionLinesAllViews.add(connections.get(remoteId));
		}
	
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	private void renderFromStackLevel(GL gl, HashMap<Integer, VisLinkAnimationStage> connections, HashMap<Integer, Vec3f> bundlingPoints){
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if (stackLevel.getElementByPositionIndex(stackCount).getGLView() != null){
				int id = stackLevel.getElementByPositionIndex(stackCount).getGLView().getID();
				if (bundlingPoints.containsKey(id))
					ids.add(id);
			}
		}
		while(!ids.get(0).equals(activeViewID)){
			Integer temp = ids.get(0);
			ids.remove(0);
			ids.add(temp);
		}
		
		if (ids.size() > 1){
			connectionLinesAllViews.add(connections.get(activeViewID));
			Vec3f src = bundlingPoints.get(activeViewID);
			for (Integer currentID : ids) {
				VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
				bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID),  vecCenter));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(currentID));
				src = bundlingPoints.get(currentID);
			}
			if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
				if (bundlingPoints.containsKey(focusLevel.getElementByPositionIndex(0).getGLView().getID())){
					VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
					Vec3f centerBundlingPoint = bundlingPoints.get(focusLevel.getElementByPositionIndex(0).getGLView().getID());
					bundlingLine.addLine(createControlPoints(src, centerBundlingPoint, vecCenter));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(connections.get(focusLevel.getElementByPositionIndex(0).getGLView().getID()));
				}	
			}
			
		}
		else {
			if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
				int remoteId = focusLevel.getElementByPositionIndex(0).getGLView().getID();
				VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
				bundlingLine.addLine(createControlPoints(bundlingPoints.get(remoteId), bundlingPoints.get(activeViewID), vecCenter));
				connectionLinesAllViews.add(connections.get(activeViewID));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(remoteId));
			}
		}	
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	@Override
	/**
	 *  Calculates the optimal heatmap and parcoord connection point if not more than one entry is available
	 * @param heatMapPoints
	 * @param heatMapID
	 * @param parCoordsPoints
	 * @param parCoordID
	 * @param hashViewToCenterPoint
	 * @return
	 */
	protected ArrayList<ArrayList<Vec3f>> calculateOptimalSinglePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID, ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID, HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		boolean isHeatMapCentered = false;
		boolean areParCoordsCentered = false;
		if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLHeatMap)
			isHeatMapCentered = true;
		else if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLParallelCoordinates)
			areParCoordsCentered = true;
		ArrayList<ArrayList<Vec3f>> optimalDynamicPoints = null;
		
		if (isHeatMapCentered)
			optimalDynamicPoints = getOptimalHeatMapPointsCenter(hashViewToCenterPoint, heatMapPoints, heatMapID, parCoordsPoints, parCoordID);
		else if (areParCoordsCentered)
			optimalDynamicPoints = getOptimalParCoordPointsCenter(hashViewToCenterPoint, heatMapPoints, heatMapID, parCoordsPoints, parCoordID);
		else
			optimalDynamicPoints = getOptimalPointsStack();
		
		return optimalDynamicPoints;
	}
	
	
	private ArrayList<ArrayList<Vec3f>> getOptimalPointsStack() {
		// TODO Auto-generated method stub
		return null;
	}

	/** calculates shortest path between parcoords and one other way, if parcoords are in focus
	 * 
	 * @param hashViewToCenterPoint
	 * @param heatMapPoints
	 * @param heatMapID
	 * @param parCoordsPoints
	 * @param parCoordID
	 * @return
	 */
	private ArrayList<ArrayList<Vec3f>> getOptimalParCoordPointsCenter(HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID, ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID) {
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Vec3f> optimalHeatMapPredecessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalHeatMapSucessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoords = new ArrayList<Vec3f>();
		heatmapPredecessor.clear();
		heatmapSuccessor.clear();

		boolean isHeatMap = false;
		float minPath = Float.MAX_VALUE;
		int predecessorID = -1;
		int successorID = -1;
		int nextView = -1;
		float currentPath = -1;

		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null){
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap){
					isHeatMap = true;
					break;
				}
				else if (hashViewToCenterPoint.containsKey(nextView))
					break;
			}
		}
		//heatmap is first view to be visited, heatmap has no predecessor on the stack (only bundling to successor needed to be calculated)
		if (isHeatMap) {
			heatMapOnStackAndHasPredecessor = true;
			for (int count = 0; count < stackLevel.getCapacity(); count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
					if (count < (stackLevel.getCapacity() - 1)) {
						for (int reverseCount = (count +1); reverseCount < stackLevel.getCapacity(); reverseCount++){
							if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null){
								successorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
								heatMapOnStackAndHasSucessor = true;
								break;
							}
						}
					}
				}
			}
			for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f remoteBundlingPoint = heatMapList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalHeatMapPredecessor = heatMapList;
						optimalParCoords = parCoordsList;
					}
				}
			}
			heatmapPredecessor.add(optimalHeatMapPredecessor);
			if (heatMapOnStackAndHasSucessor){
				minPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f remoteBundlingPoint = heatMapList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						heatmapSuccessor.add(heatMapList);
						optimalHeatMapSucessor = heatMapList;
					}
				}
			}
		}
		//the first view to be connected to is not the heatmap, so heatmap maybe has a predecessor and/or successor
		else{
			for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
				Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(nextView);
				if (remoteBundlingPoint == null)
					return null;
				Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
				currentPath = distanceVec.length();
				if (currentPath < minPath){
					minPath = currentPath;
					optimalParCoords = parCoordsList;
				}
			}
			if (heatMapID != -1){
				for (int count = 0; count < stackLevel.getCapacity(); count++){
					if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap){
						if ((count < (stackLevel.getCapacity() - 1)) && count > 0) {
							for (int reverseCount = (count -1); reverseCount >= 0; reverseCount--){
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							}
							for (int reverseCount = (count +1); reverseCount < stackLevel.getCapacity(); reverseCount++){
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							}
							break;
						}
						else if (count == (stackLevel.getCapacity()-1)){
							for (int reverseCount = (stackLevel.getCapacity()-2); reverseCount >= 0; reverseCount--){
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									predecessorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							}
						}
						else if (count == 0){
							for (int reverseCount = count+1; reverseCount < stackLevel.getCapacity(); reverseCount++){
								if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null)
									successorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							}
						}	
					}
				}
				if (hashViewToCenterPoint.containsKey(predecessorID)){
					heatMapOnStackAndHasPredecessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					//getting optimal point to predecessor
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(predecessorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath){
							minPath = currentPath;
							optimalPoints = heatMapList;
							optimalHeatMapPredecessor = heatMapList;
						}
					}
					heatmapPredecessor.add(optimalPoints);
				}
				if (hashViewToCenterPoint.containsKey(successorID)){
					heatMapOnStackAndHasSucessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					//getting optimal point to successor
					for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(successorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath){
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
	
	/** calculates shortest distance between heatmap and one other view, if heatmap is in focus
	 * 
	 * @param hashViewToCenterPoint
	 * @param heatMapPoints
	 * @param heatMapID
	 * @param parCoordsPoints
	 * @param parCoordID
	 * @return
	 */

	private ArrayList<ArrayList<Vec3f>> getOptimalHeatMapPointsCenter(HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID, ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID) {
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoords = new ArrayList<Vec3f>();
		boolean areParCoords = false;
		float minPath = Float.MAX_VALUE;
		int nextView = -1;
		float currentPath = -1;
		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null){
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates)
					areParCoords = true;
			}
		}
		if (!areParCoords){
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
				Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(nextView);
				Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
				currentPath = distanceVec.length();
				if (currentPath < minPath){
					minPath = currentPath;
					optimalHeatMap = heatMapList;
				}
			}
		}
		else{
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
					currentPath = distanceVec.length();
					if (currentPath < minPath){
						minPath = currentPath;
						optimalHeatMap = heatMapList;
						optimalParCoords = parCoordsList;
					}
				}
			}	
		}
		pointsList.add(optimalHeatMap);
		pointsList.add(optimalParCoords);
		return pointsList;
	}

	protected ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		
		
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleHeatMapPoints = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		ArrayList<ArrayList<ArrayList<Vec3f>>> multipleParCoordPoints = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		for (int count = 0; count < 6; count++)
			multipleHeatMapPoints.add(new ArrayList<ArrayList<Vec3f>>());
		for (int count = 0; count < 3; count++)
			multipleParCoordPoints.add(new ArrayList<ArrayList<Vec3f>>());
		
		ArrayList<Vec3f> heatMapCenterPoints = new ArrayList<Vec3f>();
		ArrayList<Vec3f> parCoordCenterPoints = new ArrayList<Vec3f>();
		
		for (int pointCount = 0; pointCount < heatMapPoints.size(); pointCount++)
			multipleHeatMapPoints.get(pointCount%6).add(heatMapPoints.get(pointCount));
		
		for (int pointCount = 0; pointCount < parCoordsPoints.size(); pointCount++)
			multipleParCoordPoints.get(pointCount%3).add(parCoordsPoints.get(pointCount));

		for (int count = 0; count < multipleHeatMapPoints.size(); count++) {
			heatMapCenterPoints.add(calculateCenter(multipleHeatMapPoints.get(count)));
		}
		
		for (int count = 0; count < multipleParCoordPoints.size(); count++) {
			parCoordCenterPoints.add(calculateCenter(multipleParCoordPoints.get(count)));
		}
		
		
		
		double minPath = Double.MAX_VALUE;
		ArrayList<ArrayList<Vec3f>> optimalHeatMap = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> optimalParCoords = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<ArrayList<Vec3f>>> pointsList = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		
		//if heatmap view exists in bucket view
		if (heatMapPoints.size() > 0){
			for (Vec3f heatMapCenterPoint : heatMapCenterPoints) {
				if (parCoordsPoints.size() > 0){
					for (Vec3f parCoordCenterPoint : parCoordCenterPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapCenterPoint);
						hashViewToCenterPoint.put(parCoordID, parCoordCenterPoint);
						Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
					
						double currentPath = Double.MAX_VALUE;
						if(heatMapID == activeViewID)
						//TODO: Choose if global minimum or local minimum
							currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, heatMapCenterPoint);
						/*Vec3f temp = centerPoint.minus(arrayList.get(0));
						double currentPath = temp.length();*/
						else if(parCoordID == activeViewID){
							currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, parCoordCenterPoint);
						}
						else{
							Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
							Vec3f heatMapTemp = centerActiveView.minus(heatMapCenterPoint);
							Vec3f parCoordTemp = centerActiveView.minus(parCoordCenterPoint);
							currentPath = heatMapTemp.length() + parCoordTemp.length();
						}
						if (currentPath < minPath){
							minPath = currentPath;
							optimalHeatMap = multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapCenterPoint));
							optimalParCoords = multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordCenterPoint));
							vecCenter = centerPoint;
						}
					}
				}
				else {
					hashViewToCenterPoint.put(heatMapID, heatMapCenterPoint);
					Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
				
					double currentPath = Double.MAX_VALUE;
					if(heatMapID == activeViewID)
					//TODO: Choose if global minimum or local minimum
						currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, heatMapCenterPoint);
					/*Vec3f temp = centerPoint.minus(arrayList.get(0));
					double currentPath = temp.length();*/
					else{
						Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
						Vec3f temp = centerActiveView.minus(heatMapCenterPoint);
						currentPath = temp.length();
					}
					if (currentPath < minPath){
						minPath = currentPath;
						optimalHeatMap = multipleHeatMapPoints.get(heatMapCenterPoints.indexOf(heatMapCenterPoint));
						vecCenter = centerPoint;
					}
					
				}
			}

			pointsList.add(optimalHeatMap);
			pointsList.add(optimalParCoords);
		}
		// no heatmap loaded in bucket view
		else{
			for (Vec3f parCoordCenterPoint : parCoordCenterPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordCenterPoint);
				Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
			
				double currentPath = Double.MAX_VALUE;
				if(parCoordID == activeViewID)
				//TODO: Choose if global minimum or local minimum
					currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, parCoordCenterPoint);
				/*Vec3f temp = centerPoint.minus(arrayList.get(0));
				double currentPath = temp.length();*/
				else{
					Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
					Vec3f temp = centerActiveView.minus(parCoordCenterPoint);
					currentPath = temp.length();
				}
				if (currentPath < minPath){
					minPath = currentPath;
					optimalParCoords = multipleParCoordPoints.get(parCoordCenterPoints.indexOf(parCoordCenterPoint));
					vecCenter = centerPoint;
				}
			}
			if (optimalParCoords.size() == 0)
				return null;
			pointsList.add(optimalParCoords);
		}
		return pointsList;
	}
	
	/**
	 * calculates the shortest path between the local view centers
	 * @param hashViewToCenterPoint local center points
	 * @param centerPoint global center point
	 * @return path length
	 */
	private double calculateCurrentPathLengthDynamicPointCentered(HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f dynamicCenterPoint) {
	
		double length = 0;
		Set<Integer> keySet = hashViewToCenterPoint.keySet();
		
		for (Integer element : keySet) {
				Vec3f temp = dynamicCenterPoint.minus(hashViewToCenterPoint.get(element));
				length += temp.length();
		}
		return length;
	}
}