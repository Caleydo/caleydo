/**
 * 
 */
package cerberus.view.gui.opengl;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.opengl.IGLCanvasDirector;

/**
 * @author java
 *
 */
public class GLObjectTriangleTest 
extends AUniqueManagedObject
implements IGLCanvasUser
{

	protected GLAutoDrawable refCanvas;
	
	protected IGLCanvasDirector refParentView;
	
	/**
	 * 
	 */
	public GLObjectTriangleTest( final int iUniqueId, 
			final IGeneralManager setGeneralManager)
	{
		super( iUniqueId, setGeneralManager);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#link2GLCanvas(javax.media.opengl.GLCanvas, java.lang.Object)
	 */
	public void link2GLCanvasDirector( IGLCanvasDirector parentView ) {
		this.refParentView = parentView;
	}


	public final IGLCanvasDirector getGLCanvasDirector() {
		return refParentView;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init( GLAutoDrawable canvas ) {

	}	
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#render(javax.media.opengl.GLCanvas)
	 */
	public void render( GLAutoDrawable canvas ) {
		this.refCanvas = canvas;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#update(javax.media.opengl.GLCanvas)
	 */
	public void update( GLAutoDrawable canvas ) {
		this.refCanvas = canvas;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvas()
	 */
	public final GLAutoDrawable getGLCanvas() {
		return refCanvas;
	}
	
	public void destroy() {
		this.refCanvas = null;
		this.refParentView = null;
	}

	public ManagerObjectType getBaseType()
	{
		return null;
	}

	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
	
}
