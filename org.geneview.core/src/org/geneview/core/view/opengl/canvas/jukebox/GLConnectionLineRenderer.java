package org.geneview.core.view.opengl.canvas.jukebox;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.data.view.rep.renderstyle.ConnectionLineRenderStyle;
import org.geneview.core.data.view.rep.renderstyle.PathwayRenderStyle;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.SelectionManager;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Class is responsible for rendering and drawing
 * of connection lines between views in the overall 
 * jukebox setup.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public class GLConnectionLineRenderer {
	
	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	private JukeboxHierarchyLayer poolLayer;
	
	private SelectionManager selectionManager;
	
	private boolean bEnableRendering = true;
	
	/**
	 * Constructor.
	 */
	public GLConnectionLineRenderer(final IGeneralManager generalManager,
			final JukeboxHierarchyLayer underInteractionLayer,
			final JukeboxHierarchyLayer stackLayer,
			final JukeboxHierarchyLayer poolLayer) {
		
		this.underInteractionLayer = underInteractionLayer;
		this.stackLayer = stackLayer;
		this.poolLayer = poolLayer;
		
		selectionManager = generalManager.getSingelton().getViewGLCanvasManager().getSelectionManager();
	}
	
	public void render(final GL gl) {
		
		//renderTestViewConnectionLines(gl);

		if ((selectionManager.getAllSelectedElements().size() == 0)
				|| (bEnableRendering == false))
			return;
		
		renderConnectionLines(gl);
	}
	
	
	private void renderConnectionLines(final GL gl) {
		
		Vec3f vecTranslationSrc;
		Vec3f vecTranslationDest;
		Vec3f vecScaleSrc;
		Vec3f vecScaleDest;
		
		Rotf rotSrc;
		Rotf rotDest;
		Mat4f matSrc = new Mat4f();
		Mat4f matDest = new Mat4f();
		matSrc.makeIdent();
		matDest.makeIdent();

		Iterator<Integer> iterSelectedElementID = 
			selectionManager.getAllSelectedElements().iterator();
		
		ArrayList<ArrayList<Vec3f>> alSrcPointLists = new ArrayList<ArrayList<Vec3f>>();
		ArrayList<ArrayList<Vec3f>> alDestPointLists = new ArrayList<ArrayList<Vec3f>>();
	
		while(iterSelectedElementID.hasNext()) 
		{
			int iSelectedElementID = iterSelectedElementID.next();
			Iterator<SelectedElementRep> iterSelectedElementRep = 
				selectionManager.getSelectedElementRepsByElementID(iSelectedElementID).iterator();
			
			
			
			while (iterSelectedElementRep.hasNext())
			{
				SelectedElementRep selectedElementRepSrc = iterSelectedElementRep.next();

				// Check if element is in under interaction layer
				if (underInteractionLayer.containsElement(selectedElementRepSrc.getContainingViewID()))
				{
					vecTranslationSrc = underInteractionLayer.getTransformByElementId(
							selectedElementRepSrc.getContainingViewID()).getTranslation();
					vecScaleSrc = underInteractionLayer.getTransformByElementId(
							selectedElementRepSrc.getContainingViewID()).getScale();
					rotSrc = underInteractionLayer.getTransformByElementId(
							selectedElementRepSrc.getContainingViewID()).getRotation();
					
					
					ArrayList<Vec3f> alSrcPoints = selectedElementRepSrc.getPoints();
					ArrayList<Vec3f> alSrcPointsTransformed = new ArrayList<Vec3f>();
					
					for (Vec3f vecCurrentPoint : alSrcPoints)
					{
						alSrcPointsTransformed.add(transform(vecCurrentPoint, vecTranslationSrc, vecScaleSrc, rotSrc));
					}
					
					alSrcPointLists.add(alSrcPointsTransformed);
					
				}
			}
			
			Iterator<SelectedElementRep> iterSelectedElementRepInner = 
				selectionManager.getSelectedElementRepsByElementID(iSelectedElementID).iterator();
			
			while(iterSelectedElementRepInner.hasNext())
			{
				SelectedElementRep selectedElementRepDest = iterSelectedElementRepInner.next();
				
				// Check if element is in stack view
				if (stackLayer.containsElement(selectedElementRepDest.getContainingViewID()))
				{
					vecTranslationDest = stackLayer.getTransformByElementId(
							selectedElementRepDest.getContainingViewID()).getTranslation();
					vecScaleDest = stackLayer.getTransformByElementId(
							selectedElementRepDest.getContainingViewID()).getScale();
					rotDest = stackLayer.getTransformByElementId(
							selectedElementRepDest.getContainingViewID()).getRotation();
					
			
					
					ArrayList<Vec3f> alDestPoints = selectedElementRepDest.getPoints();
					ArrayList<Vec3f> alDestPointsTransformed = new ArrayList<Vec3f>();
					for (Vec3f vecCurrentPoint : alDestPoints)
					{
						alDestPointsTransformed.add(transform(vecCurrentPoint, vecTranslationDest, vecScaleDest, rotDest));
					}								
			
					
					alDestPointLists.add(alDestPointsTransformed);
					//renderLineBundling(gl, alSrcPointsTransformed, alDestPointsTransformed);
					//renderConnection(gl, alSrcPointsTransformed, alDestPointsTransformed);
			
				}
			}
			if(alSrcPointLists.size() > 0 && alDestPointLists.size() > 0)	
			{
				renderLineBundling(gl, alSrcPointLists, alDestPointLists);
				alSrcPointLists.clear();
				alDestPointLists.clear();
			}
			
		}
		
		gl.glLineWidth(1);
	}
	
	private void renderLineBundling(final GL gl, 
			final ArrayList<ArrayList<Vec3f>> alSrcPointLists, 
			final ArrayList<ArrayList<Vec3f>> alDestPointLists)
	{
		Vec3f vecSrcCenter = calculateCenter(alSrcPointLists);
		Vec3f vecDestCenter = calculateCenter(alDestPointLists);
		
		Vec3f vecDirection = new Vec3f();
		vecDirection = vecDestCenter.minus(vecSrcCenter);
		float fLength = vecDirection.length();
		vecDirection.normalize();
		
		Vec3f vecSrcBundlingPoint = new Vec3f();
		//Vec3f vecDestBundingPoint = new Vec3f();
		
		vecSrcBundlingPoint = vecDirection.copy();
		vecSrcBundlingPoint.scale(fLength / 3);
		
		vecSrcBundlingPoint.add(vecSrcCenter);
		
		// is not normalized any more after this
		vecDirection.scale(fLength / 3);
		
		Vec3f vecDestBundlingPoint = vecDestCenter.minus(vecDirection);
		
		int iNumberOfLines = 0;
		
		// condition is true for polylines (planes)
		if(alSrcPointLists.size() == 1 && alSrcPointLists.get(0).size() > 1)
		{
			renderPlanes(gl, vecSrcBundlingPoint, alSrcPointLists.get(0));
		}
		else
		{
			renderLines(gl, vecSrcBundlingPoint, alSrcPointLists);
			iNumberOfLines = alSrcPointLists.size();
		}
		// condition is true for polylines (planes)
		if(alDestPointLists.size() == 1 && alDestPointLists.get(0).size() > 1)
		{
			renderPlanes(gl, vecDestBundlingPoint, alDestPointLists.get(0));
		}
		else
		{
			renderLines(gl, vecDestBundlingPoint, alDestPointLists);
			iNumberOfLines = alDestPointLists.size();
		}
		
		// TODO: render LineS
//		renderPlanes(gl, vecSrcBundlingPoint, alSrcPointList);
//		
//		renderPlanes(gl, vecDestBundingPoint, alDestPoints);
		
		
		renderLine(gl, vecSrcBundlingPoint, vecDestBundlingPoint, iNumberOfLines);
		

	}
	

	
	private void renderPlanes(final GL gl, final Vec3f vecPoint, final ArrayList<Vec3f> alPoints)
	{
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
	
	private void renderLines(final GL gl, final Vec3f vecPoint, final ArrayList<ArrayList<Vec3f>> alPointLists)
	{
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		gl.glBegin(GL.GL_LINES);		
		for(ArrayList<Vec3f> alCurrent : alPointLists)
		{
			if (alCurrent.size() > 1)
			{
				throw new GeneViewRuntimeException("GLConnectionLineRenderer: " +
						"Selected Element Rep should not have more than one point in renderLines", GeneViewRuntimeExceptionType.VIEW);
			}
			Vec3f vecCurrent = alCurrent.get(0);
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		gl.glEnd();
	}
	
	private void renderLine(final GL gl, 
			final Vec3f vecSrcPoint, 
			final Vec3f vecDestPoint, 
			final int iNumberOfLines)
	{
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
	
	public void enableRendering(final boolean bEnableRendering) {
		
		this.bEnableRendering = bEnableRendering;
	}
}
