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
 * @author Michael Wittmayer
 */

public class GLTheRingGraphDrawing
	extends GraphDrawingUtils {

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	
	private ArrayList<Vec3f> connectionPoints = new ArrayList<Vec3f>(); 
	
	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 */
	public GLTheRingGraphDrawing(final RemoteLevel focusLevel, final RemoteLevel stackLevel) {

		super(focusLevel, stackLevel);

		this.focusLevel = focusLevel;
		this.stackLevel = stackLevel;
		connectionPoints.add(new Vec3f(0, 2, 0));
		connectionPoints.add(new Vec3f(0, -2, 0));
		connectionPoints.add(new Vec3f(2, 0, 0));
		connectionPoints.add(new Vec3f(-2, 0, 0));
	}

	protected void renderLineBundling(final GL gl, EIDType idType, float[] fArColor) {
		
		renderTheRing(gl);

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
			float currentPath = -1;
			float minPath = Float.MAX_VALUE;
			for (Vec3f connectionPoint : connectionPoints) {
				Vec3f distance = connectionPoint.minus(hashViewToCenterPoint.get(iKey));
				currentPath = distance.length();
				if (currentPath < minPath){
					minPath = currentPath;
					vecCenter = connectionPoint;
				}
			}
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
					connectionLinesActiveView.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				else
					connectionLinesOtherViews.addLine( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
			}
	
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
	
	
	private void renderTheRing(GL gl) {
		ArrayList<VisLinkAnimationStage> theRing = new ArrayList<VisLinkAnimationStage>();
		VisLinkAnimationStage upperLine = new VisLinkAnimationStage();
		VisLinkAnimationStage lowerLine = new VisLinkAnimationStage();
		VisLinkAnimationStage leftLine = new VisLinkAnimationStage();
		VisLinkAnimationStage rightLine = new VisLinkAnimationStage();
		
		upperLine.addLine(createControlPoints(new Vec3f(-2, 2, 0), new Vec3f(2, 2, 0), new Vec3f(0, 2, 0)));
		leftLine.addLine(createControlPoints(new Vec3f(-2, 2, 0), new Vec3f(-2, -2, 0), new Vec3f(-2, 0, 0)));
		lowerLine.addLine(createControlPoints(new Vec3f(-2, -2, 0), new Vec3f(2, -2, 0), new Vec3f(0, -2, 0)));
		rightLine.addLine(createControlPoints(new Vec3f(2, 2, 0), new Vec3f(2, -2, 0), new Vec3f(2, 0, 0)));
		theRing.add(upperLine);
		theRing.add(leftLine);
		theRing.add(lowerLine);
		theRing.add(rightLine);
		VisLinkScene theRingScene = new VisLinkScene(theRing);
		theRingScene.setAnimation(false);
		theRingScene.renderLines(gl);
		
	}

	protected ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		

		ArrayList<ArrayList<Vec3f>> optimalHeatMap = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> optimalParCoords = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<ArrayList<Vec3f>>> pointsList = new ArrayList<ArrayList<ArrayList<Vec3f>>>();

		ArrayList<Vec3f> currentPoint = new ArrayList<Vec3f>();
		int numOfHeatMapPoints = heatMapPoints.size()/6;
		int numOfParCoordPoints = parCoordsPoints.size()/3;
		
		if (heatMapPoints.size() > 0 && parCoordsPoints.size() > 0){
			for (int count = 0; count < numOfHeatMapPoints; count++){
				ArrayList<ArrayList<Vec3f>> centerPointList = new ArrayList<ArrayList<Vec3f>>();
				currentPoint = new ArrayList<Vec3f>();
				for (int pointCount = 0; pointCount < 6; pointCount++)
					centerPointList.add(heatMapPoints.get(6*count + pointCount));
				Vec3f currentVec = calculateCenter(centerPointList);
				currentPoint.add(currentVec);
				optimalHeatMap.add(currentPoint);
				
			}
			pointsList.add(optimalHeatMap);
			
			for (int count = 0; count < numOfParCoordPoints; count++){
				currentPoint = new ArrayList<Vec3f>();
				ArrayList<ArrayList<Vec3f>> centerPointList = new ArrayList<ArrayList<Vec3f>>();
				for (int pointCount  = 0; pointCount  < 3; pointCount ++)
					centerPointList.add(parCoordsPoints.get(3*count + pointCount));
				Vec3f currentVec = calculateCenter(centerPointList);
				currentPoint.add(currentVec);
				optimalParCoords.add(currentPoint);
			}
			pointsList.add(optimalParCoords);
		}
		else if (heatMapPoints.size() == 0 && parCoordsPoints.size()>0){
			for (int count = 0; count < numOfParCoordPoints; count++){
				currentPoint = new ArrayList<Vec3f>();
				ArrayList<ArrayList<Vec3f>> centerPointList = new ArrayList<ArrayList<Vec3f>>();
				for (int pointCount  = 0; pointCount  < 3; pointCount ++)
					centerPointList.add(parCoordsPoints.get(3*count + pointCount));
				Vec3f currentVec = calculateCenter(centerPointList);
				currentPoint.add(currentVec);
				optimalParCoords.add(currentPoint);
			}
			pointsList.add(optimalParCoords);
		}
		else if (heatMapPoints.size() > 0 && parCoordsPoints.size()==0){
			for (int count = 0; count < numOfHeatMapPoints; count++){
				ArrayList<ArrayList<Vec3f>> centerPointList = new ArrayList<ArrayList<Vec3f>>();
				currentPoint = new ArrayList<Vec3f>();
				for (int pointCount = 0; pointCount < 6; pointCount++)
					centerPointList.add(heatMapPoints.get(6*count + pointCount));
				Vec3f currentVec = calculateCenter(centerPointList);
				currentPoint.add(currentVec);
				optimalHeatMap.add(currentPoint);
				
			}
			pointsList.add(optimalHeatMap);
			pointsList.add(new ArrayList<ArrayList<Vec3f>>());
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
		
		ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalParCoords = new ArrayList<Vec3f>();
		ArrayList<ArrayList<Vec3f>> pointsList = new ArrayList<ArrayList<Vec3f>>();
		
		if (heatMapPoints.size() > 0 && parCoordsPoints.size() > 0){
			optimalHeatMap.add(calculateCenter(heatMapPoints));
			pointsList.add(optimalHeatMap);
			optimalParCoords.add(calculateCenter(parCoordsPoints));
			pointsList.add(optimalParCoords);
		}
		else if (heatMapPoints.size() > 0 && parCoordsPoints.size() == 0){
			optimalHeatMap.add(calculateCenter(heatMapPoints));
			pointsList.add(optimalHeatMap);
			pointsList.add(new ArrayList<Vec3f>());
		}
		else if (heatMapPoints.size() == 0 && parCoordsPoints.size() > 0){
			optimalParCoords.add(calculateCenter(parCoordsPoints));
			pointsList.add(optimalParCoords);
		}
		return pointsList;
	}
}
