package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GraphDrawingUtils;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLViewCenteredConnectionGraphDrawing
	extends GraphDrawingUtils {

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;

	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 */
	public GLViewCenteredConnectionGraphDrawing(final RemoteLevel focusLevel, final RemoteLevel stackLevel) {

		super(focusLevel, stackLevel);

		this.focusLevel = focusLevel;
		this.stackLevel = stackLevel;
	}

	protected void renderLineBundling(final GL gl, EIDType idType, float[] fArColor) {
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		
		hashViewToCenterPoint = getOptimalDynamicPoints(idType);
		if (hashViewToCenterPoint == null)
			return;
		
		Vec3f activeViewBundlingPoint = new Vec3f();
		vecCenter = calculateCenter(hashViewToCenterPoint.values());
		ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews = new ArrayList<ArrayList<ArrayList<Vec3f>>>(4);
		ArrayList<ArrayList<Vec3f>> connectionLinesActiveView = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> bundlingToCenterLinesActiveView = new ArrayList<ArrayList<Vec3f>>();
//		ArrayList<ArrayList<Vec3f>> bundlingToCenterLinesOtherViews = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> connectionLinesOtherViews = new ArrayList<ArrayList<Vec3f>>();
		if(activeViewID > 0)
			activeViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(activeViewID), vecCenter);
		for (Integer iKey : keySet) {
			Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
			ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
			

			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
				if (alCurrentPoints.size() > 1) {
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				}
				else
					pointsToDepthSort.add(alCurrentPoints.get(0));
			}
	
			for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {
				if(activeViewID != -1 && iKey == activeViewID)
					connectionLinesActiveView.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				else
					connectionLinesOtherViews.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
			}
			
			//bundlingToCenterLinesOtherViews.add(createControlPoints(activeViewBundlingPoint, vecViewBundlingPoint, vecCenter));
			if((activeViewID != iKey))
				bundlingToCenterLinesActiveView.add(createControlPoints(vecViewBundlingPoint, activeViewBundlingPoint, vecCenter));

		}
		
		connectionLinesAllViews.add(connectionLinesActiveView);
		connectionLinesAllViews.add(bundlingToCenterLinesActiveView);
		//connectionLinesAllViews.add(bundlingToCenterLinesOtherViews);
		connectionLinesAllViews.add(connectionLinesOtherViews);
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	@Override
	/**
	 * selects optimal points of views which have a set of points to choose from (atm this especially concerns HeatMap and Parallel Coordinates)
	 * @param idType
	 * @return returns a {@link HashMap} that contains the local center points
	 */
	protected HashMap<Integer, Vec3f> getOptimalDynamicPoints(EIDType idType) {
		
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		ArrayList<ArrayList<Vec3f>> heatMapPoints = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> parCoordsPoints = new ArrayList<ArrayList<Vec3f>>();
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
		if ((heatMapID < 0) && (parCoordID < 0)){
			vecCenter = calculateCenter(hashViewToCenterPoint.values());
			return hashViewToCenterPoint;
		}
		
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<ArrayList<Vec3f>>> multiplePointsList = new ArrayList<ArrayList<ArrayList<Vec3f>>>();
		if ((heatMapPoints.size() < 7) && (parCoordsPoints.size() < 4)){
			pointsList = calculateOptimalSinglePoints(heatMapPoints, heatMapID, parCoordsPoints, parCoordID, hashViewToCenterPoint);
			if (pointsList == null)
				return null;
		}
		else {
			multiplePointsList = calculateOptimalMultiplePoints(heatMapPoints, heatMapID, parCoordsPoints, parCoordID, hashViewToCenterPoint);
			if (multiplePointsList == null)
				return null;
		}
				
		if (pointsList.size() >0){
			ArrayList<ArrayList<Vec3f>> tempArray = new ArrayList<ArrayList<Vec3f>>();
			if ((pointsList.size() == 2) && (pointsList.get(1).size() == 0)){
				tempArray.add(pointsList.get(0));
				hashIDTypeToViewToPointLists.get(idType).remove(heatMapID);
				hashIDTypeToViewToPointLists.get(idType).put(heatMapID, tempArray);
				hashViewToCenterPoint.put(heatMapID, pointsList.get(0).get(0));
			}
			else if (pointsList.size() == 2){
				tempArray.add(pointsList.get(0));
				hashIDTypeToViewToPointLists.get(idType).remove(heatMapID);
				hashIDTypeToViewToPointLists.get(idType).put(heatMapID, tempArray);
				hashViewToCenterPoint.put(heatMapID, pointsList.get(0).get(0));
				tempArray = new ArrayList<ArrayList<Vec3f>>();		
				tempArray.add(pointsList.get(1));
				hashIDTypeToViewToPointLists.get(idType).remove(parCoordID);
				hashIDTypeToViewToPointLists.get(idType).put(parCoordID, tempArray);
				hashViewToCenterPoint.put(parCoordID, pointsList.get(1).get(0));
			}
			else {
				tempArray.add(pointsList.get(0));
				hashIDTypeToViewToPointLists.get(idType).remove(parCoordID);
				hashIDTypeToViewToPointLists.get(idType).put(parCoordID, tempArray);
				hashViewToCenterPoint.put(parCoordID, pointsList.get(0).get(0));
				
			}
		}
		else if (multiplePointsList.size() > 0){
			if ((multiplePointsList.size() == 2) && (multiplePointsList.get(1).size() == 0)){
				hashIDTypeToViewToPointLists.get(idType).remove(heatMapID);
				hashIDTypeToViewToPointLists.get(idType).put(heatMapID, multiplePointsList.get(0));
				hashViewToCenterPoint.put(heatMapID, calculateCenter(multiplePointsList.get(0)));
			}
			else if (multiplePointsList.size() == 2){
				hashIDTypeToViewToPointLists.get(idType).remove(heatMapID);
				hashIDTypeToViewToPointLists.get(idType).put(heatMapID, multiplePointsList.get(0));
				hashViewToCenterPoint.put(heatMapID, calculateCenter(multiplePointsList.get(0)));
				hashIDTypeToViewToPointLists.get(idType).remove(parCoordID);
				hashIDTypeToViewToPointLists.get(idType).put(parCoordID, multiplePointsList.get(1));
				hashViewToCenterPoint.put(parCoordID, calculateCenter(multiplePointsList.get(1)));
			}
			else {
				hashIDTypeToViewToPointLists.get(idType).remove(parCoordID);
				hashIDTypeToViewToPointLists.get(idType).put(parCoordID,multiplePointsList.get(0));
				hashViewToCenterPoint.put(parCoordID, calculateCenter(multiplePointsList.get(0)));
				
			}				
		}

		
/*				
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		ArrayList<ArrayList<Vec3f>> heatMapPoints = new ArrayList<ArrayList<Vec3f>>();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		int heatMapID = getSpecialViewID(HEATMAP);

		for (Integer iKey : keySet) {
			if (iKey.equals(heatMapID))
				heatMapPoints = hashIDTypeToViewToPointLists.get(idType).get(iKey);
			else
				hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}
		if (heatMapID < 0)
			return hashViewToCenterPoint;
				
		double minPath = Double.MAX_VALUE;
		ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
		Vec3f optimalHeatMapPoint = new Vec3f();
		
		if (activeViewID == heatMapID){
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				Vec3f heatMapCenterPoint = heatMapList.get(0);
		
				//TODO: Choose if global minimum or local minimum
				double currentPath = calculateCurrentPathLengthHeatMapCentered(hashViewToCenterPoint, heatMapCenterPoint);
				/*Vec3f temp = centerPoint.minus(arrayList.get(0));
				double currentPath = temp.length();*/
/*					
				if (currentPath < minPath){
					minPath = currentPath;
					optimalHeatMap = heatMapList;
					optimalHeatMapPoint = heatMapList.get(0);
					vecCenter = heatMapCenterPoint;
				}
			}
		}
		else{
			Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				Vec3f heatMapCenterPoint = heatMapList.get(0);
				Vec3f temp = centerActiveView.minus(heatMapList.get(0));
				double currentPath = temp.length();
				
				if (currentPath < minPath){
					minPath = currentPath;
					optimalHeatMap = heatMapList;
					optimalHeatMapPoint = heatMapList.get(0);
					vecCenter = heatMapCenterPoint;
				}
			}
			
		}
		if (optimalHeatMap.size() == 0)
			return null;

		ArrayList<ArrayList<Vec3f>> tempArray = new ArrayList<ArrayList<Vec3f>>();
		tempArray.add(optimalHeatMap);
		hashIDTypeToViewToPointLists.get(idType).remove(heatMapID);
		hashIDTypeToViewToPointLists.get(idType).put(heatMapID, tempArray);
		hashViewToCenterPoint.put(heatMapID, optimalHeatMapPoint);
		tempArray = new ArrayList<ArrayList<Vec3f>>();
*/
	return hashViewToCenterPoint;
	}

	private ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
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
	 *  Calculates the optimal heatmap and parcoord connection point if not more than one entry is available
	 * @param heatMapPoints
	 * @param heatMapID
	 * @param parCoordsPoints
	 * @param parCoordID
	 * @param hashViewToCenterPoint
	 * @return
	 */
	private ArrayList<ArrayList<Vec3f>> calculateOptimalSinglePoints(
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
						else {
							Vec3f centerActiveView = hashViewToCenterPoint.get(activeViewID);
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