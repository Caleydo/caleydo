package cerberus.view.gui.opengl;

import javax.media.opengl.GLCanvas;

import cerberus.view.gui.opengl.IGLCanvasDirector;

public interface IGLCanvasUser
{

	public abstract void link2GLCanvasDirector( IGLCanvasDirector parentView );

	public abstract void render(GLCanvas canvas);

	public abstract void update(GLCanvas canvas);

	public abstract GLCanvas getGLCanvas();

	public abstract IGLCanvasDirector getGLCanvasDirector();

	public abstract void destroy();
}