/**
 * 
 */
package cerberus.manager;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * Make Jogl GLCanvas adressable by id and provide ground for XML bootstraping of 
 * GLCanvas & GLEventListener.
 * 
 * @author kalkusch
 *
 */
public interface IViewGLCanvasManager 
extends IGeneralManager, IViewManager {
	
	public IGLCanvasUser createGLCanvasUser(final CommandQueueSaxType useViewType, 
			int iViewId, int iParentContainerId, String sLabel);
	
	public GLCanvas getGLCanvas( int iId );

	public boolean registerGLCanvas( final GLCanvas canvas, final int iCanvasId );
	
	public boolean unregisterGLCanvas( final GLCanvas canvas );
	
	public boolean registerGLCanvasUser( final IGLCanvasUser canvas, 
			final int iCanvasId );
	
	public boolean unregisterGLCanvasUser( final IGLCanvasUser canvas);
	
	public GLEventListener getGLEventListener( int iId );
	
	public boolean registerGLEventListener( final GLEventListener canvasListener, 
			final int iId );
	
	public boolean unregisterGLEventListener( final GLEventListener canvasListener );
	
	public boolean addGLEventListener2GLCanvasById( final int iCanvasListenerId, 
			final int iCanvasId );
	
	public boolean removeGLEventListener2GLCanvasById(final int iCanvasListenerId, 
			final int iCanvasId);
	
	public IGLCanvasDirector getGLCanvasDirector( int iId );
	
	public boolean registerGLCanvasDirector( final IGLCanvasDirector director, final int iId );
	
	public boolean unregisterGLCanvasDirector( final IGLCanvasDirector director );
}
