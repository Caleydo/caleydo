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
		
		hashViewToCenterPoint = getOptimalHeatMapPoint(idType);

		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());
		
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
	 * selects the point of the heatmap point set that corresponds to the shortest path
	 * @param idType
	 * @return returns a {@link HashMap} that contains the local center points
	 */
	
	private HashMap<Integer, Vec3f> getOptimalHeatMapPoint(EIDType idType) {
			
			Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
			ArrayList<ArrayList<Vec3f>> heatMapPoints = new ArrayList<ArrayList<Vec3f>>();
			HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
			int heatMapID = findHeatMapID();

			for (Integer iKey : keySet) {
				if (iKey.equals(heatMapID))
					heatMapPoints = hashIDTypeToViewToPointLists.get(idType).get(iKey);
				else
					hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
			}

			double minPath = Double.MAX_VALUE;
			ArrayList<Vec3f> heatMap = new ArrayList<Vec3f>();
			Vec3f optimalPoint = new Vec3f();
			for (ArrayList<Vec3f> arrayList : heatMapPoints) {
				hashViewToCenterPoint.put(heatMapID, arrayList.get(0));
				Vec3f centerPoint = calculateCenter(hashViewToCenterPoint.values());
				double currentPath = calculateCurrentPathLength(hashViewToCenterPoint, centerPoint);
				if (currentPath < minPath){
					minPath = currentPath;
					heatMap = arrayList;
					optimalPoint = arrayList.get(0);
				}
			}
			ArrayList<ArrayList<Vec3f>> temp = new ArrayList<ArrayList<Vec3f>>();
			temp.add(heatMap);
			hashIDTypeToViewToPointLists.get(idType).put(heatMapID, temp);
			hashViewToCenterPoint.put(heatMapID, optimalPoint);

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

	/** Helper method to find the id of a possibly existing heatmap
	 * 
	 * @return returns the id of the heatmap if one has been found
	 */
	private int findHeatMapID(){
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
			if (focusLevel.getElementByPositionIndex(0).getGLView() instanceof GLHeatMap)
				return focusLevel.getElementByPositionIndex(0).getGLView().getID();
		}
		else
			for (int stack = 0; stack < stackLevel.getCapacity(); stack++) {
				if (stackLevel.getElementByPositionIndex(stack).getGLView() != null){
					if (stackLevel.getElementByPositionIndex(stack).getGLView() instanceof GLHeatMap)
						return stackLevel.getElementByPositionIndex(stack).getGLView().getID();
				}
			}
		return -1;
	}
}