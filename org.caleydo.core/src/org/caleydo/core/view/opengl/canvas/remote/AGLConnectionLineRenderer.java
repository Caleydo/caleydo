package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.media.opengl.GL;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;

/**
 * Class is responsible for rendering and drawing of connection lines (resp.
 * planes) between views in the bucket setup.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLConnectionLineRenderer
{
	protected RemoteHierarchyLevel underInteractionLayer;

	protected RemoteHierarchyLevel stackLayer;

	protected ConnectedElementRepresentationManager connectedElementRepManager;

	protected boolean bEnableRendering = true;

	protected HashMap<Integer, ArrayList<ArrayList<Vec3f>>> hashViewToPointLists;

	/**
	 * Constructor.
	 */
	public AGLConnectionLineRenderer(final RemoteHierarchyLevel underInteractionLayer,
			final RemoteHierarchyLevel stackLayer, final RemoteHierarchyLevel poolLayer)
	{
		this.underInteractionLayer = underInteractionLayer;
		this.stackLayer = stackLayer;

		connectedElementRepManager = GeneralManager.get().getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();

		hashViewToPointLists = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
	}

	public void enableRendering(final boolean bEnableRendering)
	{
		this.bEnableRendering = bEnableRendering;
	}
	
	protected void init(final GL gl)
	{        
        gl.glShadeModel(GL.GL_SMOOTH);
		gl.glEnable(GL.GL_MAP1_VERTEX_3);	
	}

	public void render(final GL gl)
	{

		if ((connectedElementRepManager.getOccuringIDTypes().size() == 0)
				|| (bEnableRendering == false))
			return;

		renderConnectionLines(gl);
	}

	protected abstract void renderConnectionLines(final GL gl);

	protected void renderLineBundling(final GL gl)
	{
		Set<Integer> keySet = hashViewToPointLists.keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();

		for (Integer iKey : keySet)
		{
			hashViewToCenterPoint.put(iKey, calculateCenter(hashViewToPointLists.get(iKey)));
		}
		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());

		for (Integer iKey : keySet)
		{
			Vec3f vecViewBundlingPoint = calculateBundlingPoint(hashViewToCenterPoint
					.get(iKey), vecCenter);

			for (ArrayList<Vec3f> alCurrentPoints : hashViewToPointLists.get(iKey))
			{
				if (alCurrentPoints.size() > 1)
				{
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				}
				else
				{
					renderLine(gl, vecViewBundlingPoint, alCurrentPoints.get(0), 0, 
							hashViewToCenterPoint.get(iKey));
				}
			}
			renderLine(gl, vecViewBundlingPoint, vecCenter, 0);
		}
	}

	private Vec3f calculateBundlingPoint(Vec3f vecViewCenter, Vec3f vecCenter)
	{
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

	private void renderPlanes(final GL gl, final Vec3f vecPoint,
			final ArrayList<Vec3f> alPoints)
	{

		gl.glColor4f(0.3f, 0.3f, 0.3f, 1f);// 0.6f);
		gl.glLineWidth(2 + 4);
		gl.glBegin(GL.GL_LINES);
		for (Vec3f vecCurrent : alPoints)
		{
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

		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINES);
		for (Vec3f vecCurrent : alPoints)
		{
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
		for (Vec3f vecCurrent : alPoints)
		{

			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		gl.glEnd();
	}

	private void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
			final int iNumberOfLines)
	{
		// Line shadow
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);// , 0.6f);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines + 4);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
		gl.glEnd();

		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
		gl.glEnd();
	}
	
	private void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
			final int iNumberOfLines, Vec3f vecViewCenterPoint)
	{
		Vec3f[] arSplinePoints = new Vec3f[3];
		
		arSplinePoints[0] = vecSrcPoint.copy();		
		arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint);
		arSplinePoints[2] = vecDestPoint.copy();
		
		FloatBuffer splinePoints = FloatBuffer.allocate(8*3);
//        float[] fArPoints = {1,2,-1,0,1,2,2,0,0,3,3,1,2,3,-2,1,3,1,1,3,0,2,-1,-1};
		float[] fArPoints = {arSplinePoints[0].x(), arSplinePoints[0].y(), arSplinePoints[0].z(),
				arSplinePoints[1].x(), arSplinePoints[1].y(), arSplinePoints[1].z(),
				arSplinePoints[2].x(), arSplinePoints[2].y(), arSplinePoints[2].z()};
		splinePoints.put(fArPoints);
        splinePoints.rewind();
		
		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, splinePoints); 
		
		// Line shadow
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);// , 0.6f);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines + 4);
		
