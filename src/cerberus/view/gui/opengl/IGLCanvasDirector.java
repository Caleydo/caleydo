package cerberus.view.gui.opengl;

import java.util.Collection;

import javax.media.opengl.GLAutoDrawable;

public interface IGLCanvasDirector {

	public abstract void initView();
	
	public abstract void addGLCanvasUser(IGLCanvasUser user);

	public abstract void removeGLCanvasUser(IGLCanvasUser user);

	public abstract void removeAllGLCanvasUsers();

	public abstract Collection<IGLCanvasUser> getAllGLCanvasUsers();

	public abstract GLAutoDrawable getGLCanvas();
	
	public abstract void initGLCanvasUser();
	
	public abstract void renderGLCanvasUser( GLAutoDrawable drawable ); 
	
	public abstract void updateGLCanvasUser( GLAutoDrawable drawable );
	
	public abstract void  reshapeGLCanvasUser(GLAutoDrawable drawable, 
			final int x, final int y, 
			final int width, final int height);
	
	public abstract void displayGLChanged(GLAutoDrawable drawable, 
			boolean modeChanged,
			boolean deviceChanged);
		
	public void destroyDirector();
}