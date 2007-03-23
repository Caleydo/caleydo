/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

//import javax.media.opengl.GL;
//import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import cerberus.data.view.camera.IViewCamera;
import cerberus.data.view.camera.ViewCameraBase;
import cerberus.manager.IGeneralManager;
//import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGLCanvasUser_OriginRotation 
extends AGLCanvasUser 
//implements IGLCanvasUser
{
	
	
	protected IViewCamera refLocalViewCamera;
	
	/**
	 * @param setGeneralManager
	 */
	public AGLCanvasUser_OriginRotation( final IGeneralManager setGeneralManager,
			final IViewCamera refViewCamera,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		assert refViewCamera != null : "Can not handle null pointer!";
		
		refLocalViewCamera = refViewCamera;
	}

	public AGLCanvasUser_OriginRotation( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		this(setGeneralManager,
				new ViewCameraBase(),
				iViewId, 
				iParentContainerId,
				sLabel );
	}
	
	
	public final void setOriginRotation( final Vec3f origin,	
		final Vec4f rotation ) {
		
		this.refLocalViewCamera.setCameraPosition(origin);
		this.refLocalViewCamera.setCameraRotation( 
				new Rotf(rotation.w(), 
						rotation.x(),rotation.y(),rotation.z()) );
	}
		
//	public final void render(GLAutoDrawable canvas)
//	{
//		GL gl = canvas.getGL();
//		
//		/* Clear The Screen And The Depth Buffer */
//		gl.glPushMatrix();
//
////		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
////		gl.glRotatef( rotation.x(), 
////				rotation.y(),
////				rotation.z(),
////				rotation.w() );
//		
//		this.renderPart( gl );
//
//		gl.glPopMatrix();		
//		
//		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
//	}
//	
//	public void reshape(GLAutoDrawable drawable, 
//			final int x,
//			final int y,
//			final int width,
//			final int height) {
//		
//		GL gl = drawable.getGL();
//		
//		System.out.println(" AGLCanvasUser_OriginRotation.reshape(GLCanvas canvas)");
//		
//		this.renderPart( gl );
//	}
//	
//	public abstract void renderPart(GL gl);

	
	/**
	 * @return the refLocalViewCamera
	 */
	public final IViewCamera getRefLocalViewCamera() {
	
		return refLocalViewCamera;
	}

	
	/**
	 * @param refLocalViewCamera the refLocalViewCamera to set
	 */
	public final void setRefLocalViewCamera(IViewCamera refLocalViewCamera) {
	
		this.refLocalViewCamera = refLocalViewCamera;
	}

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
