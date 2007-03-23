package cerberus.view.gui.opengl;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;

import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.data.IUniqueObject;

public interface IGLCanvasUser
extends IUniqueObject {

	//public abstract void link2GLCanvasDirector( IGLCanvasDirector parentView );

	public abstract void initGLCanvas(GLCanvas canvas);
	
	/**
	 * Attention: Each IGLCanvasUser object has to take care that its initGLCanvas(GLCanvas canvas); method is called.
	 * The IGLCanvasDirector tries to call it once inside the initGLCanvasUser(), if the IGLCanvasUser is registered 
	 * by that time to the Vector <IGLCanvasUser> vecGLCanvasUser; and calls setInitGLDone(); afterwards.
	 * 
	 * Solution: Check if initGLCanvas(GLCanvas canvas); was called prio to render(GLAutoDrawable canvas); 
	 * by calling boolean isInitGLDone();
	 * 
	 * @param canvas
	 */
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

	public abstract void destroyGLCanvas();
	
	/**
	 * 
	 * @see cerberus.view.gui.opengl.IGLCanvasDirector#initGLCanvasUser()
	 */
	public abstract void setInitGLDone();
	
	/**
	 * Should be used by void render(GLAutoDrawable canvas); to test if initGLCanvas was done.
	 * 
	 * @return
	 */
	public boolean isInitGLDone();
}