package org.geneview.core.view.opengl.canvas.bucket;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.media.opengl.GL;

import org.geneview.core.data.view.rep.renderstyle.ConnectionLineRenderStyle;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.SelectionManager;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Class is responsible for rendering and drawing
 * of connection lines (resp. planes) between views in the 
 * bucket setup.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public class GLConnectionLineRenderer {
	
	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	
	private SelectionManager selectionManager;
	
	private boolean bEnableRendering = true;
	

	
	
	private HashMap<Integer, ArrayList<ArrayList<Vec3f>>> hashViewToPointLists;
	
	/**
	 * Constructor.
	 */
	public GLConnectionLineRenderer(final IGeneralManager generalManager,
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
	
	
	private void renderConnectionLines(final GL gl) 
	{
		
		Vec3f vecTranslation;
		Vec3f vecScale;
		
		Rotf rotation;
		Mat4f matSrc = new Mat4f();
		Mat4f matDest = new Mat4f();
		matSrc.makeIdent();
		matDest.makeIdent();

		Iterator<Integer> iterSelectedElementID = 
			selectionManager.getAllSelectedElements().iterator();
		
		
		ArrayList<ArrayList<Vec3f>> alPointLists = null;// 
		
		while(iterSelectedElementID.hasNext()) 
		{
			int iSelectedElementID = iterSelectedElementID.next();
			Iterator<SelectedElementRep> iterSelectedElementRep = 
				selectionManager.getSelectedElementRepsByElementID(iSelectedElementID).iterator();			
			
			while (iterSelectedElementRep.hasNext())
			{
				SelectedElementRep selectedElementRep = iterSelectedElementRep.next();

				JukeboxHierarchyLayer activeLayer = null;
				// Check if element is in under interaction layer
				if (underInteractionLayer.containsElement(selectedElementRep.getContainingViewID()))
				{
					activeLayer = underInteractionLayer;
				}
				else if(stackLayer.containsElement(selectedElementRep.getContainingViewID()))
				{
					activeLayer = stackLayer;
				}
				
				if(activeLayer != null)
				{
					vecTranslation = activeLayer.getTransformByElementId(
							selectedElementRep.getContainingViewID()).getTranslation();
					vecScale = activeLayer.getTransformByElementId(
							selectedElementRep.getContainingViewID()).getScale();
					rotation = activeLayer.getTransformByElementId(
							selectedElementRep.getContainingViewID()).getRotation();
										
					ArrayList<Vec3f> alPoints = selectedElementRep.getPoints();
					ArrayList<Vec3f> alPointsTransformed = new ArrayList<Vec3f>();
					
					for (Vec3f vecCurrentPoint : alPoints)
					{
						alPointsTransformed.add(transform(vecCurrentPoint, vecTranslation, vecScale, rotation));
					}
					int iKey = selectedElementRep.getContainingViewID();
					
					alPointLists = hashViewToPointLists.get(iKey);
					if(alPointLists == null)						
					{
						alPointLists = new ArrayList<ArrayList<Vec3f>>();
						hashViewToPointLists.put(iKey, alPointLists);
					}
					alPointLists.add(alPointsTransformed);
					
				}					
			}
	
			if(hashViewToPointLists.size() > 1)	
			{
				
				renderLineBundling(gl);				
				hashViewToPointLists.clear();
			}			
		}		
	}
	
	private void renderLineBundling(final GL gl)
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
		
		gl.glColor4f(1, 1, 1, 1);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 4);
		gl.glBegin(GL.GL_LINES);		
		for(Vec3f vecCurrent : alPoints)
		{
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z() - 0.001f);
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z() - 0.001f);
		}
		gl.glEnd();
		
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		gl.glBegin(GL.GL_LINES);		
		for(Vec3f vecCurrent : alPoints)
		{
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
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
		
		gl.glColor4f(1, 1, 1, 0.6f);
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
	
	
	
	private Vec3f transform(Vec3f vecOriginalPoint, Vec3f vecTranslation, Vec3f vecScale, Rotf rotation)
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
