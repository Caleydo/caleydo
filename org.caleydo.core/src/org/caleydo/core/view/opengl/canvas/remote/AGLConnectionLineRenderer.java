package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.vislink.VisLinkEnvironment;

/**
 * Class is responsible for rendering and drawing of connection lines (resp. planes) between views in the
 * bucket setup.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLConnectionLineRenderer {
	protected RemoteLevel focusLevel;

	protected RemoteLevel stackLevel;

	protected ConnectedElementRepresentationManager connectedElementRepManager;

	protected boolean bEnableRendering = true;

	protected EnumMap<EIDType, HashMap<Integer, ArrayList<ArrayList<Vec3f>>>> hashIDTypeToViewToPointLists;
	
	protected int activeViewID = -1;

	/**
	 * Constructor.
	 */
	public AGLConnectionLineRenderer(final RemoteLevel focusLevel, final RemoteLevel stackLayer,
		final RemoteLevel poolLayer) {
		this.focusLevel = focusLevel;
		this.stackLevel = stackLayer;

		connectedElementRepManager =
			GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();

		hashIDTypeToViewToPointLists = new EnumMap<EIDType, HashMap<Integer, ArrayList<ArrayList<Vec3f>>>>(EIDType.class);
	}

	public void enableRendering(final boolean bEnableRendering) {
		this.bEnableRendering = bEnableRendering;
	}

	protected void init(final GL gl) {
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glEnable(GL.GL_MAP1_VERTEX_3);
	}

	public void render(final GL gl) {

		if (connectedElementRepManager.getOccuringIDTypes().size() == 0 || bEnableRendering == false)
			return;
		
		gl.glDisable(GL.GL_DEPTH_TEST);
		renderConnectionLines(gl);
		gl.glEnable(GL.GL_DEPTH_TEST);
	}

	protected abstract void renderConnectionLines(final GL gl);

	protected void renderLineBundling(final GL gl, EIDType idType, float[] fArColor) {
		Set<Integer> keySet = hashIDTypeToViewToPointLists.get(idType).keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		
		for (Integer iKey : keySet) {
			hashViewToCenterPoint.put(iKey, calculateCenter(hashIDTypeToViewToPointLists.get(idType).get(iKey)));
		}

		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());
//		ArrayList<Vec3f> pointsToDepthSort = new ArrayList<Vec3f>();
//		ArrayList<ArrayList<Vec3f>> connectionLinesCurrentView = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews = new ArrayList<ArrayList<ArrayList<Vec3f>>>(keySet.size());
		ArrayList<ArrayList<Vec3f>> bundlingToCenterLines = new ArrayList<ArrayList<Vec3f>>(keySet.size());
		ArrayList<ArrayList<Vec3f>> connectionLinesActiveView = new ArrayList<ArrayList<Vec3f>>(keySet.size());

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
			
			ArrayList<ArrayList<Vec3f>> connectionLinesCurrentView = new ArrayList<ArrayList<Vec3f>>(pointsToDepthSort.size()); //FIXME:added
			
			for(Vec3f currentPoint : depthSort(pointsToDepthSort)) {
				if(activeViewID != -1 && iKey == activeViewID)
					connectionLinesActiveView.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
				else
					connectionLinesCurrentView.add( createControlPoints( vecViewBundlingPoint, currentPoint, hashViewToCenterPoint.get(iKey) ) );
			}
			
//			renderLine(gl, vecViewBundlingPoint, vecCenter, 0, fArColor);
			ArrayList<Vec3f> bundlingToCenter = new ArrayList<Vec3f>(2);
			bundlingToCenter.add(vecViewBundlingPoint);
			bundlingToCenter.add(vecCenter);
			//connectionLinesCurrentView.add(bundlingToCenter);
			connectionLinesAllViews.add(connectionLinesCurrentView);
			bundlingToCenterLines.add(bundlingToCenter);
			
		}
