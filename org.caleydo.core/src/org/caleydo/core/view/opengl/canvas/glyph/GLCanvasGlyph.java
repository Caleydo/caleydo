package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.GLParCoordsToolboxRenderer;
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(GL gl) 
	{

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */
	public void initLocal(GL gl) 
	{
		init(gl);
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
		GLSharedObjects.drawAxis(gl);
		GLSharedObjects.drawViewFrustum(gl, viewFrustum);
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
	}	
}
