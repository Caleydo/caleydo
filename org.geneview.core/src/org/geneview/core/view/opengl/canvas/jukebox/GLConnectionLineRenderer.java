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
	
	public void renderTestViewConnectionLines(final GL gl) {
		
		Vec3f vecMatSrc = new Vec3f(0, 0, 0);
		Vec3f vecMatDest = new Vec3f(0, 0, 0);
		Vec3f vecTransformedSrc = new Vec3f(0, 0, 0);
		Vec3f vecTransformedDest = new Vec3f(0, 0, 0);
		
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
		
		gl.glLineWidth(4);
		Vec3f tmpLineColor = new PathwayRenderStyle().getLayerConnectionLinesColor();
		gl.glColor4f(tmpLineColor.x(), tmpLineColor.y(), tmpLineColor.z(), 1);
		
		vecTranslationSrc = underInteractionLayer.getTransformByPositionIndex(1).getTranslation();
		vecScaleSrc = underInteractionLayer.getTransformByPositionIndex(1).getScale();
		rotSrc = underInteractionLayer.getTransformByPositionIndex(1).getRotation();

		vecTranslationDest = underInteractionLayer.getTransformByPositionIndex(0).getTranslation();
		vecScaleDest = underInteractionLayer.getTransformByPositionIndex(0).getScale();
		rotDest = underInteractionLayer.getTransformByPositionIndex(0).getRotation();
				
		vecMatSrc.set(1 * GLPathwayManager.SCALING_FACTOR_X, 1 * GLPathwayManager.SCALING_FACTOR_Y, 0);
		vecMatDest.set(1 * GLPathwayManager.SCALING_FACTOR_X, 1 * GLPathwayManager.SCALING_FACTOR_Y, 0);
		
		rotSrc.toMatrix(matSrc);
		rotDest.toMatrix(matDest);

		matSrc.xformPt(vecMatSrc, vecTransformedSrc);
		matDest.xformPt(vecMatDest, vecTransformedDest);

		vecTransformedSrc.componentMul(vecScaleSrc);
		vecTransformedSrc.add(vecTranslationSrc);
		vecTransformedDest.componentMul(vecScaleDest);
		vecTransformedDest.add(vecTranslationDest);
		
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTransformedSrc.x(),
				vecTransformedSrc.y(),
				vecTransformedSrc.z());
		gl.glVertex3f(vecTransformedDest.x(),
				vecTransformedDest.y(),
				vecTransformedDest.z());
		gl.glEnd();		
		
		gl.glLineWidth(1);
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
							
							ArrayList<Vec3f> alSrcPoints = selectedElementRepSrc.getPoints();
							ArrayList<Vec3f> alSrcPointsTransformed = new ArrayList<Vec3f>();
							
							for (Vec3f vecCurrentPoint : alSrcPoints)
							{
								alSrcPointsTransformed.add(transform(vecCurrentPoint, vecTranslationSrc, vecScaleSrc, rotSrc));
							}
							
							ArrayList<Vec3f> alDestPoints = selectedElementRepDest.getPoints();
							ArrayList<Vec3f> alDestPointsTransformed = new ArrayList<Vec3f>();
							for (Vec3f vecCurrentPoint : alDestPoints)
							{
								alDestPointsTransformed.add(transform(vecCurrentPoint, vecTranslationDest, vecScaleDest, rotDest));
							}				
							
					
							renderConnection(gl, alSrcPointsTransformed, alDestPointsTransformed);
					
						}
					}
				}
			}
			
		}
		
		gl.glLineWidth(1);
	}
	
	private void renderConnection(final GL gl, final ArrayList<Vec3f> alSrcPoints, final ArrayList<Vec3f> alDestPoints)
	{
		gl.glLineWidth(4);
		Vec3f tmpLineColor = new PathwayRenderStyle().getLayerConnectionLinesColor();
		gl.glColor4f(tmpLineColor.x(), tmpLineColor.y(), tmpLineColor.z(), 1);
	
		if(alSrcPoints.size() == 1 && alDestPoints.size() == 1)
		{
			Vec3f vecSrcPoint = alSrcPoints.get(0);
			Vec3f vecDestPoint = alDestPoints.get(0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(vecSrcPoint.x(),
					vecSrcPoint.y(),
					vecSrcPoint.z());
			gl.glVertex3f(vecDestPoint.x(),
					vecDestPoint.y(),
					vecDestPoint.z());
			gl.glEnd();	
			return;
		}		
		
		ArrayList<Vec3f> alPoints;
		Vec3f vecPoint;
		if(alSrcPoints.size() == 1 && alDestPoints.size() > 1)
		{
			alPoints = alDestPoints;
			vecPoint = alSrcPoints.get(0);	
			renderPlanes(gl, vecPoint, alPoints);
		}
		else if(alSrcPoints.size() > 1 && alDestPoints.size() == 1)
		{
			alPoints = alSrcPoints;
			vecPoint = alDestPoints.get(0);
			renderPlanes(gl, vecPoint, alPoints);
		}
	
	}
	
	private void renderPlanes(final GL gl, final Vec3f vecPoint, final ArrayList<Vec3f> alPoints)
	{
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_COLOR, 0);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
		for(Vec3f vecCurrent : alPoints)
		{
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		gl.glEnd();
		
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_AREA_LINE_COLOR, 0);
		gl.glBegin(GL.GL_LINES);		
		for(Vec3f vecCurrent : alPoints)
		{
			gl.glVertex3f(vecPoint.x(), vecPoint.y(), vecPoint.z());
			gl.glVertex3f(vecCurrent.x(), vecCurrent.y(), vecCurrent.z());
		}
		gl.glEnd();
		
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