//		VisLinkEnvironment visLinkEnvironment = new VisLinkEnvironment(connectionLinesAllViews, bundlingToCenterLines);
		VisLinkEnvironment visLinkEnvironment = new VisLinkEnvironment(connectionLinesAllViews, bundlingToCenterLines, connectionLinesActiveView);
		visLinkEnvironment.renderLines(gl);
	}
	
	private ArrayList<Vec3f> createControlPoints(Vec3f vecSrcPoint, Vec3f vecDstPoint, Vec3f vecViewCenterPoint) {
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

	private void renderPlanes(final GL gl, final Vec3f vecPoint, final ArrayList<Vec3f> alPoints) {

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
	 * Render straight connection lines.
	 * 
	 * @param gl
	 * @param vecSrcPoint
	 * @param vecDestPoint
	 * @param iNumberOfLines
	 * @param fArColor
	 */
//	private void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
//		final int iNumberOfLines, float[] fArColor) {
////		// Line shadow
////		// gl.glColor4f(0.3f, 0.3f, 0.3f, 1);// , 0.6f);
////		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
////		// gl.glColor4f(28/255f, 122/255f, 254/255f, 1f);
////		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 1.5f);
////		gl.glBegin(GL.GL_LINES);
////		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
////		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
////		gl.glEnd();
////
////		// gl.glColor4fv(fArColor, 0);
////
////		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
////		// gl.glColor4f(254/255f, 160/255f, 28/255f, 1f);
////		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
////
////		gl.glBegin(GL.GL_LINES);
////		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
////		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
////		gl.glEnd();
//		
//		
////		VisLink.renderLine(gl, vecSrcPoint, vecDestPoint, true);
//		ArrayList<Vec3f> controlPoints = new ArrayList<Vec3f>();
//		controlPoints.add(vecSrcPoint);
//		controlPoints.add(vecDestPoint);
////		VisLink.renderLine(gl, controlPoints, 0, 10, true);
////		VisLink.renderPolygonLine(gl, controlPoints, 0, 10, true, true);
////		VisLink.renderPolygonLineWithHalo(gl, controlPoints, 0);
//		VisLink.renderAnimatedPolygonLine(gl, controlPoints, 0, 10, true, true);
//	}

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
////		Vec3f[] arSplinePoints = new Vec3f[3];
////
////		arSplinePoints[0] = vecSrcPoint.copy();
////		arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint);
////		arSplinePoints[2] = vecDestPoint.copy();
////
////		FloatBuffer splinePoints = FloatBuffer.allocate(8 * 3);
////		// float[] fArPoints =
////		// {1,2,-1,0,1,2,2,0,0,3,3,1,2,3,-2,1,3,1,1,3,0,2,-1,-1};
////		float[] fArPoints =
////			{ arSplinePoints[0].x(), arSplinePoints[0].y(), arSplinePoints[0].z(), arSplinePoints[1].x(),
////					arSplinePoints[1].y(), arSplinePoints[1].z(), arSplinePoints[2].x(),
////					arSplinePoints[2].y(), arSplinePoints[2].z() };
////		splinePoints.put(fArPoints);
////		splinePoints.rewind();
////
////		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, splinePoints);
////
////		// Line shadow
////		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
////		// gl.glColor4f(28/255f, 122/255f, 254/255f, 1f);
////		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2);
////		gl.glBegin(GL.GL_LINE_STRIP);
////		for (int i = 0; i <= 10; i++) {
////			gl.glEvalCoord1f((float) i / 10);
////		}
////		gl.glEnd();
////
////		// gl.glColor4fv(fArColor, 0);
////		// Point to mask artefacts
////		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
////		// gl.glColor4f(254/255f, 160/255f, 28/255f, 1f);
////
////		gl.glPointSize(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH - 0.5f);
////		gl.glBegin(GL.GL_POINTS);
////		for (int i = 0; i <= 10; i++) {
////			gl.glEvalCoord1f((float) i / 10);
////		}
////		gl.glEnd();
////
////		// The spline
////		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
////
////		gl.glBegin(GL.GL_LINE_STRIP);
////		for (int i = 0; i <= 10; i++) {
////			gl.glEvalCoord1f((float) i / 10);
////		}
////		gl.glEnd();
//		
//		ArrayList<Vec3f> controlPoints = new ArrayList<Vec3f>();
//		controlPoints.add(vecDestPoint); // TEST
////		controlPoints.add(vecSrcPoint);
//		controlPoints.add(calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint));
//		controlPoints.add(vecSrcPoint); // TEST
////		controlPoints.add(vecDestPoint);
////		VisLink.renderLine(gl, controlPoints, 0, 10, true);
////		VisLink.renderPolygonLine(gl, controlPoints, 0, 10, true, true);
////		VisLink.renderPolygonLineWithHalo(gl, controlPoints, 0);
//		VisLink.renderAnimatedPolygonLine(gl, controlPoints, 0, 10, true, true);
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

	protected Vec3f transform(Vec3f vecOriginalPoint, Vec3f vecTranslation, Vec3f vecScale, Rotf rotation,
		RemoteLevelElement remoteLevelElement) {
		Mat4f matSrc = new Mat4f();
		Vec3f vecTransformedPoint = new Vec3f();

		rotation.toMatrix(matSrc);

		if (GeneralManager.get().getTrackDataProvider().isTrackModeActive()) {
//			WiiRemote wiiRemote = GeneralManager.get().getWiiRemote();

			float[] fArHeadPosition = GeneralManager.get().getTrackDataProvider().getEyeTrackData();

			fArHeadPosition[0] -= 183;
			fArHeadPosition[1] -= 112;

			fArHeadPosition[0] = fArHeadPosition[0] - 1730/2;
			fArHeadPosition[1] = (fArHeadPosition[1] - 1055/2f);
			
			fArHeadPosition[0] = fArHeadPosition[0] / 1730 * 4f;
			fArHeadPosition[1] = fArHeadPosition[1] / 1055 * 4f * 0.61f;
			
//			fArHeadPosition[0] = 0f;
//			fArHeadPosition[1] = -1.3f;
			
//			fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
//			fArHeadPosition[1] *= 4;

			float fBucketWidth = 2f;
			float fBucketHeight = 2f;
			float fBucketDepth = 4.0f;
			float fBucketBottomLeft = -1 * fArHeadPosition[0] - fBucketWidth;// - 1.5f;
			float fBucketBottomRight = -1 * fArHeadPosition[0] + fBucketWidth;// - 1.5f;
			float fBucketBottomTop = fArHeadPosition[1] * 1.4f + fBucketHeight;
			float fBucketBottomBottom = fArHeadPosition[1] * 1.4f - fBucketHeight;

			float fNormalizedHeadDist =  
				-1 * GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f
				+ Math.abs(fBucketBottomRight - 2) / 2 + Math.abs(fBucketBottomTop - 2) * 2;// / 2;
//				-1 * GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f + Math.abs(fBucketBottomRight - 2) / 2
//					+ Math.abs(fBucketBottomTop - 2) / 2;

			Vec3f vecTrackTranformPoint = new Vec3f(vecOriginalPoint);

			if (stackLevel.getElementByPositionIndex(1) == remoteLevelElement) {
				float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
				float fGK = fBucketWidth + fBucketBottomLeft;
				float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));

				float fTransformedX = vecOriginalPoint.x() / 8f * fPlaneWidth;
				float fTransformedY = vecOriginalPoint.y() / 8f * fBucketHeight * 2f;// * (4 + fYTop - (-4 +
				// fYBottom));

				float fXScaling = fTransformedX / fPlaneWidth;
				fTransformedY += fArHeadPosition[1] * fXScaling;

				vecTrackTranformPoint =
					new Vec3f(fTransformedX, -fBucketHeight + fTransformedY, vecOriginalPoint.z()); // / 4f *
				// fBucketHeight
			}
			else if (stackLevel.getElementByPositionIndex(3) == remoteLevelElement) {
				float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
				float fGK = fBucketWidth - fBucketBottomRight;
				float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));

				float fTransformedX = vecOriginalPoint.x() / 8f * fPlaneWidth;
				float fTransformedY = vecOriginalPoint.y() / 8f * fBucketHeight * 2f;// * (4 + fYTop - (-4 +
				// fYBottom));

				float fXScaling = fTransformedX / fPlaneWidth;
				fTransformedY += fArHeadPosition[1] * (1 - fXScaling);

				vecTrackTranformPoint =
					new Vec3f(fPlaneWidth - fTransformedX, -fBucketHeight + fTransformedY, vecOriginalPoint
						.z()); // /
				// 4f
				// *
				// fBucketHeight
			}
			else if (stackLevel.getElementByPositionIndex(0) == remoteLevelElement) {
				float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
				float fGK = fBucketWidth - fBucketBottomTop;
				float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));

				float fTransformedX = vecOriginalPoint.x() / 8f * fBucketHeight * 2f;
				float fTransformedY = vecOriginalPoint.y() / 8f * fPlaneWidth;// * (4 + fYTop - (-4 +
				// fYBottom));

				float fYScaling = fTransformedY / fPlaneWidth;
				// fTransformedX += fArHeadPosition[0] * fYScaling;

				vecTrackTranformPoint =
					new Vec3f((fBucketWidth + fBucketBottomLeft) * (1 - fYScaling) + fTransformedX,
						fPlaneWidth - fTransformedY, vecOriginalPoint.z());
			}
			else if (stackLevel.getElementByPositionIndex(2) == remoteLevelElement) {
				float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
				float fGK = fBucketWidth + fBucketBottomBottom;
				float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));

				float fTransformedX = vecOriginalPoint.x() / 8f * fBucketHeight * 2f;
				float fTransformedY = vecOriginalPoint.y() / 8f * fPlaneWidth;

				float fYScaling = fTransformedY / fPlaneWidth;
				// fTransformedX += fArHeadPosition[1] * (1-fYScaling);

				vecTrackTranformPoint =
					new Vec3f((fBucketWidth + fBucketBottomLeft) * fYScaling + fTransformedX, fTransformedY,
						vecOriginalPoint.z());
			}

			matSrc.xformPt(vecTrackTranformPoint, vecTransformedPoint);
		}
		else {
			matSrc.xformPt(vecOriginalPoint, vecTransformedPoint);
		}

		vecTransformedPoint.componentMul(vecScale);
		vecTransformedPoint.add(vecTranslation);

		return vecTransformedPoint;
	}
	
	
	/**
	 * 		Depth-sorts the given set of points according to their z-value.
	 * 
	 * @param points Specifies the given set of points to be depth-sorted.
	 * @return The depth-sorted set of points.
	 */
	protected ArrayList<Vec3f> depthSort(final ArrayList<Vec3f> points) {
		ArrayList<Vec3f> sortedPoints = new ArrayList<Vec3f>();
		boolean foundSpot = false;
		
		for(Vec3f point : points) {
			foundSpot = false;
			for(int i = 0; i < sortedPoints.size(); i++)
				if(point.z() <= sortedPoints.get(i).z()) {
					sortedPoints.add(i, point);
					foundSpot = true;
					break;
				}
			if(foundSpot == false)
				sortedPoints.add(point);
		}
//		System.out.println("Points:");
//		for(Vec3f point : points)
//			System.out.println(point.z());
//		System.out.println("Sorted Points:");
//		for(Vec3f sorted : sortedPoints)
//			System.out.println(sorted.z());
//		System.out.println("----------------------------------------");
		return sortedPoints;
	}
	
	
	/**
	 * Sets ID of view in which the mouse cursor is currently positioned
	 * @param viewID ID of current active View
	 */
	public void setActiveViewID(int viewID){
		activeViewID = viewID;
	}
}
