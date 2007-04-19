package cerberus.view.gui.opengl;

import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import cerberus.view.gui.jogl.JoglCanvasForwarder;

public interface IGLCanvasDirector {
	
	public abstract void addGLCanvasUser(IGLCanvasUser user);

	public abstract void removeGLCanvasUser(IGLCanvasUser user);

	public abstract void removeAllGLCanvasUsers();

	public abstract Collection<IGLCanvasUser> getAllGLCanvasUsers();

	public abstract GLAutoDrawable getGLCanvas();
	
	public abstract void initGLCanvasUser(GL gl);
	
	public abstract void renderGLCanvasUser_GL( GL gl ); 
	
	public abstract void updateGLCanvasUser( GL gl );
	
	public abstract void  reshapeGLCanvasUser(GL gl, 
			final int x, final int y, 
			final int width, final int height);
	
	public abstract void displayGLChanged(GL gl, 
			boolean modeChanged,
			boolean deviceChanged);
		
	public void destroyDirector();
	
	public JoglCanvasForwarder getJoglCanvasForwarder();
}