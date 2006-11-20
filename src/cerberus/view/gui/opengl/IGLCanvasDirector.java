package cerberus.view.gui.opengl;

import java.util.Collection;

import javax.media.opengl.GLAutoDrawable;
// import javax.media.opengl.GLCanvas;


public interface IGLCanvasDirector {

	public abstract void initView();
	
	public abstract void addGLCanvasUser(IGLCanvasUser user);

	public abstract void removeGLCanvasUser(IGLCanvasUser user);

	public abstract void removeAllGLCanvasUsers();

	public abstract Collection<IGLCanvasUser> getAllGLCanvasUsers();

	public abstract GLAutoDrawable getGLCanvas();
	
	public abstract void initGLCanvasUser( GLAutoDrawable drawable );
	
	public abstract void renderGLCanvasUser( GLAutoDrawable drawable ); 
	
	public abstract void updateGLCanvasUser( GLAutoDrawable drawable );
	
	public void destroyDirector();

}