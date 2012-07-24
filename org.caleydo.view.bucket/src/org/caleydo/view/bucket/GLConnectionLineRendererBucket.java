/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.bucket;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.vislink.VisLinkAnimationStage;
import org.caleydo.core.view.opengl.util.vislink.VisLinkScene;
import org.caleydo.core.view.vislink.ConnectionMap;
import org.caleydo.core.view.vislink.SelectedElementRepList;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLConnectionLineRendererBucket extends AGLConnectionLineRenderer {

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;

	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 */
	public GLConnectionLineRendererBucket(final RemoteLevel focusLevel,
			final RemoteLevel stackLevel) {

		super();

		this.focusLevel = focusLevel;
		this.stackLevel = stackLevel;
	}

	@Override
	protected void renderConnectionLines(final GL2 gl) {
		ViewManager viewGLCanvasManager = GeneralManager.get().getViewManager();

		for (Entry<IDType, ConnectionMap> typeConnections : connectedElementRepManager
				.getTransformedConnectionsByType().entrySet()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;

			IDType idType = typeConnections.getKey();
			HashMap<Integer, ArrayList<ArrayList<Vec3f>>> viewToPointList = hashIDTypeToViewToPointLists
					.get(idType);

			if (viewToPointList == null) {
				viewToPointList = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
				hashIDTypeToViewToPointLists.put(idType, viewToPointList);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections
					.getValue().entrySet()) {
				for (ElementConnectionInformation selectedElementRep : connections
						.getValue()) {

					if (selectedElementRep.getIDType() != idType)
						throw new IllegalStateException(
								"Current ID Type does not match the selected elemen rep's");

					AGLView glView = viewGLCanvasManager.getGLView(selectedElementRep
							.getSourceViewID());

					if (glView == null) {
						// TODO: investigate! view must not be null here.
						// GeneralManager.get().getLogger().log(Level.WARNING,
						// "View in connection line manager is null!");
						continue;
					}

					RemoteLevelElement remoteLevelElement = glView
							.getRemoteLevelElement();
					if (remoteLevelElement == null) {
						// ignore views that are not rendered remote
						continue;
					}

					RemoteLevel activeLevel = remoteLevelElement.getRemoteLevel();

					if (activeLevel == stackLevel || activeLevel == focusLevel) {
						int viewID = selectedElementRep.getSourceViewID();

						alPointLists = hashIDTypeToViewToPointLists.get(idType).get(
								viewID);
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

	protected void renderLineBundling(final GL2 gl, IDType idType, float[] fArColor) {
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();

		for (Integer iKey : keySet) {
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists
					.get(idType).get(iKey)));
		}

		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());

		ArrayList<VisLinkAnimationStage> connectionLinesAllViews = new ArrayList<VisLinkAnimationStage>(
				4);

		VisLinkAnimationStage connectionLinesActiveView = new VisLinkAnimationStage();
		VisLinkAnimationStage bundlingToCenterLinesActiveView = new VisLinkAnimationStage();
		VisLinkAnimationStage bundlingToCenterLinesOtherViews = new VisLinkAnimationStage(
				true);
		VisLinkAnimationStage connectionLinesOtherViews = new VisLinkAnimationStage(true);

		for (Integer iKey : keySet) {
			Vec3f vecViewBundlingPoint = calculateBundlingPoint(
					hashViewToCenterPoint.get(iKey), vecCenter);
			ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();

			for (ArrayList<Vec3f> alCurrentPoints : hashIDTypeToViewToPointLists.get(
					idType).get(iKey)) {
				if (alCurrentPoints.size() > 1) {
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				} else
					pointsToDepthSort.add(alCurrentPoints.get(0));
			}

			for (Vec3f currentPoint : depthSort(pointsToDepthSort)) {
				if (activeViewID != -1 && iKey == activeViewID)
					connectionLinesActiveView.addLine(createControlPoints(
							vecViewBundlingPoint, currentPoint,
							hashViewToCenterPoint.get(iKey)));
				else
					connectionLinesOtherViews.addLine(createControlPoints(
							vecViewBundlingPoint, currentPoint,
							hashViewToCenterPoint.get(iKey)));
			}

			ArrayList<Vec3f> bundlingToCenter = new ArrayList<Vec3f>(2);
			bundlingToCenter.add(vecViewBundlingPoint);
			bundlingToCenter.add(vecCenter);
			if (activeViewID != -1 && iKey == activeViewID) {
				bundlingToCenterLinesActiveView.addLine(bundlingToCenter);
			} else {
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

	private ArrayList<Vec3f> createControlPoints(Vec3f vecSrcPoint, Vec3f vecDstPoint,
			Vec3f vecViewCenterPoint) {
		ArrayList<Vec3f> controlPoints = new ArrayList<Vec3f>(3);
		controlPoints.add(vecDstPoint);
		controlPoints.add(calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint));
		controlPoints.add(vecSrcPoint);
		return controlPoints;
	}

	private Vec3f calculateBundlingPoint(Vec3f vecViewCenter, Vec3f vecCenter) {
		Vec3f vecDirection = new Vec3f();
		vecDirection = vecCenter.minus(vecViewCenter);
		float fLength = vecDirection.length();
		vecDirection.normalize();

		Vec3f vecViewBundlingPoint = new Vec3f();
		// Vec3f vecDestBundingPoint = new Vec3f();

		vecViewBundlingPoint = vecViewCenter.copy();
		vecDirection.scale(fLength / 2f);
		vecViewBundlingPoint.add(vecDirection);
		return vecViewBundlingPoint;
	}

	private void renderPlanes(final GL2 gl, final Vec3f vecPoint,
			final ArrayList<Vec3f> alPoints) {

		gl.glColor4f(0.3f, 0.3f, 0.3f, 1f);// 0.6f);
		gl.glLineWidth(2 + 4);
		gl.glBegin(GL2.GL_LINES);
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
		gl.glBegin(GL2.GL_LINES);
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
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		for (Vec3f vecCurrent : alPoints) {

			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		gl.glEnd();
	}

	// /**
	// * Render curved connection lines.
	// *
	// * @param gl
	// * @param vecSrcPoint
	// * @param vecDestPoint
	// * @param iNumberOfLines
	// * @param vecViewCenterPoint
	// * @param fArColor
	// */
	// private void renderLine(final GL2 gl, final Vec3f vecSrcPoint,
	// final Vec3f vecDestPoint, final int iNumberOfLines,
	// Vec3f vecViewCenterPoint, float[] fArColor) {
	// Vec3f[] arSplinePoints = new Vec3f[3];
	//
	// arSplinePoints[0] = vecSrcPoint.copy();
	// arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint,
	// vecViewCenterPoint);
	// arSplinePoints[2] = vecDestPoint.copy();
	//
	// FloatBuffer splinePoints = FloatBuffer.allocate(8 * 3);
	// // float[] fArPoints =
	// // {1,2,-1,0,1,2,2,0,0,3,3,1,2,3,-2,1,3,1,1,3,0,2,-1,-1};
	// float[] fArPoints = {arSplinePoints[0].x(), arSplinePoints[0].y(),
	// arSplinePoints[0].z(), arSplinePoints[1].x(),
	// arSplinePoints[1].y(), arSplinePoints[1].z(),
	// arSplinePoints[2].x(), arSplinePoints[2].y(),
	// arSplinePoints[2].z()};
	// splinePoints.put(fArPoints);
	// splinePoints.rewind();
	//
	// gl.glMap1f(GL2.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, splinePoints);
	//
	// // Line shadow
	// gl
	// .glColor4fv(
	// ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR,
	// 0);
	// // gl.glColor4f(28/255f, 122/255f, 254/255f, 1f);
	// gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2);
	// gl.glBegin(GL2.GL_LINE_STRIP);
	// for (int i = 0; i <= 10; i++) {
	// gl.glEvalCoord1f((float) i / 10);
	// }
	// gl.glEnd();
	//
	// // gl.glColor4fv(fArColor, 0);
	// // Point to mask artefacts
	// gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
	// // gl.glColor4f(254/255f, 160/255f, 28/255f, 1f);
	//
	// gl.glPointSize(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH - 0.5f);
	// gl.glBegin(GL2.GL_POINTS);
	// for (int i = 0; i <= 10; i++) {
	// gl.glEvalCoord1f((float) i / 10);
	// }
	// gl.glEnd();
	//
	// // The spline
	// gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
	//
	// gl.glBegin(GL2.GL_LINE_STRIP);
	// for (int i = 0; i <= 10; i++) {
	// gl.glEvalCoord1f((float) i / 10);
	// }
	// gl.glEnd();
	// }

	// private void renderLine(final GL2 gl, final Vec3f vecSrcPoint, final
	// Vec3f
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
	// // gl.glBegin(GL2.GL_LINES);
	// // gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() -
	// 0.001f);
	// // gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() -
	// 0.001f);
	// // gl.glEnd();
	//
	// gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
	// gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH +
	// iNumberOfLines);
	// gl.glBegin(GL2.GL_LINES);
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

	private Vec3f calculateCenter(ArrayList<ArrayList<Vec3f>> alPointLists) {
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

	private Vec3f calculateCenter(Collection<Vec3f> pointCollection) {

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