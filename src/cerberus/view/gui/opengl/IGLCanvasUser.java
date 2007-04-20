package cerberus.view.gui.opengl;

import javax.media.opengl.GL;

import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.data.IUniqueObject;

public interface IGLCanvasUser
extends IUniqueObject {

	//public abstract void link2GLCanvasDirector( IGLCanvasDirector parentView );

//	public abstract void initGLCanvas(GLCanvas canvas);
	
	public abstract void initGLCanvas(GL gl);	
	
	/**
	 * Attention: Each IGLCanvasUser object has to take care that its initGLCanvas(GLCanvas canvas); method is called.
	 * The IGLCanvasDirector tries to call it once inside the initGLCanvasUser(), if the IGLCanvasUser is registered 
	 * by that time to the Vector <IGLCanvasUser> vecGLCanvasUser; and calls setInitGLDone(); afterwards.
	 * 
	 * Solution: Check if initGLCanvas(GL gl); was called prio to render(GL gl); 
	 * by calling boolean isInitGLDone();
	 * 
	 * @param canvas
	 */	
	public abstract void render(GL gl);
	

	public void reshape(GL gl, 
			final int x,
			final int y,
			final int width,
			final int height);
	
	public abstract void update(GL gl);
	
	public abstract void displayChanged(GL gl, 
			final boolean modeChanged, 
			final boolean deviceChanged);

	public abstract GL getGLCanvas();

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