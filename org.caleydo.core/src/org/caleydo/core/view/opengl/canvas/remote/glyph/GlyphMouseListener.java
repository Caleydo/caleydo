package org.caleydo.core.view.opengl.canvas.remote.glyph;

import gleem.linalg.Vec3f;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;

/**
 * Specialized mouse wheel listener for "diving" into the bucket.
 * 
 * @author Marc Streit
 *
 */
public class GlyphMouseListener 
implements MouseWheelListener {

	private IGeneralManager generalManager;
	
	private AGLCanvasUser viewCanvas;
	

	
	private int notches_ = 0;
	
	
	/**
	 * Constructor.
	 */
	public GlyphMouseListener(final AGLCanvasUser remoteRendering3D,
			final IGeneralManager generalManager) 
	{
		this.viewCanvas = remoteRendering3D;
		this.generalManager = generalManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent event) 
	{
		// Turn off picking while zoom action is running
		//generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(false);
		
	    int notches = event.getWheelRotation();
	    
	    System.out.println("Mouse Wheel: " + Integer.toString(notches) );
	    
	    notches_ += notches;
	    

	}
	
	public void render() 
	{
		
		viewCanvas.getViewCamera().addCameraScale(
	    		new Vec3f( 0, 0, notches_ / 1f));
		
		notches_ = 0;

				
	}
	
}
