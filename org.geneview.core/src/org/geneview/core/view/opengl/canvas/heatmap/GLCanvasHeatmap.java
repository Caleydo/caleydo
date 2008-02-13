package org.geneview.core.view.opengl.canvas.heatmap;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasHeatmap 
extends AGLCanvasUser {
	
	protected int[] iResolution;
	
	protected ISet targetSet;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasHeatmap(final IGeneralManager generalManager,
			int iViewID,
			int iGLCanvasID,
			String sLabel) {

		super(generalManager, iViewID, iGLCanvasID, sLabel);
	}

	public void setResolution( int[] iResolution ) {
		this.iResolution = iResolution;
		
		if ( iResolution.length < 3 ) {
			throw new RuntimeException("GLCanvasHeatmap.setResolution() array must contain 3 items.");
		}
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			generalManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			generalManager.getSingelton().logMsg(
					"GLCanvasObjectHeatmap.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!",
					LoggerType.FULL );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable drawable) {

		((GLEventListener)parentGLCanvas).init(drawable);
		
		final GL gl = drawable.getGL();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
		
		((GLEventListener)parentGLCanvas).display(drawable);
		
		final GL gl = drawable.getGL();	
		
		if  ( targetSet != null ) 
		{
			
		}
		else 
		{
			
		}

		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to red
		gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 1.0f, 1.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(1.0f, 1.0f, 0.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

		float fX_inc = 1.0f / (float) iResolution[0];
		float fY_inc = 1.0f / (float) iResolution[1];
		
		float fX_init = 0.0f;
		
		float fX = fX_init;
		float fY = 0.0f;
		
		gl.glTranslatef(0.0f, 0.0f, -0.5f); // Move right 3 units	
		
		for ( int i=0; i < this.iResolution[0]; i++ ) {
			
			float fY_next = fY + fY_inc;
			
			for ( int j=0; j < this.iResolution[1]; j++ ) {
				
				float fX_next = fX + fX_inc;
				
				gl.glColor3f(fX * fY, 0.2f, 1 - fX); // Set the color to blue one time only
				
				gl.glBegin(GL.GL_QUADS); // Draw a quad
				gl.glVertex3f(fX, fY, 0.0f); // Top left
				gl.glVertex3f(fX_next, fY, 0.0f); // Top right
				gl.glVertex3f(fX_next, fY_next, 0.0f); // Bottom right
				gl.glVertex3f(fX, fY_next, 0.0f); // Bottom left
				gl.glEnd(); // Done drawing the quad
				
				fX = fX_next;
			}
			
			fX = fX_init;
			fY = fY_next;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {

		((GLEventListener)parentGLCanvas).displayChanged(drawable, modeChanged, deviceChanged);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	
	}
}
