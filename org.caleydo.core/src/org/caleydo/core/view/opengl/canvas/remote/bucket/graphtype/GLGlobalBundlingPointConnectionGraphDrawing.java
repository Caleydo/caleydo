package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GraphDrawingUtils;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.vislink.VisLinkAnimationStage;
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
		
		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(4);
		
		VisLinkAnimationStage connectionLinesActiveView = new VisLinkAnimationStage();
		VisLinkAnimationStage bundlingToCenterLinesActiveView = new VisLinkAnimationStage();
		VisLinkAnimationStage bundlingToCenterLinesOtherViews = new VisLinkAnimationStage(true);
		VisLinkAnimationStage connectionLinesOtherViews = new VisLinkAnimationStage(true);
		
		

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
					connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				else
					connectionLinesOtherViews.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
			}
			
//			renderLine(gl, vecViewBundlingPoint, vecCenter, 0, fArColor);
			ArrayList<Vec3f> bundlingToCenter = new ArrayList<Vec3f>(2);
			bundlingToCenter.add(vecViewBundlingPoint);
			bundlingToCenter.add(vecCenter);
			if(activeViewID != -1 && iKey == activeViewID) {
				bundlingToCenterLinesActiveView.addLine(bundlingToCenter);
			}
			else {
				bundlingToCenterLinesOtherViews.addLine(bundlingToCenter);
			}
			
		}
		
		connectionLinesAllViews.add(connectionLinesActiveView);
		connectionLinesAllViews.add(bundlingToCenterLinesActiveView);
		connectionLinesAllViews.add(bundlingToCenterLinesOtherViews);
		connectionLinesAllViews.add(connectionLinesOtherViews);
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
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
					
						double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
					
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
				
					double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
				
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
			
				double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
			
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
					
						double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
					
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
				
					double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
				
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
			
				double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
			
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
