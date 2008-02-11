package org.geneview.core.view.opengl.canvas.jukebox;

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
		
		renderConnectionLinesByElementID(gl,
				(Integer)selectionManager.getAllSelectedElements().toArray()[0]);
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
		
		vecTranslationSrc = stackLayer.getTransformByPositionIndex(1).getTranslation();
		vecScaleSrc = stackLayer.getTransformByPositionIndex(1).getScale();
		rotSrc = stackLayer.getTransformByPositionIndex(1).getRotation();

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
	
	private void renderConnectionLinesByElementID(final GL gl,
			final int iElementID) {
		
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
			
		vecTranslationSrc = stackLayer.getTransformByPositionIndex(1).getTranslation();
		vecScaleSrc = stackLayer.getTransformByPositionIndex(1).getScale();
		rotSrc = stackLayer.getTransformByPositionIndex(1).getRotation();

		vecTranslationDest = underInteractionLayer.getTransformByPositionIndex(0).getTranslation();
		vecScaleDest = underInteractionLayer.getTransformByPositionIndex(0).getScale();
		rotDest = underInteractionLayer.getTransformByPositionIndex(0).getRotation();
				
		Iterator<SelectedElementRep> iterSelectedElementReps = 
			selectionManager.getSelectedElementRepsByElementID(iElementID).iterator();
		
//		while(iterSelectedElementReps.hasNext()) {
			
			SelectedElementRep selectedElementRepSrc; 
			if (iterSelectedElementReps.hasNext())
				selectedElementRepSrc = iterSelectedElementReps.next();
			else
				return;
			
			SelectedElementRep selectedElementRepDest;
			if (iterSelectedElementReps.hasNext())
				selectedElementRepDest = iterSelectedElementReps.next();
			else
				return;
			
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
//		}
	
		gl.glLineWidth(1);
	}
}
