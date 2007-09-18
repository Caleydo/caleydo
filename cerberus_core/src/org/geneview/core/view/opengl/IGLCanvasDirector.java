package org.geneview.core.view.opengl;

import java.util.Collection;

import org.geneview.core.view.jogl.JoglCanvasForwarder;
import org.geneview.core.view.jogl.TriggeredAnimator;

/**
 * 
 * @see org.geneview.core.view.opengl.IGLCanvasUser
 * @see org.geneview.core.view.jogl.JoglCanvasForwarder
 * 
 * @author Michael Kalkusch
 *
 */
public interface IGLCanvasDirector {
	
	/**
	 * Forwards calls to GLCanvas object.
	 * 
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder#addGLCanvasUser(IGLCanvasUser)
	 * 
	 * @param user
	 */
	public abstract void addGLCanvasUser(IGLCanvasUser user);

	/**
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder#removeGLCanvasUser(IGLCanvasUser)
	 * 
	 * @param user
	 */
	public abstract void removeGLCanvasUser(IGLCanvasUser user);
	
	/**
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder#containsGLCanvasUser(IGLCanvasUser)
	 * 
	 * @param user
	 * @return
	 */
	public abstract boolean containsGLCanvasUser(IGLCanvasUser user);

	/**
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder#removeAllGLCanvasUsers()
	 */
	public abstract void removeAllGLCanvasUsers();

	/**
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder#getAllGLCanvasUsers()
	 * 
	 * @return
	 */
	public abstract Collection<IGLCanvasUser> getAllGLCanvasUsers();
		
	/**
	 * Cleanup internal data structures.
	 */
	public void destroyDirector();
	
	/**
	 * Expose JoglCanvasForwarder for internal GLCanvas
	 * 
	 * @return forwarder for GLCanvas objects
	 */
	public JoglCanvasForwarder getJoglCanvasForwarder();
	
	/**
	 * Expose Animator for internal GLCanvas
	 *
	 * @see com.sun.opengl.util.Animator
	 * 
	 * @return Animator for internal GLCanvas
	 */
	public TriggeredAnimator getAnimator();
	
	/**
	 * Expose Animator for internal GLCanvas.
	 * Note: not all implementations allow setting the Animator externally
	 *
	 * @see com.sun.opengl.util.Animator
	 * 
	 * @return Animator for internal GLCanvas
	 */
	public void setAnimator( TriggeredAnimator setTriggeredAnimator );
	
	/**
	 * Get id for GL EventListener
	 * 
	 * @see org.geneview.core.view.jogl.JoglCanvasForwarder#getGlEventListernerId()
	 * 
	 * @return
	 */
	public int getGlEventListernerId();
}