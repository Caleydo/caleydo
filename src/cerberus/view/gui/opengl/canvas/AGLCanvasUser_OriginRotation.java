/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser;

/**
 * @author kalkusch
 *
 */
public abstract class AGLCanvasUser_OriginRotation 
extends AGLCanvasUser 
implements IGLCanvasUser
{
	
	protected Vec3f origin;
	
	protected Vec4f rotation;
	
	/**
	 * @param setGeneralManager
	 */
	public AGLCanvasUser_OriginRotation( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
	}

	public final void setOriginRotation( final Vec3f origin,	
		final Vec4f rotation ) {
		this.origin   = origin;
		this.rotation = rotation;
	}
	
	public final Vec3f getOrigin( ) {
		return this.origin;
	}
	
	public final Vec4f getRoation( ) {
		return this.rotation;
	}
		
	public final void render(GLAutoDrawable canvas)
	{
		GL gl = canvas.getGL();
		
		/* Clear The Screen And The Depth Buffer */
		gl.glPushMatrix();

		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
		gl.glRotatef( rotation.x(), 
				rotation.y(),
				rotation.z(),
				rotation.w() );
		
		this.renderPart( gl );

		gl.glPopMatrix();		
		
		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}
	
	public void reshape(GLAutoDrawable drawable, 
			final int x,
			final int y,
			final int width,
			final int height) {
		
		GL gl = canvas.getGL();
		
		System.err.println(" AGLCanvasUser_OriginRotation.reshape(GLCanvas canvas)");
		
		this.renderPart( gl );
	}
	
	public abstract void renderPart(GL gl);

//	public void update(GLAutoDrawable canvas)
//	{
//		// TODO Auto-generated method stub
//		System.err.println(" TestTriangle.update(GLCanvas canvas)");
//	}
//
//	public void destroy()
//	{
//		// TODO Auto-generated method stub
//		System.err.println(" TestTriangle.destroy(GLCanvas canvas)");
//	}

}
