package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.GLParCoordsToolboxRenderer;

import org.caleydo.core.view.opengl.canvas.remote.glyph.GlyphMouseListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLSharedObjects;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * Rendering the Glyph View
 * 
 * @author Stefan Sauer
 *
 */

public class GLCanvasGlyph 
extends AGLCanvasUser
{	
	//private Vec4f vecRotation = new Vec4f(1, 1, 1, 0);
	//private Vec3f vecTranslation = new Vec3f(0,0,0);
	
	GLCanvasGlyphGrid grid_;
	int displayList_ = -1;

	private GlyphMouseListener mouseListener_ = null;
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iViewId
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLCanvasGlyph(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
		mouseListener_ = new GlyphMouseListener(this, generalManager);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(GL gl) 
	{
	      gl.glEnable    ( GL.GL_DEPTH_TEST);
	      
		grid_ = new GLCanvasGlyphGrid();
		grid_.buildGrid(gl);

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */
	public void initLocal(GL gl) 
	{
		init(gl);
		
		parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized mouse wheel listener
		parentGLCanvas.addMouseWheelListener(mouseListener_);
		
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener, org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */


	public void initRemote(final GL gl, 
			final int iRemoteViewID,
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas) 

	{
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;
	
		glToolboxRenderer = new GLParCoordsToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, null);
		
		init(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(GL gl) 
	{
		
		pickingManager.handlePicking(iUniqueId, gl, true);
	
		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(GL gl) 
	{		
		display(gl);
		checkForHits(gl);
//		pickingTriggerMouseAdapter.resetEvents();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(GL gl) 
	{
	    gl.glPushMatrix();
		gl.glMatrixMode(GL.GL_VIEWPORT);

		//rotate grid view
		gl.glTranslatef(0f, 0f, -10f);
		gl.glRotatef( 45f, 0,0,1 );
		gl.glRotatef( 45f, -1,1,0 );

		gl.glMatrixMode(GL.GL_MODELVIEW);
	    
		

		
		//draw helplines
		//GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		GLSharedObjects.drawAxis(gl);

		gl.glBegin(GL.GL_POLYGON);		
		
		//if(displayList_ < 0) {
	    {
			displayList_ = gl.glGenLists(1);
			gl.glNewList(displayList_, GL.GL_COMPILE);
			
			Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();
			
			while(git.hasNext()) {
				GlyphEntry e = git.next();
				Vec2i pos = grid_.getGridPosition(e.getX(), e.getY());
				
				
				gl.glTranslatef( (float)pos.x(), -(float)pos.y(), 0f);
				gl.glPushName(pickingManager.getPickingID(iUniqueId,
						EPickingType.FIELD_SELECTION, e.getID() ) );
				gl.glCallList(e.getGlList(gl));
				gl.glPopName();
				gl.glTranslatef(-(float)pos.x(),  (float)pos.y(), 0f);
			}
			gl.glEndList();
		}
		
		int g = grid_.getGridLayout();

		if(g != 0)
			gl.glCallList(g);
		
		if(displayList_ >= 0)
			gl.glCallList(displayList_);
		
		gl.glEnd();		
		
	    gl.glPopMatrix();
		
		if(mouseListener_ != null)
			mouseListener_.render();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Type: Glyph Map");
		alInfo.add("GL: Showing test clinical data");
		return alInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType, org.caleydo.core.manager.view.EPickingMode, int, org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) 
	{
		if(pickingType != EPickingType.FIELD_SELECTION)
			return;
		
		switch(pickingMode) {
			case CLICKED:
				GlyphEntry g = grid_.getGlyph(iExternalID);
				
				System.out.println("picked object index: " + Integer.toString(iExternalID) + " on Point " +  g.getX() + " " + g.getY() );
				
				grid_.deSelectAll();
				g.select();

				
				
				break;
			default:
				//System.out.println("picking Mode " + pickingMode.toString());
					
		}
		
		pickingManager.flushHits(iUniqueId, pickingType);
	}
	
	
	
	
}
