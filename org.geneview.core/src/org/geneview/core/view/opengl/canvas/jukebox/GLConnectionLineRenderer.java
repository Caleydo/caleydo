package org.geneview.core.view.opengl.canvas.jukebox;

import java.util.ArrayList;
import java.util.Iterator;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

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
 *
 */
public class GLConnectionLineRenderer {
	
	private JukeboxHierarchyLayer underInteractionLayer;
	private JukeboxHierarchyLayer stackLayer;
	private JukeboxHierarchyLayer poolLayer;
	
	private SelectionManager selectionManager;
	
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

		if (selectionManager.getAllSelectedElements().size() == 0)
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

							float fXCoordSrc = selectedElementRepSrc.getXCoord() * GLPathwayManager.SCALING_FACTOR_X;
							float fYCoordSrc = selectedElementRepSrc.getYCoord() * GLPathwayManager.SCALING_FACTOR_Y;
							float fXCoordDest = selectedElementRepDest.getXCoord() * GLPathwayManager.SCALING_FACTOR_X;
							float fYCoordDest = selectedElementRepDest.getYCoord() * GLPathwayManager.SCALING_FACTOR_Y;
							
							vecMatSrc.set(fXCoordSrc, fYCoordSrc, 0);
							vecMatDest.set(fXCoordDest, fYCoordDest, 0);
							
							rotSrc.toMatrix(matSrc);
							rotDest.toMatrix(matDest);

							matSrc.xformPt(vecMatSrc, vecTransformedSrc);
							matDest.xformPt(vecMatDest, vecTransformedDest);

							vecTransformedSrc.componentMul(vecScaleSrc);
							vecTransformedSrc.add(vecTranslationSrc);
							vecTransformedDest.componentMul(vecScaleDest);
							vecTransformedDest.add(vecTranslationDest);
							
							gl.glLineWidth(4);
							Vec3f tmpLineColor = new PathwayRenderStyle().getLayerConnectionLinesColor();
							gl.glColor4f(tmpLineColor.x(), tmpLineColor.y(), tmpLineColor.z(), 1);
						
							
							gl.glBegin(GL.GL_LINES);
							gl.glVertex3f(vecTransformedSrc.x(),
									vecTransformedSrc.y(),
									vecTransformedSrc.z());
							gl.glVertex3f(vecTransformedDest.x(),
									vecTransformedDest.y(),
									vecTransformedDest.z());
							gl.glEnd();	
						}
					}
				}
			}
			
		}
		
		gl.glLineWidth(1);
	}
}
