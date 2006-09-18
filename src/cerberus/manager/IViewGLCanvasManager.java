/**
 * 
 */
package cerberus.manager;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;

/**
 * Make Jogl GLCanvas adressable by id and provide ground for XML bootstraping of 
 * GLCanvas & GLEventListener.
 * 
 * @author kalkusch
 *
 */
public interface IViewGLCanvasManager extends IGeneralManager, IViewManager
{

	public GLCanvas getGLCanvas( int iId );

	public boolean registerGLCanvas( final GLCanvas canvas, final int iCanvasId );
	
	public boolean unregisterGLCanvas( final GLCanvas canvas );
	
	public GLEventListener getGLEventListener( int iId );
	
	public boolean registerGLEventListener( final GLEventListener canvasListener, 
			final int iId );
	
	public boolean unregisterGLEventListener( final GLEventListener canvasListener );
	
	public boolean addGLEventListener2GLCanvasById( final int iCanvasListenerId, 
			final int iCanvasId );
	
	public boolean removeGLEventListener2GLCanvasById(final int iCanvasListenerId, 
			final int iCanvasId);
	
}