//		gl.glPointSize(5);
//		gl.glBegin(GL.GL_POINTS);
//		for (int i=0; i<=10; i++)
//		{
//			gl.glEvalCoord1f((float)(i)/10);
//		}
//		gl.glEnd();
//		
		
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i=0; i<=10; i++)
		{
			gl.glEvalCoord1f((float)(i)/10);
		}
		gl.glEnd();
		
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
				
		gl.glPointSize(3.2f);
		gl.glBegin(GL.GL_POINTS);
		for (int i=0; i<=10; i++)
		{
			gl.glEvalCoord1f((float)(i)/10);
		}
		gl.glEnd();

		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines);
		
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i=0; i<=10; i++)
		{
			gl.glEvalCoord1f((float)(i)/10);
		}
		gl.glEnd();
	}
	
//	private void renderLine(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint,
//			final int iNumberOfLines, Vec3f vecViewCenterPoint)
//	{
//		Vec3f[] arSplinePoints = new Vec3f[3];
//		
////		Vec3f vecDirection = new Vec3f();
////		vecDirection = vecCenter.minus(vecViewCenter);
////		float fLength = vecDirection.length();
////		vecDirection.normalize();
////
////		Vec3f vecViewBundlingPoint2 = new Vec3f();
////		// Vec3f vecDestBundingPoint = new Vec3f();
////
////		vecViewBundlingPoint = vecViewCenter.copy();
////		vecDirection.scale(fLength / 3);
////		vecViewBundlingPoint.add(vecDirection);
//		
//		arSplinePoints[0] = vecSrcPoint.copy();		
//		arSplinePoints[1] = calculateBundlingPoint(vecSrcPoint, vecViewCenterPoint);
//		arSplinePoints[2] = vecDestPoint.copy();
//		
//		// FIXME: Do not create spline in every render frame
//		Spline3D spline = new Spline3D(arSplinePoints, 0.001f, 0.01f);
//		
////		// Line shadow
////		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);// , 0.6f);
////		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines + 4);
////		gl.glBegin(GL.GL_LINES);
////		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
////		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
////		gl.glEnd();
//
//		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
//		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines);
//		gl.glBegin(GL.GL_LINES);
//
//		for (int i=0; i<(arSplinePoints.length-1)*10; i++)
//		{
//			Vec3f vec = spline.getPositionAt((float)i / 10);
//			gl.glVertex3f(vec.x(), vec.y(), vec.z());
//			vec = spline.getPositionAt(((float)i+1) / 10);
//			gl.glVertex3f(vec.x(), vec.y(), vec.z());			
//		}
////		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
////		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
//
//		gl.glEnd();
//	}

	private Vec3f calculateCenter(ArrayList<ArrayList<Vec3f>> alPointLists)
	{
		Vec3f vecCenterPoint = new Vec3f(0, 0, 0);

		int iCount = 0;
		for (ArrayList<Vec3f> currentList : alPointLists)
		{
			for (Vec3f vecCurrent : currentList)
			{
				vecCenterPoint.add(vecCurrent);
				iCount++;
			}

		}
		vecCenterPoint.scale(1.0f / iCount);
		return vecCenterPoint;
	}

	private Vec3f calculateCenter(Collection<Vec3f> pointCollection)
	{

		Vec3f vecCenterPoint = new Vec3f(0, 0, 0);

		int iCount = 0;

		for (Vec3f vecCurrent : pointCollection)
		{
			vecCenterPoint.add(vecCurrent);
			iCount++;
		}

		vecCenterPoint.scale(1.0f / iCount);
		return vecCenterPoint;
	}

	protected Vec3f transform(Vec3f vecOriginalPoint, Vec3f vecTranslation, Vec3f vecScale,
			Rotf rotation)
	{

		Mat4f matSrc = new Mat4f();
		Vec3f vecTransformedPoint = new Vec3f();

		rotation.toMatrix(matSrc);

		matSrc.xformPt(vecOriginalPoint, vecTransformedPoint);

		vecTransformedPoint.componentMul(vecScale);

		vecTransformedPoint.add(vecTranslation);

		return vecTransformedPoint;
	}
}
