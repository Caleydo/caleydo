package cerberus.view.opengl;

import java.util.Collection;

import cerberus.view.jogl.JoglCanvasForwarder;

/**
 * 
 * @author Michael Kalkusch
 *
 */
public interface IGLCanvasDirector {
	
	public abstract void addGLCanvasUser(IGLCanvasUser user);

	public abstract void removeGLCanvasUser(IGLCanvasUser user);
	
	public abstract boolean containsGLCanvasUser(IGLCanvasUser user);

	public abstract void removeAllGLCanvasUsers();

	public abstract Collection<IGLCanvasUser> getAllGLCanvasUsers();
		
	/**
	 * Cleanup internal data structures.
	 */
	public void destroyDirector();
	
	public JoglCanvasForwarder getJoglCanvasForwarder();
}