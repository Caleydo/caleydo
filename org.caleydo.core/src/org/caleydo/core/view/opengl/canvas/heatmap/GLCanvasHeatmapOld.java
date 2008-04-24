package org.caleydo.core.view.opengl.canvas.heatmap;

import java.util.ArrayList;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.GLParCoordsToolboxRenderer;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasHeatmapOld 
extends AGLCanvasUser {
	
	protected int[] iResolution;
	
	protected ISet targetSet;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasHeatmapOld(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
	}

	public void setResolution( int[] iResolution ) {
		this.iResolution = iResolution;
		
		if ( iResolution.length < 3 ) {
			throw new RuntimeException("GLCanvasHeatmap.setResolution() array must contain 3 items.");
		}
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			generalManager.getSingleton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			generalManager.getSingleton().logMsg(
					"GLCanvasObjectHeatmap.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!",
					LoggerType.FULL );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		//iGLDisplayListIndexLocal = gl.glGenLists(1);	
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{
//		toolboxRenderer = new GLParCoordsToolboxRenderer(generalManager,
//				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true);
//		
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		//iGLDisplayListIndexRemote = gl.glGenLists(1);	
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {
		
		pickingManager.handlePicking(iUniqueId, gl, true);
		
		display(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {
	
		display(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		
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
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType, org.caleydo.core.manager.view.EPickingMode, int, org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(final EPickingType ePickingType, 
			final EPickingMode ePickingMode, 
			final int iExternalID,
			final Pick pick)
	{
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("No info available!");
		return sAlInfo;
	}
}
