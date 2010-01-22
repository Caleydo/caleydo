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
public class GLGlobalBundlingPointConnectionGraphDrawing
	extends GraphDrawingUtils {

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 */
	public GLGlobalBundlingPointConnectionGraphDrawing(final RemoteLevel focusLevel, final RemoteLevel stackLevel) {

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
		
		ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews = new ArrayList<ArrayList<ArrayList<Vec3f>>>(4);
		ArrayList<ArrayList<Vec3f>> connectionLinesActiveView = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> bundlingToCenterLinesActiveView = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> bundlingToCenterLinesOtherViews = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> connectionLinesOtherViews = new ArrayList<ArrayList<Vec3f>>();
		
		

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
//			for(Vec3f currentPoint : depthSort(pointsToDepthSort))				
//				renderLine(gl, vecViewBundlingPoint, currentPoint, 0, hashViewToCenterPoint.get(iKey), fArColor);
			
			for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {
				if(activeViewID != -1 && iKey == activeViewID)
					connectionLinesActiveView.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				else
					connectionLinesOtherViews.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
			}
			
//			renderLine(gl, vecViewBundlingPoint, vecCenter, 0, fArColor);
			ArrayList<Vec3f> bundlingToCenter = new ArrayList<Vec3f>(2);
			bundlingToCenter.add(vecViewBundlingPoint);
			bundlingToCenter.add(vecCenter);
			if(activeViewID != -1 && iKey == activeViewID) {
				bundlingToCenterLinesActiveView.add(bundlingToCenter);
			}
			else {
				bundlingToCenterLinesOtherViews.add(bundlingToCenter);
			}
			
		}
		
		connectionLinesAllViews.add(connectionLinesActiveView);
		connectionLinesAllViews.add(bundlingToCenterLinesActiveView);
		connectionLinesAllViews.add(bundlingToCenterLinesOtherViews);
		connectionLinesAllViews.add(connectionLinesOtherViews);
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}
	
	
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
			
			
			double minPath = Double.MAX_VALUE;
			ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
			ArrayList<Vec3f> optimalParCoords = new ArrayList<Vec3f>();
			Vec3f optimalHeatMapPoint = new Vec3f();
			Vec3f optimalParCoordPoint = new Vec3f();
			
			//if heatmap view exists in bucket view
			if (heatMapPoints.size() > 0){
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
						hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
						hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
						Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
					
						//TODO: Choose if global minimum or local minimum
						double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
						/*Vec3f temp = centerPoint.minus(arrayList.get(0));
						double currentPath = temp.length();*/
					
						if (currentPath < minPath){
							minPath = currentPath;
							optimalHeatMap = heatMapList;
							optimalParCoords = parCoordsList;
							optimalHeatMapPoint = heatMapList.get(0);
							optimalParCoordPoint = parCoordsList.get(0);
							vecCenter = centerPoint;
						}
					}
				}
				if ((optimalHeatMap.size() == 0) || (optimalParCoords.size() == 0))
					return null;
			}
			// no heatmap loaded in bucket view
			else{
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
				
					//TODO: Choose if global minimum or local minimum
					double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
					/*Vec3f temp = centerPoint.minus(arrayList.get(0));
					double currentPath = temp.length();*/
				
					if (currentPath < minPath){
						minPath = currentPath;
						optimalParCoords = parCoordsList;
						optimalParCoordPoint = parCoordsList.get(0);
						vecCenter = centerPoint;
					}
				}
				if (optimalParCoords.size() == 0)
					return null;
			}
			ArrayList<ArrayList<Vec3f>> tempArray = new ArrayList<ArrayList<Vec3f>>();
			if (heatMapPoints.size() > 0){
				tempArray.add(optimalHeatMap);
				hashIDTypeToViewToPointLists.get(idType).remove(heatMapID);
				hashIDTypeToViewToPointLists.get(idType).put(heatMapID, tempArray);
				hashViewToCenterPoint.put(heatMapID, optimalHeatMapPoint);
				tempArray = new ArrayList<ArrayList<Vec3f>>();
			}
			tempArray.add(optimalParCoords);
			hashIDTypeToViewToPointLists.get(idType).remove(parCoordID);
			hashIDTypeToViewToPointLists.get(idType).put(parCoordID, tempArray);
			hashViewToCenterPoint.put(parCoordID, optimalParCoordPoint);

		return hashViewToCenterPoint;
	}

	/**
	 * calculates the shortest path between the local view centers
	 * @param hashViewToCenterPoint local center points
	 * @param centerPoint global center point
	 * @return path length
	 */
	private double calculateCurrentPathLength(HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f centerPoint) {

		double length = 0;
		Set<Integer> keySet = hashViewToCenterPoint.keySet();
		
		for (Integer element : keySet) {
			Vec3f temp = centerPoint.minus(hashViewToCenterPoint.get(element));
			length += temp.length();
		}
		return length;
	}
}
