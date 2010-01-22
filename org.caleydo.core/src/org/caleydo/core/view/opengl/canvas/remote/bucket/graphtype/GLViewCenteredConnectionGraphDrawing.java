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

	return hashViewToCenterPoint;
	}

	/**
	 * calculates the shortest path between the local view centers
	 * @param hashViewToCenterPoint local center points
	 * @param centerPoint global center point
	 * @return path length
	 */
	private double calculateCurrentPathLengthHeatMapCentered(HashMap<Integer, Vec3f> hashViewToCenterPoint, Vec3f heatMapCenterPoint) {
	
		double length = 0;
		Set<Integer> keySet = hashViewToCenterPoint.keySet();
		
		for (Integer element : keySet) {
				Vec3f temp = heatMapCenterPoint.minus(hashViewToCenterPoint.get(element));
				length += temp.length();
		}
		return length;
		}
}