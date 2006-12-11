package cerberus.view.gui.opengl;

import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.data.IUniqueObject;

public interface IGLCanvasUser
extends IUniqueObject {

	public abstract void link2GLCanvasDirector( IGLCanvasDirector parentView );

	public abstract void init(GLAutoDrawable canvas);
	
	public abstract void render(GLAutoDrawable canvas);

	public void reshape(GLAutoDrawable drawable, 
			final int x,
			final int y,
			final int width,
			final int height);
	
	public abstract void update(GLAutoDrawable canvas);
	
	public abstract void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged);

	public abstract GLAutoDrawable getGLCanvas();

	public abstract IGLCanvasDirector getGLCanvasDirector();

	public abstract void destroy();
	
	public boolean isInitGLDone();
}