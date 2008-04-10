package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.rep.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.SelectionManager;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Class is responsible for rendering and drawing
 * of connection lines (resp. planes) between views in the 
 * bucket setup.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public abstract class AGLConnectionLineRenderer {
	
	protected JukeboxHierarchyLayer underInteractionLayer;
	protected JukeboxHierarchyLayer stackLayer;
	
	protected SelectionManager selectionManager;
	
	protected boolean bEnableRendering = true;
	
	protected HashMap<Integer, ArrayList<ArrayList<Vec3f>>> hashViewToPointLists;
	
	/**
	 * Constructor.
	 */
	public AGLConnectionLineRenderer(final IGeneralManager generalManager,
			final JukeboxHierarchyLayer underInteractionLayer,
			final JukeboxHierarchyLayer stackLayer,
			final JukeboxHierarchyLayer poolLayer) 
	{
		
		this.underInteractionLayer = underInteractionLayer;
		this.stackLayer = stackLayer;
		
		selectionManager = generalManager.getSingelton().getViewGLCanvasManager().getSelectionManager();
		
		hashViewToPointLists = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
	}
	
	public void enableRendering(final boolean bEnableRendering) 
	{
		
		this.bEnableRendering = bEnableRendering;
	}
	
	public void render(final GL gl) 
	{
		
		if ((selectionManager.getAllSelectedElements().size() == 0)
				|| (bEnableRendering == false))
			return;
		
		renderConnectionLines(gl);
	}
	
	protected abstract void renderConnectionLines(final GL gl); 
	
	protected void renderLineBundling(final GL gl)
	{	
		Set<Integer> keySet =  hashViewToPointLists.keySet();
		HashMap<Integer, Vec3f> hashViewToCenterPoint = new HashMap<Integer, Vec3f>();
		
		for(Integer iKey : keySet)
		{
			 hashViewToCenterPoint.put(iKey, calculateCenter(hashViewToPointLists.get(iKey)));
		}
		Vec3f vecCenter = calculateCenter(hashViewToCenterPoint.values());
		
		for(Integer iKey : keySet)
		{
			Vec3f vecViewBundlingPoint = 
				calculateBundlingPoint(hashViewToCenterPoint.get(iKey), vecCenter);
		
			for(ArrayList<Vec3f> alCurrentPoints : hashViewToPointLists.get(iKey))
			{
				if (alCurrentPoints.size() > 1)
				{
					renderPlanes(gl, vecViewBundlingPoint, alCurrentPoints);
				}
				else
				{
					renderLine(gl, vecViewBundlingPoint, alCurrentPoints.get(0), 0);
					
				}
			}
			renderLine(gl, vecViewBundlingPoint, vecCenter, 2);		
		}
	}
	

	private Vec3f calculateBundlingPoint(Vec3f vecViewCenter, Vec3f vecCenter)
	{
		Vec3f vecDirection = new Vec3f();
		vecDirection = vecCenter.minus(vecViewCenter);
		float fLength = vecDirection.length();
		vecDirection.normalize();
		
		Vec3f vecViewBundlingPoint = new Vec3f();
		//Vec3f vecDestBundingPoint = new Vec3f();
		
		vecViewBundlingPoint = vecViewCenter.copy();
		vecDirection.scale(fLength / 3);
		vecViewBundlingPoint.add(vecDirection);
		return vecViewBundlingPoint;
	}
	
	private void renderPlanes(final GL gl, final Vec3f vecPoint, final ArrayList<Vec3f> alPoints)
	{
		
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1f);//0.6f);
		gl.glLineWidth(2 + 4);
		gl.glBegin(GL.GL_LINES);		
		for(Vec3f vecCurrent : alPoints)
		{
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z() - 0.001f);
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z() - 0.001f);
		}
//		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
//		gl.glVertex3f(alPoints.get(0).x(), alPoints.get(0).y(), alPoints.get(0).z());
//		
//		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
//		gl.glVertex3f(alPoints.get(alPoints.size()-1).x(), alPoints.get(alPoints.size()-1).y(), alPoints.get(0).z());
		gl.glEnd();
		
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINES);		
		for(Vec3f vecCurrent : alPoints)
		{
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
//		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
//		gl.glVertex3f(alPoints.get(0).x(), alPoints.get(0).y(), alPoints.get(0).z());
//		
//		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
//		gl.glVertex3f(alPoints.get(alPoints.size()-1).x(), alPoints.get(alPoints.size()-1).y(), alPoints.get(0).z());
		
		gl.glEnd();
		
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_COLOR, 0);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		for(Vec3f vecCurrent : alPoints)
		{
			
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		gl.glEnd();		
	}
	
	private void renderLine(final GL gl, 
			final Vec3f vecSrcPoint, 
			final Vec3f vecDestPoint, 
			final int iNumberOfLines)
	{	
		// Line shadow
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);//, 0.6f);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines + 4);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(),
				vecSrcPoint.y(),
				vecSrcPoint.z() - 0.001f);
		gl.glVertex3f(vecDestPoint.x(),
				vecDestPoint.y(),
				vecDestPoint.z() - 0.001f);
		gl.glEnd();
		
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + iNumberOfLines);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(),
				vecSrcPoint.y(),
				vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(),
				vecDestPoint.y(),
				vecDestPoint.z());
		gl.glEnd();	
	}
	
	private Vec3f calculateCenter(ArrayList<ArrayList<Vec3f>> alPointLists)
	{
		Vec3f vecCenterPoint = new Vec3f(0, 0 , 0);
		
		int iCount = 0;
		for(ArrayList<Vec3f> currentList : alPointLists)
		{
			for(Vec3f vecCurrent : currentList)
			{
				vecCenterPoint.add(vecCurrent);
				iCount++;
			}
			
		}
		vecCenterPoint.scale(1.0f/iCount);
		return vecCenterPoint;		
	}
	
	private Vec3f calculateCenter(Collection<Vec3f> pointCollection)
	{
		Vec3f vecCenterPoint = new Vec3f(0, 0 , 0);
		
		int iCount = 0;
		
		for(Vec3f vecCurrent : pointCollection)
		{
			vecCenterPoint.add(vecCurrent);
			iCount++;
		}
			
		
		vecCenterPoint.scale(1.0f/iCount);
		return vecCenterPoint;		
	}
	
	protected Vec3f transform(Vec3f vecOriginalPoint, Vec3f vecTranslation, Vec3f vecScale, Rotf rotation)
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
