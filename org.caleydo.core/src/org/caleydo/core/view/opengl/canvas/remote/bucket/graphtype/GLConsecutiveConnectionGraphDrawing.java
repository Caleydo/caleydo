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
	private boolean parCoordsOnStackAndHavePredecessor = false;
	private boolean parCoordsOnStackAndHaveSucessor = false;
	private boolean isGapParCoordsOnStackAndHeatMapOnStack = false;
	private boolean isGapParCoordsCenteredAndHeatMapOnStack = false;
	private boolean isGapParCoordsOnStackAndHeatMapCentered = false;


	private ArrayList<ArrayList<Vec3f>> heatmapPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> heatmapSuccessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsPredecessor = new ArrayList<ArrayList<Vec3f>>();
	private ArrayList<ArrayList<Vec3f>> parCoordsSuccessor = new ArrayList<ArrayList<Vec3f>>();
	
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
		parCoordsOnStackAndHavePredecessor = false;
		parCoordsOnStackAndHaveSucessor = false;
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
		int parCoordID = getSpecialViewID(PARCOORDS);
		for (int stackCount = 0; stackCount < stackLevel.getCapacity(); stackCount++) {
			if(stackLevel.getElementByPositionIndex(stackCount).getGLView() != null){
				int id = stackLevel.getElementByPositionIndex(stackCount).getGLView().getID();
				if (bundlingPoints.containsKey(id))
					ids.add(id);
			}
		}
		
		if(ids.size()>1){
			if ((activeViewID != parCoordID) && (activeViewID != heatMapID)){
				if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1)
					connectionLinesAllViews.add(connections.get(activeViewID));
			}
			Vec3f src = new Vec3f();
			if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size()>1)
				src = bundlingPoints.get(activeViewID);
			else
				src = hashViewToCenterPoint.get(activeViewID);
			if ((activeViewID == parCoordID) || (activeViewID == heatMapID))
				src = hashViewToCenterPoint.get(activeViewID);
			for (Integer currentID : ids) {
				if (currentID == heatMapID){
					//vecCenter = calculateControlPoint(heatmapPredecessor.get(0).get(0), src);
					if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor && isGapParCoordsOnStackAndHeatMapOnStack){
						if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1){
							VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
							bundlingLineToPredecessor.addLine(createControlPoints(bundlingPoints.get(activeViewID), heatmapPredecessor.get(0).get(0), vecCenter));
							connectionLinesAllViews.add(bundlingLineToPredecessor);
							src = heatmapSuccessor.get(0).get(0);
						}
						else{
							VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
							bundlingLineToPredecessor.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID), heatmapPredecessor.get(0).get(0), vecCenter));
							connectionLinesAllViews.add(bundlingLineToPredecessor);
							src = heatmapSuccessor.get(0).get(0);
						}
					}
					else if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor){
						VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
						bundlingLineToPredecessor.addLine(createControlPoints(src, heatmapPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLineToPredecessor);
						src = heatmapSuccessor.get(0).get(0);
					}
					
					else if (heatMapOnStackAndHasPredecessor && (isGapParCoordsCenteredAndHeatMapOnStack == true)){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(parCoordsPredecessor.get(0).get(0), heatmapPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
					}
					else if (heatMapOnStackAndHasPredecessor && (isGapParCoordsOnStackAndHeatMapOnStack == true)){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID), heatmapPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
					}
					else if (heatMapOnStackAndHasPredecessor){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(src, heatmapPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
					}
				}
				else if (currentID == parCoordID){
					//vecCenter = calculateControlPoint(parCoordsPredecessor.get(0).get(0), src);
					if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor && isGapParCoordsOnStackAndHeatMapOnStack){
						if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() >1){
							VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
							bundlingLineToPredecessor.addLine(createControlPoints(bundlingPoints.get(activeViewID), parCoordsPredecessor.get(0).get(0), vecCenter));
							connectionLinesAllViews.add(bundlingLineToPredecessor);
							src = parCoordsSuccessor.get(0).get(0);
						}
						else{
							VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
							bundlingLineToPredecessor.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID), parCoordsPredecessor.get(0).get(0), vecCenter));
							connectionLinesAllViews.add(bundlingLineToPredecessor);
							src = parCoordsSuccessor.get(0).get(0);
						}
					}
					else if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor){
						VisLinkAnimationStage bundlingLineToPredecessor = new VisLinkAnimationStage(true);
						bundlingLineToPredecessor.addLine(createControlPoints(src, parCoordsPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLineToPredecessor);
						src = parCoordsSuccessor.get(0).get(0);
					}
					else if (parCoordsOnStackAndHavePredecessor && isGapParCoordsOnStackAndHeatMapCentered){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(heatmapPredecessor.get(0).get(0), parCoordsPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
					}
					else if (parCoordsOnStackAndHavePredecessor && (isGapParCoordsOnStackAndHeatMapOnStack == true)){
						if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() > 1){
							VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
							bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID), parCoordsPredecessor.get(0).get(0), vecCenter));
							connectionLinesAllViews.add(bundlingLine);	
						}
						else {
							VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
							bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID), parCoordsPredecessor.get(0).get(0), vecCenter));
							connectionLinesAllViews.add(bundlingLine);
						}
					}
					else if (parCoordsOnStackAndHavePredecessor){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(src, parCoordsPredecessor.get(0).get(0), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
					}
				}
				else{
					//vecCenter = calculateControlPoint(bundlingPoints.get(currentID), src);
					if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && (currentID != parCoordID)){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						connectionLinesAllViews.add(connections.get(currentID));
						src = bundlingPoints.get(currentID);
					}
					else if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size()>1 && (currentID != heatMapID)){
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
						connectionLinesAllViews.add(bundlingLine);
						connectionLinesAllViews.add(connections.get(currentID));
						src = bundlingPoints.get(currentID);
					}
					else{
						VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
						if (hashIDTypeToViewToPointLists.get(idType).get(currentID).size() > 1){
							bundlingLine.addLine(createControlPoints(src, bundlingPoints.get(currentID), vecCenter));
							connectionLinesAllViews.add(bundlingLine);
							src = bundlingPoints.get(currentID);
						}
						else
							bundlingLine.addLine(createControlPoints(src, hashViewToCenterPoint.get(currentID), vecCenter));
							connectionLinesAllViews.add(bundlingLine);
							src = hashViewToCenterPoint.get(currentID);
					}

				}
			}			
		}
		else if (ids.size() == 1){
			int remoteId = ids.get(0);
			VisLinkAnimationStage bundlingLine = new VisLinkAnimationStage(true);
			if (activeViewID == heatMapID){
				if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size()>1){
					vecCenter = calculateControlPoint(heatmapSuccessor.get(0).get(0), hashViewToCenterPoint.get(remoteId));
					bundlingLine.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), bundlingPoints.get(remoteId), vecCenter));
				}
				else{
					vecCenter = calculateControlPoint(heatmapSuccessor.get(0).get(0), hashViewToCenterPoint.get(remoteId));
					bundlingLine.addLine(createControlPoints(heatmapSuccessor.get(0).get(0), hashViewToCenterPoint.get(remoteId), vecCenter));
				}
			}
			else if (activeViewID == parCoordID){
				if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size()>1){
					vecCenter = parCoordsSuccessor.get(0).get(0);
					bundlingLine.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), bundlingPoints.get(remoteId), vecCenter));
				}
				else{
					vecCenter = parCoordsSuccessor.get(0).get(0);
					bundlingLine.addLine(createControlPoints(parCoordsSuccessor.get(0).get(0), hashViewToCenterPoint.get(remoteId), vecCenter));
				}
			}
			else if (remoteId == heatMapID){
				if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size()>1){
					vecCenter = calculateControlPoint(bundlingPoints.get(activeViewID), heatmapPredecessor.get(0).get(0));
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID), heatmapPredecessor.get(0).get(0), vecCenter));
				}
				else{
					vecCenter = calculateControlPoint(hashViewToCenterPoint.get(activeViewID), heatmapPredecessor.get(0).get(0));
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID), heatmapPredecessor.get(0).get(0), vecCenter));
				}
			}
			else if (remoteId == parCoordID){
				if (hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size() >1){
					vecCenter = calculateControlPoint(bundlingPoints.get(activeViewID), parCoordsPredecessor.get(0).get(0));
					vecCenter = bundlingPoints.get(activeViewID);
					bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID), parCoordsPredecessor.get(0).get(0), vecCenter));
				}
				else{
					vecCenter = calculateControlPoint(hashViewToCenterPoint.get(activeViewID), parCoordsPredecessor.get(0).get(0));
					vecCenter = hashViewToCenterPoint.get(activeViewID);
					bundlingLine.addLine(createControlPoints(hashViewToCenterPoint.get(activeViewID), parCoordsPredecessor.get(0).get(0), vecCenter));
				}
			}
			else{
				vecCenter = calculateControlPoint(bundlingPoints.get(activeViewID), bundlingPoints.get(remoteId));
				bundlingLine.addLine(createControlPoints(bundlingPoints.get(activeViewID), bundlingPoints.get(remoteId), vecCenter));
			}
			if (activeViewID != heatMapID && activeViewID != parCoordID && hashIDTypeToViewToPointLists.get(idType).get(activeViewID).size()>1){
				connectionLinesAllViews.add(connections.get(activeViewID));
			}
			connectionLinesAllViews.add(bundlingLine);
			if (hashIDTypeToViewToPointLists.get(idType).get(remoteId).size() >1)
				connectionLinesAllViews.add(connections.get(remoteId));
			else
				connectionLinesAllViews.add(new VisLinkAnimationStage());
			
		}
	
		
		VisLinkScene visLinkScene = new VisLinkScene(connectionLinesAllViews);
		visLinkScene.renderLines(gl);
	}

	private Vec3f calculateControlPoint(Vec3f src, Vec3f dst) {
		Vec3f controlPoint = new Vec3f();
		if (src.x()<0 && src.y() <0 && dst.x() < 0 && dst.y() <0)
			controlPoint = new Vec3f(-2, -2, 0);
		else if (src.x()<0 && src.y() >0 && dst.x() < 0 && dst.y() >0)
			controlPoint = new Vec3f(-2, 2, 0);
		else if (src.x()>0 && src.y() >0 && dst.x() > 0 && dst.y() >0)
			controlPoint = new Vec3f(2, 2, 0);
		else if (src.x()>0 && src.y() <0 && dst.x() > 0 && dst.y() <0)
			controlPoint = new Vec3f(2, -2, 0);
		
		
		else if ((src.x()<0 && src.y() >0 && dst.x() < 0 && dst.y() <0) || (src.x()<0 && src.y() <0 && dst.x() < 0 && dst.y() >0))
			controlPoint = new Vec3f(-2, 0, 0);
		else if ((src.x()<0 && src.y() >0 && dst.x() > 0 && dst.y() >0) || (src.x()>0 && src.y() >0 && dst.x() < 0 && dst.y() >0))
			controlPoint = new Vec3f(0, 2, 0);
		else if ((src.x()>0 && src.y() <0 && dst.x() > 0 && dst.y() >0) || (src.x()>0 && src.y() >0 && dst.x() > 0 && dst.y() <0))
			controlPoint = new Vec3f(2, 0, 0);
		else if ((src.x()>0 && src.y() <0 && dst.x() < 0 && dst.y() <0) || (src.x()<0 && src.y() <0 && dst.x() > 0 && dst.y() <0))
			controlPoint = new Vec3f(0, -2, 0);

		else if ((src.x()<0 && src.y() >0 && dst.x() > 0 && dst.y()< 0) || (src.x()>0 && src.y() <0 && dst.x() < 0 && dst.y() >0))
			controlPoint = new Vec3f(0, 0, 0);
		else if ((src.x()<0 && src.y() <0 && dst.x() > 0 && dst.y()> 0) || (src.x()>0 && src.y() >0 && dst.x() < 0 && dst.y() <0))
			controlPoint = new Vec3f(0, 0, 0);
		
		
		return controlPoint;
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
			optimalDynamicPoints = getOptimalPointsStack(hashViewToCenterPoint, heatMapPoints, heatMapID, parCoordsPoints, parCoordID);
		
		return optimalDynamicPoints;
	}
	
	
	private ArrayList<ArrayList<Vec3f>> getOptimalPointsStack(HashMap<Integer, Vec3f> hashViewToCenterPoint, ArrayList<ArrayList<Vec3f>> heatMapPoints, int heatMapID, ArrayList<ArrayList<Vec3f>> parCoordsPoints, int parCoordID) {
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
		
		//check if there's a gap
		for (int count = 0; count < stackLevel.getCapacity()-1; count++){
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null){
				for (int reverseCount = count +1; reverseCount < stackLevel.getCapacity(); reverseCount++){
					if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null){
						if (reverseCount == count +2)
							isGapParCoordsOnStackAndHeatMapOnStack = true;
					}
				}
			}
		}
		
		
		//get id of predecessor/successor to heatmap and/or parCoords
		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLHeatMap) {
				if (count > 0){
					for (int predecessorCount = count-1; predecessorCount >= 0; predecessorCount--){
						if ((stackLevel.getElementByPositionIndex(predecessorCount).getGLView() !=null) && ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID()))|| (stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID() == parCoordID))){
							heatMapPredecessorID = stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID();
							heatMapOnStackAndHasPredecessor = true;
							break;
						}
					}
					if (heatMapOnStackAndHasPredecessor == false){
						if ((focusLevel.getElementByPositionIndex(0).getGLView() != null) && ((hashViewToCenterPoint.containsKey(focusLevel.getElementByPositionIndex(0).getGLView().getID()))|| (focusLevel.getElementByPositionIndex(0).getGLView().getID() == parCoordID))){
							heatMapPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
							heatMapOnStackAndHasPredecessor = true;
						}
					}
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
						heatMapPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						heatMapOnStackAndHasPredecessor = true;
					}
					for (int successorCount = 1; successorCount < stackLevel.getCapacity(); successorCount++){
						if ((stackLevel.getElementByPositionIndex(successorCount).getGLView() !=null) && ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(successorCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(successorCount).getGLView().getID() == parCoordID))){
							heatMapSuccessorID = stackLevel.getElementByPositionIndex(successorCount).getGLView().getID();
							heatMapOnStackAndHasSucessor = true;
							break;
						}						
					}
				}
				if (count < (stackLevel.getCapacity() - 1)) {
					for (int reverseCount = (count +1); reverseCount < stackLevel.getCapacity(); reverseCount++){
						if ((stackLevel.getElementByPositionIndex(reverseCount).getGLView() !=null) && ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID() == parCoordID))){
							heatMapSuccessorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							heatMapOnStackAndHasSucessor = true;
							break;
						}
					}
				}
			}
			else if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
				if (count > 0){
					for (int predecessorCount = count-1; predecessorCount >= 0; predecessorCount--){
						if ((stackLevel.getElementByPositionIndex(predecessorCount).getGLView() !=null)&& ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID()))|| (stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID() == heatMapID))){
							parCoordsPredecessorID = stackLevel.getElementByPositionIndex(predecessorCount).getGLView().getID();
							parCoordsOnStackAndHavePredecessor = true;
							break;
						}
					}
					if (parCoordsOnStackAndHavePredecessor == false){
						if ((focusLevel.getElementByPositionIndex(0).getGLView() != null) && ((hashViewToCenterPoint.containsKey(focusLevel.getElementByPositionIndex(0).getGLView().getID()))|| (focusLevel.getElementByPositionIndex(0).getGLView().getID() == heatMapID))){
							parCoordsPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
							parCoordsOnStackAndHavePredecessor = true;
						}
					}
				}
				else {
					if (focusLevel.getElementByPositionIndex(0).getGLView() != null){
						parCoordsPredecessorID = focusLevel.getElementByPositionIndex(0).getGLView().getID();
						parCoordsOnStackAndHavePredecessor = true;
					}
					for (int successorCount = 1; successorCount < stackLevel.getCapacity(); successorCount++){
						if ((stackLevel.getElementByPositionIndex(successorCount).getGLView() !=null) && ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(successorCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(successorCount).getGLView().getID() == heatMapID))){
							parCoordsSuccessorID = stackLevel.getElementByPositionIndex(successorCount).getGLView().getID();
							parCoordsOnStackAndHaveSucessor = true;
							break;
						}						
					}
				}				
				if (count < (stackLevel.getCapacity() - 1)) {
					for (int reverseCount = (count +1); reverseCount < stackLevel.getCapacity(); reverseCount++){
						if ((stackLevel.getElementByPositionIndex(reverseCount).getGLView() !=null) && ((hashViewToCenterPoint.containsKey(stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID())) || (stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID() == heatMapID))){
							parCoordsSuccessorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
							parCoordsOnStackAndHaveSucessor = true;
							break;
						}
					}
				}
			}
		}
		
		if ((heatMapPredecessorID != parCoordID) && (heatMapSuccessorID != parCoordID)){
			minPredecessorPath = Float.MAX_VALUE;
			currentPredecessorPath = -1;
			if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor && isGapParCoordsOnStackAndHeatMapOnStack){
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f successorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					Vec3f distanceToSuccessorVec = successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
					if (currentSuccessorPath < minSuccessorPath){
						minSuccessorPath = currentSuccessorPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
			else if (heatMapOnStackAndHasPredecessor && heatMapOnStackAndHasSucessor){
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f successorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
					Vec3f distanceToSuccessorVec = successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
					if (currentSuccessorPath < minSuccessorPath){
						minSuccessorPath = currentSuccessorPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
			else if (heatMapOnStackAndHasPredecessor && !isGapParCoordsOnStackAndHeatMapOnStack){
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);
			}
			else if (heatMapOnStackAndHasPredecessor && isGapParCoordsOnStackAndHeatMapOnStack){
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f predecessorPoint = heatMapList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(focusLevel.getElementByPositionIndex(0).getGLView().getID()));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalHeatMapPredecessor = heatMapList;
					}
				}
				heatmapPredecessor.add(optimalHeatMapPredecessor);				
			}
		}
		if ((parCoordsPredecessorID != heatMapID) && (parCoordsSuccessorID != heatMapID)){
			minPredecessorPath = Float.MAX_VALUE;
			currentPredecessorPath = -1;
			if (parCoordsOnStackAndHavePredecessor && parCoordsOnStackAndHaveSucessor){
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f successorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					Vec3f distanceToSuccessorVec = successorPoint.minus(hashViewToCenterPoint.get(parCoordsSuccessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsPredecessor = parCoordsList;
					}
					if (currentSuccessorPath < minSuccessorPath){
						minSuccessorPath = currentSuccessorPath;
						optimalParCoordsSucessor = parCoordsList;
					}
				}
				parCoordsSuccessor.add(optimalParCoordsSucessor);
				parCoordsPredecessor.add(optimalParCoordsPredecessor);
			}	
			else if (parCoordsOnStackAndHavePredecessor && isGapParCoordsOnStackAndHeatMapOnStack){
				minPredecessorPath = Float.MAX_VALUE;
				currentPredecessorPath = -1;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(activeViewID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsPredecessor = parCoordsList;
					}
				}
				parCoordsPredecessor.add(optimalParCoordsPredecessor);
			}
			else if (parCoordsOnStackAndHavePredecessor){
				minPredecessorPath = Float.MAX_VALUE;
				currentPredecessorPath = -1;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f predecessorPoint = parCoordsList.get(0);
					Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
					currentPredecessorPath = distanceToPredecessorVec.length();
					if (currentPredecessorPath < minPredecessorPath) {
						minPredecessorPath = currentPredecessorPath;
						optimalParCoordsPredecessor = parCoordsList;
					}
				}
				parCoordsPredecessor.add(optimalParCoordsPredecessor);
			}
		}
		if (heatMapPredecessorID == parCoordID){
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
				Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(parCoordsPredecessorID));
				currentPredecessorPath = distanceToPredecessorVec.length();
				if (currentPredecessorPath < minPredecessorPath){
					minPredecessorPath = currentPredecessorPath;
					optimalParCoordsPredecessor = parCoordsList;
				}
			}
			parCoordsPredecessor.add(optimalParCoordsPredecessor);
			if (heatMapOnStackAndHasSucessor){
				currentSuccessorPath = -1;
				minSuccessorPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f successorPoint = heatMapList.get(0);
					Vec3f distanceToSuccessorVec = successorPoint.minus(hashViewToCenterPoint.get(heatMapSuccessorID));
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentSuccessorPath < minSuccessorPath){
						minSuccessorPath = currentSuccessorPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapSuccessor.add(optimalHeatMapSucessor);	
			}
		}
		if (heatMapSuccessorID == parCoordID){
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
				Vec3f distanceToPredecessorVec = predecessorPoint.minus(hashViewToCenterPoint.get(heatMapPredecessorID));
				currentPredecessorPath = distanceToPredecessorVec.length();
				if (currentPredecessorPath < minPredecessorPath){
					minPredecessorPath = currentPredecessorPath;
					optimalHeatMapPredecessor = heatMapList;
				}
			}
			heatmapPredecessor.add(optimalHeatMapPredecessor);
			if (parCoordsOnStackAndHaveSucessor){
				currentSuccessorPath = -1;
				minSuccessorPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f successorPoint = parCoordsList.get(0);
					Vec3f distanceToSuccessorVec = successorPoint.minus(hashViewToCenterPoint.get(parCoordsSuccessorID));
					currentSuccessorPath = distanceToSuccessorVec.length();
					if (currentSuccessorPath < minSuccessorPath){
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

	/** calculates shortest path between parcoords and another pathway, if parcoords are in focus
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
								if (reverseCount == (count + 2))
									isGapParCoordsCenteredAndHeatMapOnStack = true;
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
			parCoordsSuccessor.add(optimalParCoords);
			if (heatMapOnStackAndHasSucessor && !isGapParCoordsCenteredAndHeatMapOnStack){
				minPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					Vec3f remoteBundlingPoint = heatMapList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalHeatMapSucessor = heatMapList;
					}
				}
				heatmapSuccessor.add(optimalHeatMapSucessor);
			}
			else if (heatMapOnStackAndHasSucessor && isGapParCoordsCenteredAndHeatMapOnStack){
				int preID = -1;
				for (int count = stackLevel.getCapacity()-1; count >= 0; count--){
					if (stackLevel.getElementByPositionIndex(count).getGLView()!= null){
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
		//the first view to be connected to is not the heatmap, so heatmap maybe has a predecessor and/or successor
		else{
			for (int count = 0; count < stackLevel.getCapacity()-1; count++){
				if (stackLevel.getElementByPositionIndex(count).getGLView()!= null && stackLevel.getElementByPositionIndex(count).getGLView().getID() == nextView){
					for (int reverseCount = count+1; reverseCount < stackLevel.getCapacity(); reverseCount++){
						if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null){
							if (reverseCount == count +2){
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
				if (currentPath < minPath){
					minPath = currentPath;
					optimalParCoords = parCoordsList;
				}
			}
			parCoordsSuccessor.add(optimalParCoords);
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
				if (hashViewToCenterPoint.containsKey(predecessorID) && !isGapParCoordsCenteredAndHeatMapOnStack){
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
				else if (hashViewToCenterPoint.containsKey(predecessorID) && isGapParCoordsCenteredAndHeatMapOnStack){
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
		ArrayList<Vec3f> optimalParCoordPredecessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalparCoordSucessor = new ArrayList<Vec3f>();
		ArrayList<Vec3f> optimalHeatMap = new ArrayList<Vec3f>();
		parCoordsPredecessor.clear();
		parCoordsSuccessor.clear();

		boolean areParCoords = false;
		float minPath = Float.MAX_VALUE;
		int predecessorID = -1;
		int successorID = -1;
		int nextView = -1;
		float currentPath = -1;

		for (int count = 0; count < stackLevel.getCapacity(); count++) {
			if (stackLevel.getElementByPositionIndex(count).getGLView() != null){
				nextView = stackLevel.getElementByPositionIndex(count).getGLView().getID();
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates){
					areParCoords = true;
					break;
				}
				else if (hashViewToCenterPoint.containsKey(nextView))
					break;
			}
		}
		//parCoords are first view to be visited, parCoords have no predecessor on the stack (only bundling to successor needed to be calculated)
		if (areParCoords) {
			parCoordsOnStackAndHavePredecessor = true;
			for (int count = 0; count < stackLevel.getCapacity(); count++) {
				if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates) {
					if (count < (stackLevel.getCapacity() - 1)) {
						for (int reverseCount = (count +1); reverseCount < stackLevel.getCapacity(); reverseCount++){
							if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null){
								if (reverseCount == (count +2))
									isGapParCoordsOnStackAndHeatMapCentered = true;
								successorID = stackLevel.getElementByPositionIndex(reverseCount).getGLView().getID();
								parCoordsOnStackAndHaveSucessor = true;
								break;
							}
						}
					}
				}
			}
			for (ArrayList<Vec3f> heatMapList : heatMapPoints) {
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {					
					hashViewToCenterPoint.put(heatMapID, heatMapList.get(0));
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(heatMapList.get(0));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalParCoordPredecessor = parCoordsList;
						optimalHeatMap = heatMapList;
					}
				}
			}
			parCoordsPredecessor.add(optimalParCoordPredecessor);
			heatmapSuccessor.add(optimalHeatMap);
			if (parCoordsOnStackAndHaveSucessor && !isGapParCoordsOnStackAndHeatMapCentered){
				minPath = Float.MAX_VALUE;
				for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
					hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
					Vec3f remoteBundlingPoint = parCoordsList.get(0);
					Vec3f distanceVec = remoteBundlingPoint.minus(hashViewToCenterPoint.get(successorID));
					currentPath = distanceVec.length();
					if (currentPath < minPath) {
						minPath = currentPath;
						optimalparCoordSucessor = parCoordsList;
					}
				}
				parCoordsSuccessor.add(optimalparCoordSucessor);
			}
			else if (parCoordsOnStackAndHaveSucessor && isGapParCoordsOnStackAndHeatMapCentered){
				int preID = -1;
				for (int count = stackLevel.getCapacity()-1; count >= 0; count--){
					if (stackLevel.getElementByPositionIndex(count).getGLView()!= null){
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
		//the first view to be connected to are not the parCoords, so parCoords maybe have a predecessor and/or successor
		else{
			for (int count = 0; count < stackLevel.getCapacity()-1; count++){
				if (stackLevel.getElementByPositionIndex(count).getGLView()!= null && stackLevel.getElementByPositionIndex(count).getGLView().getID() == nextView){
					for (int reverseCount = count+1; reverseCount < stackLevel.getCapacity(); reverseCount++){
						if (stackLevel.getElementByPositionIndex(reverseCount).getGLView() != null){
							if (reverseCount == count +2){
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
				if (currentPath < minPath){
					minPath = currentPath;
					optimalHeatMap = heatMapList;
				}
			}
			heatmapSuccessor.add(optimalHeatMap);
			if (parCoordID != -1){
				for (int count = 0; count < stackLevel.getCapacity(); count++){
					if (stackLevel.getElementByPositionIndex(count).getGLView() instanceof GLParallelCoordinates){
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
				if (hashViewToCenterPoint.containsKey(predecessorID) && !isGapParCoordsOnStackAndHeatMapCentered){
					parCoordsOnStackAndHavePredecessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					//getting optimal point to predecessor
					for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
						hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(predecessorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath){
							minPath = currentPath;
							optimalPoints = parCoordsList;
							optimalParCoordPredecessor = parCoordsList;
						}
					}
					parCoordsPredecessor.add(optimalPoints);
				}
				else if (hashViewToCenterPoint.containsKey(predecessorID) && isGapParCoordsOnStackAndHeatMapCentered){
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
								optimalHeatMapPredecessor  = heatMapList;
							}
						}				
					}
					parCoordsPredecessor.add(optimalParCoordsPredecessor);
					heatmapPredecessor.add(optimalHeatMapPredecessor);	
				}
				
				if (hashViewToCenterPoint.containsKey(successorID)){
					parCoordsOnStackAndHaveSucessor = true;
					minPath = Float.MAX_VALUE;
					ArrayList<Vec3f> optimalPoints = new ArrayList<Vec3f>();
					//getting optimal point to successor
					for (ArrayList<Vec3f> parCoordsList : parCoordsPoints) {
						hashViewToCenterPoint.put(parCoordID, parCoordsList.get(0));
						Vec3f remoteBundlingPoint = hashViewToCenterPoint.get(successorID);
						Vec3f distanceVec = remoteBundlingPoint.minus(parCoordsList.get(0));
						currentPath = distanceVec.length();
						if (currentPath < minPath){
							minPath = currentPath;
							optimalPoints = parCoordsList;
							optimalparCoordSucessor = parCoordsList;
						}
					}
					parCoordsSuccessor.add(optimalPoints);
				}	
			}
		}
		pointsList.add(optimalHeatMap);
		if (parCoordsOnStackAndHavePredecessor)
			pointsList.add(optimalParCoordPredecessor);
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