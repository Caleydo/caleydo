package cerberus.view.gui.swt.jogl;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

public interface ISwtJoglContainerViewRep
{

	/**
	 * Get the JOGL animator.
	 * 
	 * @see com.sun.opengl.util.Animator
	 *  
	 * @return animator current Animator
	 */
	public abstract Animator getAnimator();

	/**
	 * ISet the JOGL animator.
	 * 
	 * @see com.sun.opengl.util.Animator
	 *  
	 * @param setAnimator set current Animator
	 * 
	 * @return TRUE if Animator was scuessfully set.
	 */
	public abstract boolean setAnimator(final Animator setAnimator);

	/**
	 * Add a GLEventListener to the GLCanvas.
	 * 
	 * @see javax.media.opengl.GLCanvas#addGLEventListener(GLEventListener)
	 * 
	 * @param listener to be added
	 */
	public abstract void addGLEventListener(
			final GLEventListener setGLEventListener);

	/**
	 * Removes a GLEventListener from the GLCanvas.
	 *  
	 * @see javax.media.opengl.GLCanvas#removeGLEventListener(GLEventListener)
	 * 
	 * @param setGLEventListener listener to be removed
	 */
	public abstract void removeGLEventListener(
			final GLEventListener setGLEventListener);

	public GLEventListener getGLEventListener();

	
	public abstract boolean setGLCanvas(final GLCanvas setGLCanvas);

	public abstract GLCanvas getGLCanvas();

}