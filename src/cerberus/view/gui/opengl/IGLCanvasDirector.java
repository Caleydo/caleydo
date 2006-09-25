package cerberus.view.gui.opengl;

import java.util.Collection;

import javax.media.opengl.GLCanvas;


public interface IGLCanvasDirector
{

	public abstract void initView();

	public abstract void addGLCanvasUser(IGLCanvasUser user);

	public abstract void removeGLCanvasUser(IGLCanvasUser user);

	public abstract void removeAllGLCanvasUsers();

	public abstract Collection<IGLCanvasUser> getAllGLCanvasUsers();

	public abstract GLCanvas getGLCanvas();

}