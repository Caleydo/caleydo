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

public class GLConsecutiveConnectionGraphDrawing
	extends GraphDrawingUtils {
	
	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;

	HashMap<Integer, Vec3f> bundlingPoints = new HashMap<Integer, Vec3f>();
	Vec3f vecCenter = new Vec3f();
	HashMap<Integer, ArrayList<ArrayList<Vec3f>>> connections = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();

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
		connections.clear();
		bundlingPoints.clear();
		
		for (Integer iKey : keySet)
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
	
		vecCenter = calculateCenter(hashViewToCenterPoint.values());
		ArrayList<ArrayList<Vec3f>> connectionLinesCurrentView = new ArrayList<ArrayList<Vec3f>>();

		for (Integer iKey : keySet) {
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
				if (iKey == activeViewID)
					connectionLinesCurrentView.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				else
					connectionLinesCurrentView.add( createControlPoints( currentPoint, vecViewBundlingPoint, hashViewToCenterPoint.get(iKey) ) );
			}
			connections.put(iKey, connectionLinesCurrentView);
		}
		if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
			if (focusLevel.getElementByPositionIndex(0).getGLView().getID() == activeViewID)
				renderFromCenter(gl);
		}
		else {
			for (int stackElement = 0; stackElement < stackLevel.getCapacity(); stackElement++) {
				if (stackLevel.getElementByPositionIndex(stackElement).getGLView() != null){
					if(activeViewID == stackLevel.getElementByPositionIndex(stackElement).getGLView().getID()){
						renderFromStackLevel(gl);
						break;
					}
				}
			}
		}
	}
	
	private void renderFromCenter(GL gl){

		ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews = new ArrayList<ArrayList<ArrayList<Vec3f>>>(4);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null){
				int id = stackLevel.getElementByPositionIndex(stackCount).getGLView().getID();
				if (bundlingPoints.containsKey(id))
					ids.add(id);
			}
		}
		
		if(ids.size()>1){
			ArrayList<ArrayList<Vec3f>> bundlingLine = new ArrayList<ArrayList<Vec3f>>();
			connectionLinesAllViews.add(connections.get(activeViewID));
			Vec3f src = bundlingPoints.get(activeViewID);
			for (Integer currentID : ids) {
				bundlingLine.add(createControlPoints(bundlingPoints.get(currentID), src, vecCenter));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(currentID));
				src = bundlingPoints.get(currentID);
			}			
		}
		else if (ids.size() == 1){
			int remoteId = ids.get(0);
			ArrayList<ArrayList<Vec3f>> bundlingLine = new ArrayList<ArrayList<Vec3f>>();
			bundlingLine.add(createControlPoints(bundlingPoints.get(remoteId), bundlingPoints.get(activeViewID), vecCenter));
			connectionLinesAllViews.add(connections.get(activeViewID));
			connectionLinesAllViews.add(bundlingLine);
			connectionLinesAllViews.add(connections.get(remoteId));
		}
	
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}
	
	private void renderFromStackLevel(GL gl){
		ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews = new ArrayList<ArrayList<ArrayList<Vec3f>>>(4);
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
			ArrayList<ArrayList<Vec3f>> bundlingLine = new ArrayList<ArrayList<Vec3f>>();
			connectionLinesAllViews.add(connections.get(activeViewID));
			Vec3f src = bundlingPoints.get(activeViewID);
			for (Integer currentID : ids) {
				bundlingLine.add(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(currentID));
				src = bundlingPoints.get(currentID);
			}
			if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
				if (bundlingPoints.containsKey(focusLevel.getElementByPositionIndex(0).getGLView().getID())){
					Vec3f centerBundlingPoint = bundlingPoints.get(focusLevel.getElementByPositionIndex(0).getGLView().getID());
					bundlingLine.add(createControlPoints(src, centerBundlingPoint, vecCenter));
					connectionLinesAllViews.add(bundlingLine);
					connectionLinesAllViews.add(connections.get(focusLevel.getElementByPositionIndex(0).getGLView().getID()));
				}	
			}
			
		}
		else {
			if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
				int remoteId = focusLevel.getElementByPositionIndex(0).getGLView().getID();
				ArrayList<ArrayList<Vec3f>> bundlingLine = new ArrayList<ArrayList<Vec3f>>();
				bundlingLine.add(createControlPoints(bundlingPoints.get(remoteId), bundlingPoints.get(activeViewID), vecCenter));
				connectionLinesAllViews.add(connections.get(activeViewID));
				connectionLinesAllViews.add(bundlingLine);
				connectionLinesAllViews.add(connections.get(remoteId));
			}
		}	
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	@Override
	protected ArrayList<ArrayList<ArrayList<Vec3f>>> calculateOptimalMultiplePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<ArrayList<Vec3f>> calculateOptimalSinglePoints(
		ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID,
		ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID,
		HashMap<Integer, Vec3f> hashViewToCenterPoint) {
		// TODO Auto-generated method stub
		return null;
	}
}