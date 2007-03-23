/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGLCanvasUser 
extends AUniqueManagedObject 
implements IGLCanvasUser
{

	protected GLAutoDrawable canvas;
	
	protected IGLCanvasDirector openGLCanvasDirector;
	
	private boolean bInitGLcanvawsWasCalled = false;
	
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
		
		assert openGLCanvasDirector != null : 
			"parent GLCanvas Director is null! Maybe parentID=" + 
			iParentContainerId + " in XML file is invalid.";
		
		this.canvas = openGLCanvasDirector.getGLCanvas();
		
		assert canvas != null : "canvas from parten ist null!";
	}

	public final boolean isInitGLDone() 
	{
		return this.bInitGLcanvawsWasCalled;
	}
	
	public final void setInitGLDone() 
	{
		if ( bInitGLcanvawsWasCalled ) {
			System.err.println(" called setInitGLDone() for more than once! " + 
					this.getClass().getSimpleName()  +
					" " + this.getId());
		}
		else 
		{
			System.err.println(" called setInitGLDone() " + 
					this.getClass().getSimpleName() + 
					" " + this.getId() );
		}
		bInitGLcanvawsWasCalled = true;
	}
	
//	/* (non-Javadoc)
//	 * @see cerberus.view.gui.opengl.IGLCanvasUser#link2GLCanvasDirector(cerberus.view.gui.opengl.IGLCanvasDirector)
//	 */
//	public final void link2GLCanvasDirector(IGLCanvasDirector parentView)
//	{
//		if ( openGLCanvasDirector == null ) {
//			openGLCanvasDirector = parentView;
//		}
//		
//		parentView.addGLCanvasUser( this );
//	}


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

	public void initGLCanvas(GLCanvas canvas)
	{
		setInitGLDone();
	}

	
	public abstract void renderPart(GL gl);
	
	
	public final void render(GLAutoDrawable canvas)
	{
		GL gl = canvas.getGL();
		
//		/* Clear The Screen And The Depth Buffer */
//		gl.glPushMatrix();

//		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
//		gl.glRotatef( rotation.x(), 
//				rotation.y(),
//				rotation.z(),
//				rotation.w() );
		
		this.renderPart( gl );

//		gl.glPopMatrix();		
//		
		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}
	
	public void reshape(GLAutoDrawable drawable, 
			final int x,
			final int y,
			final int width,
			final int height) {
		
		GL gl = drawable.getGL();
		
		System.out.println(" AGLCanvasUser_OriginRotation.reshape(GLCanvas canvas)");
		
		this.renderPart( gl );
	}
	
}
