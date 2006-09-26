/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;

/**
 * @author kalkusch
 *
 */
public abstract class AGLCanvasUser 
extends AUniqueManagedObject 
implements IGLCanvasUser
{

	protected GLAutoDrawable canvas;
	
	protected IGLCanvasDirector openGLCanvasDirector;
	
	
	/**
	 * @param setGeneralManager
	 */
	protected AGLCanvasUser( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( iViewId, setGeneralManager );
		
		openGLCanvasDirector =
			setGeneralManager.getSingelton().getViewGLCanvasManager().getGLCanvasDirector( iParentContainerId );
		
		this.canvas = openGLCanvasDirector.getGLCanvas();
	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#link2GLCanvasDirector(cerberus.view.gui.opengl.IGLCanvasDirector)
	 */
	public final void link2GLCanvasDirector(IGLCanvasDirector parentView)
	{
		if ( openGLCanvasDirector == null ) {
			openGLCanvasDirector = parentView;
		}
		
		parentView.addGLCanvasUser( this );
	}


	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvas()
	 */
	public final GLAutoDrawable getGLCanvas()
	{
		return canvas;
	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvasDirector()
	 */
	public final IGLCanvasDirector getGLCanvasDirector()
	{
		return openGLCanvasDirector;
	}

	public final ManagerObjectType getBaseType()
	{
		return null;
	}

}
