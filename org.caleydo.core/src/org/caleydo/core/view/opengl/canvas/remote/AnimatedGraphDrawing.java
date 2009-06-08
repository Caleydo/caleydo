package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketGraphDrawingAdapter;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;



public class AnimatedGraphDrawing
	extends BucketGraphDrawingAdapter {

	public static int firstValue = -1;
	public static int secondValue = -1;
	public static int thirdValue = -1;
	public static int forthValue = -1;
	public static int fifthValue = -1;
	
	public AnimatedGraphDrawing(RemoteLevel underInteractionLayer, RemoteLevel stackLayer,
		RemoteLevel poolLayer) {
		super(underInteractionLayer, stackLayer, poolLayer);
	}

	@Override
	/**
	 * Connection graph drawing routine. First view (to which mouse points to) is drawn from view to bundling point.
	 * Then connection to next view is drawn and next views' elements are drawn from bundling point to points in view, etc.
	 */
	protected void renderLineBundling(GL gl,EIDType idType, float[] arColor) {
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		HashMap<Integer, Vec3f> localBundlingPoints = new HashMap<Integer, Vec3f>();
		
		for (Integer iKey : keySet) {
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}
		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());
		for (Integer iKey : keySet) {
			localBundlingPoints.put(iKey, calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter));
		}
		//animate first view
		if (activeViewID != -1){
			ArrayList<ArrayList<Vec3f>> points = hashIDTypeToViewToPointLists.get(idType).get(activeViewID);
			if (points == null)
				return;
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(activeViewID), points.get(i));
				else
					renderLine(gl, points.get(i).get(0),localBundlingPoints.get(activeViewID), 0, hashViewToCenterPoint.get(activeViewID), arColor, firstValue);
			}
			if (firstValue == 10){
				renderLine(gl, localBundlingPoints.get(activeViewID), vecCenter, 0, arColor);
				hashIDTypeToViewToPointLists.get(idType).remove(activeViewID);
				hashViewToCenterPoint.remove(activeViewID);
				localBundlingPoints.remove(activeViewID);
			}
		}	
		keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		int nrOfViews = keySet.size();
		if (nrOfViews == 1){
			Integer firstID = (Integer)keySet.toArray()[0];
			renderLine(gl, vecCenter, localBundlingPoints.get(firstID),  0, arColor);
			ArrayList<ArrayList<Vec3f>> points = hashIDTypeToViewToPointLists.get(idType).get(firstID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(firstID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(firstID),points.get(i).get(0), 0, hashViewToCenterPoint.get(firstID), arColor, secondValue);
			}
		}
		else if (nrOfViews == 2){
			Integer firstID = (Integer)keySet.toArray()[0];
			Integer secondID = (Integer)keySet.toArray()[1];
			renderLine(gl, vecCenter, localBundlingPoints.get(firstID),  0, arColor);
			ArrayList<ArrayList<Vec3f>> points = hashIDTypeToViewToPointLists.get(idType).get(firstID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(firstID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(firstID),points.get(i).get(0), 0, hashViewToCenterPoint.get(firstID), arColor, secondValue);
			}
			if (secondValue == 10)
				renderLine(gl, vecCenter, localBundlingPoints.get(secondID),  0, arColor);
			points = hashIDTypeToViewToPointLists.get(idType).get(secondID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(secondID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(secondID),points.get(i).get(0), 0, hashViewToCenterPoint.get(secondID), arColor, thirdValue);
			}
		}
		else if (nrOfViews == 3){
			Integer firstID = (Integer)keySet.toArray()[0];
			Integer secondID = (Integer)keySet.toArray()[1];
			Integer thirdID = (Integer)keySet.toArray()[2];
			renderLine(gl, vecCenter, localBundlingPoints.get(firstID),  0, arColor);
			ArrayList<ArrayList<Vec3f>> points = hashIDTypeToViewToPointLists.get(idType).get(firstID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(firstID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(firstID),points.get(i).get(0), 0, hashViewToCenterPoint.get(firstID), arColor, secondValue);
			}
			if (secondValue == 10)
				renderLine(gl, vecCenter, localBundlingPoints.get(secondID),  0, arColor);
			points = hashIDTypeToViewToPointLists.get(idType).get(secondID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(secondID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(secondID),points.get(i).get(0), 0, hashViewToCenterPoint.get(secondID), arColor, thirdValue);
			}
			if (thirdValue == 10)
				renderLine(gl, vecCenter, localBundlingPoints.get(thirdID),  0, arColor);
			points = hashIDTypeToViewToPointLists.get(idType).get(thirdID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(thirdID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(thirdID),points.get(i).get(0), 0, hashViewToCenterPoint.get(thirdID), arColor, forthValue);
			}
		}
		else if (nrOfViews == 4){
			Integer firstID = (Integer)keySet.toArray()[0];
			Integer secondID = (Integer)keySet.toArray()[1];
			Integer thirdID = (Integer)keySet.toArray()[2];
			Integer forthID = (Integer)keySet.toArray()[3];
			renderLine(gl, vecCenter, localBundlingPoints.get(firstID),  0, arColor);
			ArrayList<ArrayList<Vec3f>> points = hashIDTypeToViewToPointLists.get(idType).get(firstID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(firstID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(firstID),points.get(i).get(0), 0, hashViewToCenterPoint.get(firstID), arColor, secondValue);
			}
			if (secondValue == 10)
				renderLine(gl, vecCenter, localBundlingPoints.get(secondID),  0, arColor);
			points = hashIDTypeToViewToPointLists.get(idType).get(secondID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(secondID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(secondID),points.get(i).get(0), 0, hashViewToCenterPoint.get(secondID), arColor, thirdValue);
			}
			if (thirdValue == 10)
				renderLine(gl, vecCenter, localBundlingPoints.get(thirdID),  0, arColor);
			points = hashIDTypeToViewToPointLists.get(idType).get(thirdID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(thirdID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(thirdID),points.get(i).get(0), 0, hashViewToCenterPoint.get(thirdID), arColor, forthValue);
			}
			if (forthValue == 10)
				renderLine(gl, vecCenter, localBundlingPoints.get(forthID),  0, arColor);
			points = hashIDTypeToViewToPointLists.get(idType).get(forthID);
			for (int i = 0; i<points.size(); i++){
				if (points.get(0).size()>1)
					renderPlanes(gl, localBundlingPoints.get(forthID), points.get(i));
				else
					renderLine(gl, localBundlingPoints.get(forthID),points.get(i).get(0), 0, hashViewToCenterPoint.get(forthID), arColor, fifthValue);
			}

		}

	}
	
	/**
	 * Render straight connection lines.
	 * 
	 * @param gl
	 * @param vecSrcPoint
	 * @param vecDestPoint
	 * @param iNumberOfLines
	 * @param fArColor
	 */
	protected void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
		final int iNumberOfLines, float[] fArColor) {
		
		gl.glColor4f(187/255f, 0/255f, 255/255f, 1f);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
		gl.glEnd();
	}
	
		/**
	 * Render curved connection lines.
	 * 
	 * @param gl
	 * @param vecSrcPoint
	 * @param vecDestPoint
	 * @param iNumberOfLines
	 * @param vecViewCenterPoint
	 * @param fArColor
	 * @param length length of curve to be drawn
	 */
	protected void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint, 
		final int iNumberOfLines, Vec3f vecViewCenterPoint, float[] fArColor, int length)
	{
		Vec3f[] arSplinePoints = new Vec3f[3];

		arSplinePoints[0] = vecSrcPoint.copy();
		arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint);
		arSplinePoints[2] = vecDestPoint.copy();

		FloatBuffer splinePoints = FloatBuffer.allocate(8 * 3);
		// float[] fArPoints =
		// {1,2,-1,0,1,2,2,0,0,3,3,1,2,3,-2,1,3,1,1,3,0,2,-1,-1};
		float[] fArPoints =
			{ arSplinePoints[0].x(), arSplinePoints[0].y(), arSplinePoints[0].z(), arSplinePoints[1].x(),
					arSplinePoints[1].y(), arSplinePoints[1].z(), arSplinePoints[2].x(),
					arSplinePoints[2].y(), arSplinePoints[2].z() };
		splinePoints.put(fArPoints);
		splinePoints.rewind();

		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, splinePoints);

		gl.glColor4f(187/255f, 0/255f, 255/255f, 1f);
		
		// The spline
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i <= length; i++) {
			gl.glEvalCoord1f((float) i / 10);
		}
		gl.glEnd();
		
		//diamond on top of the graph
		if ((length < 10) && (length >= 0)){
			gl.glEnable(GL.GL_POINT_SMOOTH);
			
			gl.glPointSize(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH +6);
			gl.glColor4f(187/255f, 0/255f, 255/255f, 0.3f);

			gl.glBegin(GL.GL_POINTS);
				gl.glEvalCoord1f((float) length / 10);
			gl.glEnd();
			
			gl.glPointSize(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH +3);
			gl.glColor4f(187/255f, 0/255f, 255/255f, 1f);

			gl.glBegin(GL.GL_POINTS);
				gl.glEvalCoord1f((float) length / 10);
			gl.glEnd();
			gl.glDisable(GL.GL_POINT_SMOOTH);
		}
	
	}
	
	/**
	 * Sets ID of view in which the mouse cursor is currently positioned
	 * @param viewID ID of current active View
	 */
	public void setActiveViewID(int viewID){
		activeViewID = viewID;
	}
	
	/**
	 * Setter methods for setting the length values for animated graph
	 * @param val
	 */	
	public static void setFirstViewValue(int val){
		firstValue = val;
	}
	public static void setSecondViewValue(int val){
		secondValue = val;
	}
	public static void setThirdViewValue(int val){
		thirdValue = val;
	}
	public static void setForthViewValue(int val){
		forthValue = val;
	}
	public static void setFifthViewValue(int val){
		fifthValue = val;
	}
	
}
