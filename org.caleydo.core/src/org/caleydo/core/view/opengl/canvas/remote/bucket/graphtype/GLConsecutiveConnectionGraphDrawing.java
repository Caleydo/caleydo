package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GraphDrawingUtils;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.vislink.VisLinkAnimationStage;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */

public class GLConsecutiveConnectionGraphDrawing
	extends GraphDrawingUtils {
	
	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;

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
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		HashMap<Integer, VisLinkAnimationStage> connections = new HashMap<Integer, VisLinkAnimationStage>();
		HashMap<Integer, Vec3f> bundlingPoints = new HashMap<Integer, Vec3f>();
	//	hashViewToCenterPoint = getOptimalDynamicPoints(idType);
		if (hashViewToCenterPoint == null)
			return;
		
		for (Integer iKey : keySet)
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
	
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
		Integer heatMapID = getSpecialViewID(HEATMAP);
		boolean isHeatMap = false;
		if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLHeatMap)
			isHeatMap = true;
		
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();

		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null){
				int id = stackLevel.getElementByPositionIndex(stackCount).getGLView().getID();
				if (bundlingPoints.containsKey(id))
					ids.add(id);
			}
		}
		
		if(ids.size()>1){
			connectionLinesAllViews.add(connections.get(activeViewID));
			Vec3f src = bundlingPoints.get(activeViewID);
			for (Integer currentID : ids) {
				VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
				bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(currentID));
				src = bundlingPoints.get(currentID);
			}			
		}
		else if (ids.size() == 1){
			int remoteId = ids.get(0);
			VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage();
			if (!isHeatMap){
				bundlingLine.addLine(createControlPoints(bundlingPoints.get(remoteId), bundlingPoints.get(activeViewID), vecCenter));
				connectionLinesAllViews.add(connections.get(activeViewID));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(remoteId));
			}
			else{
				bundlingLine.addLine(createControlPoints(bundlingPoints.get(remoteId), bundlingPoints.get(activeViewID), vecCenter));
				VisLinkAnimationStage tempLine =  bestheatMapPoint(bundlingPoints, remoteId, idType, hashViewToCenterPoint);
				connectionLinesAllViews.add(tempLine);
				//connectionLinesAllViews.add(connections.get(activeViewID));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(remoteId));

			}
		}
	
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	private VisLinkAnimationStage bestheatMapPoint(HashMap<Integer, Vec3f> bundlingPoints, int remoteID, EIDType idType, HashMap<Integer, Vec3f> hashViewToCenterPoint) {

		VisLinkAnimationStage bestHeatMapLine = new VisLinkAnimationStage();
		float minPath = Float.MAX_VALUE;
		Vec3f optimalPoint = new Vec3f();
		for (int count = 0; count < hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size(); count++){
			Vec3f temp = hashIDTypeToViewToPointLists.get(idType).get(activeViewID).get(count).get(0);
			Vec3f path = temp.minus(bundlingPoints.get(remoteID)); 
			if (path.length() < minPath){
				minPath = path.length();
				optimalPoint = temp; 
			}
		}
		bestHeatMapLine.addLine(createControlPoints(optimalPoint, bundlingPoints.get(activeViewID), hashIDTypeToViewToPointLists.get(idType).get(activeViewID).get(0).get(0)));
		return bestHeatMapLine;
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
		
		double minPath = Double.MAX_VALUE;
		ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoords = new ArrayList<Vec3f>();
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		
		//if heatmap view exists in bucket view
		if (heatMapPoints.size() > 0){
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				if (parCoordsPoints.size() > 0){
					for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
						hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
						Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
					
						double currentPath = Double.MAX_VALUE;
						if(heatMapID == activeViewID)
							currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, heatMapList.get(0));
						else if (parCoordID == activeViewID)
							currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, parCoordsList.get(0));
						else{
							Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
							if (centerActiveView == null)
								return null;
							Vec3f heatMapTemp = centerActiveView.minus(heatMapList.get(0));
							Vec3f parCoordTemp = centerActiveView.minus(parCoordsList.get(0));
							currentPath = heatMapTemp.length() + parCoordTemp.length();
						}
						if (currentPath < minPath){
							minPath = currentPath;
							optimalHeatMap = heatMapList;
							optimalParCoords = parCoordsList;
							vecCenter = centerPoint;
						}
					}
				}
				else {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
					double currentPath = Double.MAX_VALUE;
					if(heatMapID == activeViewID)
					//TODO: Choose if global minimum or local minimum
						currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, heatMapList.get(0));
					/*Vec3f temp = centerPoint.minus(arrayList.get(0));
					double currentPath = temp.length();*/
					else{
						Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
						Vec3f temp = centerActiveView.minus(heatMapList.get(0));
						currentPath = temp.length();
					}
					if (currentPath < minPath){
						minPath = currentPath;
						optimalHeatMap = heatMapList;
						vecCenter = centerPoint;
					}		
				}
			}

			pointsList.add(optimalHeatMap);
			pointsList.add(optimalParCoords);
		}
		// no heatmap loaded in bucket view
		else{
			for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
				hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
				Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());

				double currentPath = Double.MAX_VALUE;
				if(parCoordID == activeViewID)
				//TODO: Choose if global minimum or local minimum
					currentPath = calculateCurrentPathLengthDynamicPointCentered(hashViewToCenterPoint, parCoordsList.get(0));
				/*Vec3f temp = centerPoint.minus(arrayList.get(0));
				double currentPath = temp.length();*/
				else{
					Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
					Vec3f temp = centerActiveView.minus(parCoordsList.get(0));
					currentPath = temp.length();
				}
				if (currentPath < minPath){
					minPath = currentPath;
					optimalParCoords = parCoordsList;
					vecCenter = centerPoint;
				}

			}
			if (optimalParCoords.size() == 0)
				return null;
			pointsList.add(optimalParCoords);
		}
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