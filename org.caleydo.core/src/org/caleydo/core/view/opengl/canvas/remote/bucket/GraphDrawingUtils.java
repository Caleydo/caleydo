package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.ConnectionMap;
import org.caleydo.core.manager.view.SelectedElementRepList;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class GraphDrawingUtils
	extends AGLConnectionLineRenderer {

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;

	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 */
	public GraphDrawingUtils(final RemoteLevel focusLevel, final RemoteLevel stackLevel) {

		super();

		this.focusLevel = focusLevel;
		this.stackLevel = stackLevel;
	}

	@Override
	protected void renderConnectionLines(final GL gl) {
		IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();

		for (Entry<EIDType, ConnectionMap> typeConnections : connectedElementRepManager
			.getTransformedConnectionsByType().entrySet()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;

			EIDType idType = typeConnections.getKey();
			HashMap<Integer, ArrayList<ArrayList<Vec3f>>> viewToPointList =
				hashIDTypeToViewToPointLists.get(idType);

			if (viewToPointList == null) {
				viewToPointList = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
				hashIDTypeToViewToPointLists.put(idType, viewToPointList);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections.getValue().entrySet()) {
				for (SelectedElementRep selectedElementRep : connections.getValue()) {

					if (selectedElementRep.getIDType() != idType)
						throw new IllegalStateException(
							"Current ID Type does not match the selected elemen rep's");

					AGLEventListener glView =
						viewGLCanvasManager.getGLEventListener(selectedElementRep.getSourceViewID());

					if (glView == null) {
						// TODO: investigate! view must not be null here.
						// GeneralManager.get().getLogger().log(Level.WARNING,
						// "View in connection line manager is null!");
						continue;
					}

					RemoteLevelElement remoteLevelElement = glView.getRemoteLevelElement();
					if (remoteLevelElement == null) {
						// ignore views that are not rendered remote
						continue;
					}

					RemoteLevel activeLevel = remoteLevelElement.getRemoteLevel();

					if (activeLevel == stackLevel || activeLevel == focusLevel) {
						int viewID = selectedElementRep.getSourceViewID();

						alPointLists = hashIDTypeToViewToPointLists.get(idType).get(viewID);
						if (alPointLists == null) {
							alPointLists = new ArrayList<ArrayList<Vec3f>>();
							viewToPointList.put(viewID, alPointLists);
						}

						alPointLists.add(selectedElementRep.getPoints());
					}
				}
			}
			if (viewToPointList.size() > 1) {
				renderLineBundling(gl, idType, new float[] { 0, 0, 0 });
				hashIDTypeToViewToPointLists.clear();
			}
		}
	}

	protected abstract void renderLineBundling(final GL gl, EIDType idType, float[] fArColor);
//	protected void renderLineBundling(final GL gl, EIDType idType, float[] fArColor) {
//		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
//		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
//
//		for (Integer iKey : keySet) {
//			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType)
//				.get(iKey)));
//		}
//
//		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());
//
//		for (Integer iKey : keySet) {
//			Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
//
//			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(idType).get(iKey)) {
//				if (alCurrentPoints.size() > 1) {
//					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
//				}
//				else {
//					renderLine(gl, vecViewBundlingPoint, alCurrentPoints.get(0), 0, hashViewToCenterPoint
//						.get(iKey), fArColor);
//				}
//			}
//
//			renderLine(gl, vecViewBundlingPoint, vecCenter, 0, fArColor);
//		}
//	}
	
	protected ArrayList<Vec3f> createControlPoints(Vec3f vecSrcPoint, Vec3f vecDstPoint, Vec3f vecViewCenterPoint) {
		ArrayList<Vec3f> controlPoints = new ArrayList<Vec3f>(3);
		controlPoints.add(vecDstPoint);
		controlPoints.add(calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint));
		controlPoints.add(vecSrcPoint);
		return controlPoints;
	}

	protected Vec3f calculateBundlingPoint(Vec3f vecViewCenter, Vec3f vecCenter) {
		Vec3f vecDirection = new Vec3f();
		vecDirection = vecCenter.minus(vecViewCenter);
		float fLength = vecDirection.length();
		vecDirection.normalize();

		Vec3f vecViewBundlingPoint = new Vec3f();
		// Vec3f vecDestBundingPoint = new Vec3f();

		vecViewBundlingPoint = vecViewCenter.copy();
		vecDirection.scale(fLength / 1.5f);
		vecViewBundlingPoint.add(vecDirection);
		return vecViewBundlingPoint;
	}

	protected void renderPlanes(final GL gl, final Vec3f vecPoint, final ArrayList<Vec3f> alPoints) {

		gl.glColor4f(0.3f, 0.3f, 0.3f, 1f);// 0.6f);
		gl.glLineWidth(2 + 4);
		gl.glBegin(GL.GL_LINES);
		for (Vec3f vecCurrent : alPoints) {
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z() - 0.001f);
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z() - 0.001f);
		}
		// gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		// gl.glVertex3f(alPoints.get(0).x(), alPoints.get(0).y(),
		// alPoints.get(0).z());
		//		
		// gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		// gl.glVertex3f(alPoints.get(alPoints.size()-1).x(),
		// alPoints.get(alPoints.size()-1).y(), alPoints.get(0).z());
		gl.glEnd();

		// gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR_1, 0);

		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);

		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINES);
		for (Vec3f vecCurrent : alPoints) {
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		// gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		// gl.glVertex3f(alPoints.get(0).x(), alPoints.get(0).y(),
		// alPoints.get(0).z());
		//		
		// gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		// gl.glVertex3f(alPoints.get(alPoints.size()-1).x(),
		// alPoints.get(alPoints.size()-1).y(), alPoints.get(0).z());

		gl.glEnd();

		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_COLOR, 0);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		for (Vec3f vecCurrent : alPoints) {

			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
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
	 */
//	private void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
//		final int iNumberOfLines, Vec3f vecViewCenterPoint, float[] fArColor) {
//		Vec3f[] arSplinePoints = new Vec3f[3];
//
//		arSplinePoints[0] = vecSrcPoint.copy();
//		arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint);
//		arSplinePoints[2] = vecDestPoint.copy();
//
//		FloatBuffer splinePoints = FloatBuffer.allocate(8 * 3);
//		// float[] fArPoints =
//		// {1,2,-1,0,1,2,2,0,0,3,3,1,2,3,-2,1,3,1,1,3,0,2,-1,-1};
//		float[] fArPoints =
//			{ arSplinePoints[0].x(), arSplinePoints[0].y(), arSplinePoints[0].z(), arSplinePoints[1].x(),
//					arSplinePoints[1].y(), arSplinePoints[1].z(), arSplinePoints[2].x(),
//					arSplinePoints[2].y(), arSplinePoints[2].z() };
//		splinePoints.put(fArPoints);
//		splinePoints.rewind();
//
//		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, splinePoints);
//
//		// Line shadow
//		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
//		// gl.glColor4f(28/255f, 122/255f, 254/255f, 1f);
//		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2);
//		gl.glBegin(GL.GL_LINE_STRIP);
//		for (int i = 0; i <= 10; i++) {
//			gl.glEvalCoord1f((float) i / 10);
//		}
//		gl.glEnd();
//
//		// gl.glColor4fv(fArColor, 0);
//		// Point to mask artefacts
//		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
//		// gl.glColor4f(254/255f, 160/255f, 28/255f, 1f);
//
//		gl.glPointSize(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH - 0.5f);
//		gl.glBegin(GL.GL_POINTS);
//		for (int i = 0; i <= 10; i++) {
//			gl.glEvalCoord1f((float) i / 10);
//		}
//		gl.glEnd();
//
//		// The spline
//		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
//
//		gl.glBegin(GL.GL_LINE_STRIP);
//		for (int i = 0; i <= 10; i++) {
//			gl.glEvalCoord1f((float) i / 10);
//		}
//		gl.glEnd();
//	}

	// private void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f
	// vecDestPoint,
	// final int iNumberOfLines, Vec3f vecViewCenterPoint)
	// {
	// Vec3f[] arSplinePoints = new Vec3f[3];
	//		
	// // Vec3f vecDirection = new Vec3f();
	// // vecDirection = vecCenter.minus(vecViewCenter);
	// // float fLength = vecDirection.length();
	// // vecDirection.normalize();
	// //
	// // Vec3f vecViewBundlingPoint2 = new Vec3f();
	// // // Vec3f vecDestBundingPoint = new Vec3f();
	// //
	// // vecViewBundlingPoint = vecViewCenter.copy();
	// // vecDirection.scale(fLength / 3);
	// // vecViewBundlingPoint.add(vecDirection);
	//		
	// arSplinePoints[0] = vecSrcPoint.copy();
	// arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint,
	// vecViewCenterPoint);
	// arSplinePoints[2] = vecDestPoint.copy();
	//		
	// // FIXME: Do not create spline in every render frame
	// Spline3D spline = new Spline3D(arSplinePoints, 0.001f, 0.01f);
	//		
	// // // Line shadow
	// // gl.glColor4f(0.3f, 0.3f, 0.3f, 1);// , 0.6f);
	// // gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH +
	// iNumberOfLines + 4);
	// // gl.glBegin(GL.GL_LINES);
	// // gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() -
	// 0.001f);
	// // gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() -
	// 0.001f);
	// // gl.glEnd();
	//
	// gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
	// gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH +
	// iNumberOfLines);
	// gl.glBegin(GL.GL_LINES);
	//
	// for (int i=0; i<(arSplinePoints.length-1)*10; i++)
	// {
	// Vec3f vec = spline.getPositionAt((float)i / 10);
	// gl.glVertex3f(vec.x(), vec.y(), vec.z());
	// vec = spline.getPositionAt(((float)i+1) / 10);
	// gl.glVertex3f(vec.x(), vec.y(), vec.z());
	// }
	// // gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
	// // gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
	//
	// gl.glEnd();
	// }

	protected Vec3f calculateCenter(ArrayList<ArrayList<Vec3f>> alPointLists) {
		Vec3f vecCenterPoint = new Vec3f(0, 0, 0);

		int iCount = 0;
		for (ArrayList<Vec3f> currentList : alPointLists) {
			for (Vec3f vecCurrent : currentList) {
				vecCenterPoint.add(vecCurrent);
				iCount++;
			}

		}
		vecCenterPoint.scale(1.0f / iCount);
		return vecCenterPoint;
	}

	protected Vec3f calculateCenter(Collection<Vec3f> pointCollection) {

		Vec3f vecCenterPoint = new Vec3f(0, 0, 0);

		int iCount = 0;

		for (Vec3f vecCurrent : pointCollection) {
			vecCenterPoint.add(vecCurrent);
			iCount++;
		}

		vecCenterPoint.scale(1.0f / iCount);
		return vecCenterPoint;
	}

}