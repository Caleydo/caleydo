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
	
	//public abstract GLAutoDrawable getGLDrawable();
	
	//public abstract void initGLCanvasUser(GL gl);	
	
	//public abstract void renderGLCanvasUser( GL gl ); 
	
	//public abstract void updateGLCanvasUser( GL gl );
	
//	public abstract void  reshapeGLCanvasUser(GL gl, 
//			final int x, final int y, 
//			final int width, final int height);
	
//	public abstract void displayGLChanged(GL gl, 
//			boolean modeChanged,
//			boolean deviceChanged);
		
	/**
	 * Cleanup internal data structures.
	 */
	public void destroyDirector();
	
	public JoglCanvasForwarder getJoglCanvasForwarder();
}