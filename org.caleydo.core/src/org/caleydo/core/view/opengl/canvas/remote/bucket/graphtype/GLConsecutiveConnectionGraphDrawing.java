package org.caleydo.core.view.opengl.canvas.remote.bucket.graphtype;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketGraphDrawingAdapter;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

/**
 * Specialized connection line renderer drawing consecutive graphs to bucket view.
 * Connection lines between bundling points are always drawn in the following order:
 * if mouse position is in stackLevel: connect stackLevel elements counterclockwise and then connect to focusLevel
 * if mouse position is in focusLevel: connect focusLevel to stackLevel and then counterclockwise through stackLevel
 */
public class GLConsecutiveConnectionGraphDrawing
	extends BucketGraphDrawingAdapter {

	private final static int BUCKET_TOP = 0;
	private final static int BUCKET_LEFT = 1;
	private final static int BUCKET_BOTTOM = 2;
	private final static int BUCKET_RIGHT = 3;
	
	private HashMap<Integer, Vec3f> bundlingPoints = new HashMap<Integer, Vec3f>();
	private Vec3f vecCenter;
	
	/**
	 * Constructor.
	 * 
	 * @param underInteractionLevel
	 * @param stackLevel
	 * @param poolLevel
	 */
	public GLConsecutiveConnectionGraphDrawing(final RemoteLevel focusLevel, final RemoteLevel stackLevel,
		final RemoteLevel poolLevel) {
		super(focusLevel, stackLevel, poolLevel);
	}


	protected void renderLineBundling(GL gl, EIDType idType, float[] arColor) {
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		for (Integer iKey : keySet) {
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}

		vecCenter = calculateCenter(hashViewToCenterPoint.values());

		for (Integer iKey : keySet) {
			Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
			bundlingPoints.put(iKey, vecViewBundlingPoint);
			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
				if (alCurrentPoints.size() > 1) {
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				}
				else {
					renderLine(gl, vecViewBundlingPoint, alCurrentPoints.get(0), 0, hashViewToCenterPoint
						.get(iKey), arColor);
				}
			}
		}
		if (activeViewID == focusLevel.getElementByPositionIndex(0).getContainedElementID()){
			renderFromCenter(gl, arColor);
		}
		else{
			for (int position = 0; position <stackLevel.getCapacity();position++){
				if (activeViewID == stackLevel.getElementByPositionIndex(position).getContainedElementID()){
					renderFromAmbience(gl, arColor, position);
					break;
				}
			}

		}
	}
	
	private void renderFromCenter(final GL gl, float[] arColor){
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int views = 0; views < stackLevel.getCapacity(); views++){
			int id = stackLevel.getElementByPositionIndex(views).getContainedElementID();
			if (bundlingPoints.containsKey(id)){
				ids.add(id);
			}
		}
		if (ids.size()>1){
			Iterator<Integer> idCounter = ids.iterator();
			int src = idCounter.next();
			int connectionToFocusLevel = src;
			while(idCounter.hasNext()){
				int dst = idCounter.next();
				renderLine(gl, bundlingPoints.get(src), bundlingPoints.get(dst), 0, vecCenter, arColor);
				src = dst;
			}
			if(bundlingPoints.containsKey(activeViewID)){
				renderLine(gl, bundlingPoints.get(activeViewID), bundlingPoints.get(connectionToFocusLevel), 0, vecCenter, arColor);
			}
		}
		else{
			if(bundlingPoints.containsKey(activeViewID)){
				renderLine(gl, bundlingPoints.get(activeViewID), bundlingPoints.get(ids.get(0)), 0, vecCenter, arColor);
			}
		}
		bundlingPoints.clear();
	}
	
	
	
	private void renderFromAmbience(final GL gl, float[] arColor, int activeView){
		
		switch (activeView){
			case BUCKET_TOP:
				renderFromTop(gl, arColor);
				break;
			case BUCKET_LEFT:
				renderFromLeft(gl, arColor);
				break;
			case BUCKET_BOTTOM:
				renderFromBottom(gl, arColor);
				break;
			case BUCKET_RIGHT:
				renderFromRight(gl, arColor);
				break;
		}
	}
	
	private void renderFromTop(final GL gl, float[] arColor){
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int views = 0; views < stackLevel.getCapacity(); views++){
			int id = stackLevel.getElementByPositionIndex(views).getContainedElementID();
			if (bundlingPoints.containsKey(id)){
				ids.add(id);
			}
		}
		
		if (ids.size()>1){
			Iterator<Integer> idCounter = ids.iterator();
			int src = idCounter.next();
			int dst = 0;
			while(idCounter.hasNext()){
				dst = idCounter.next();
				renderLine(gl, bundlingPoints.get(src), bundlingPoints.get(dst), 0, vecCenter, arColor);
				src = dst;
			}
			int centerID = focusLevel.getElementByPositionIndex(0).getContainedElementID();
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(dst), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		else{
			int centerID = focusLevel.getElementByPositionIndex(0).getContainedElementID();
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(ids.get(0)), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		bundlingPoints.clear();
	}
	
	private void renderFromLeft(final GL gl, float[] arColor){
		
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for (int views = 0; views < stackLevel.getCapacity(); views++){
			int id = stackLevel.getElementByPositionIndex(views).getContainedElementID();
			if (bundlingPoints.containsKey(id)){
				ids.add(id);
			}
		}
		
		if (!ids.getFirst().equals(activeViewID)){
			Integer temp = ids.getFirst();
			ids.removeFirst();
			ids.addLast(temp);
		}
		
		if (ids.size() > 1){
			Iterator<Integer> idCounter = ids.iterator();
			int src = idCounter.next();
			int dst = 0;
			while(idCounter.hasNext()){
				dst = idCounter.next();
				renderLine(gl, bundlingPoints.get(src), bundlingPoints.get(dst), 0, vecCenter, arColor);
				src = dst;
			}
			int centerID = focusLevel.getElementByPositionIndex(0).getContainedElementID();
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(dst), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		else{
			int centerID = focusLevel.getElementByPositionIndex(0).getContainedElementID();
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(ids.getFirst()), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		bundlingPoints.clear();
	}

	private void renderFromBottom(final GL gl, float[] arColor){
		LinkedList<Integer> ids = new LinkedList<Integer>();
		int centerID = -1;
		for (int views = 0; views < stackLevel.getCapacity(); views++){
			int id = stackLevel.getElementByPositionIndex(views).getContainedElementID();
			if (bundlingPoints.containsKey(id)){
				ids.add(id);
			}
		}
		while(!ids.getFirst().equals(activeViewID)){
			Integer temp = ids.getFirst();
			ids.removeFirst();
			ids.addLast(temp);
		}
		centerID = focusLevel.getElementByPositionIndex(0).getContainedElementID();
		if (ids.size() > 1){
			Iterator<Integer> idCounter = ids.iterator();
			int src = idCounter.next();
			int dst = 0;
			while(idCounter.hasNext()){
				dst = idCounter.next();
				renderLine(gl, bundlingPoints.get(src), bundlingPoints.get(dst), 0, vecCenter, arColor);
				src = dst;
			}
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(dst), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		else{
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(ids.getFirst()), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		bundlingPoints.clear();
	}
	
	private void renderFromRight(final GL gl, float[] arColor){
		
		LinkedList<Integer> ids = new LinkedList<Integer>();
		int centerID = -1;
		for (int views = 0; views < stackLevel.getCapacity(); views++){
			int id = stackLevel.getElementByPositionIndex(views).getContainedElementID();
			if (bundlingPoints.containsKey(id)){
				ids.add(id);
			}
		}
		Integer temp = ids.getLast();
		ids.removeLast();
		ids.addFirst(temp);
		centerID = focusLevel.getElementByPositionIndex(0).getContainedElementID();

		if (ids.size()>1){
			Iterator<Integer> idCounter = ids.iterator();
			int src = idCounter.next();
			int dst = 0;
			while (idCounter.hasNext()){
				dst = idCounter.next();
				renderLine(gl, bundlingPoints.get(src), bundlingPoints.get(dst), 0, vecCenter, arColor);
				src = dst;
			}
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(dst), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		else{
			if (bundlingPoints.get(centerID) != null){
				renderLine(gl, bundlingPoints.get(ids.getFirst()), bundlingPoints.get(centerID), 0, vecCenter, arColor);
			}
		}
		bundlingPoints.clear();		
	}
}