/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasTestTriangle 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser
{
	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasTestTriangle( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
	}

	
	@Override
	public void renderPart(GL gl)
	{
		
		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(2.0f, 0.0f, 0.0f); // Set the color to red
		gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 2.0f, -1.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

		gl.glTranslatef(0.0f, 0.0f, -1.0f); // Move right 3 units
		gl.glColor3f(0.5f, 0.5f, 1.0f); // Set the color to blue one time only
		gl.glBegin(GL.GL_QUADS); // Draw a quad
		gl.glVertex3f(-1.0f, 1.0f, 0.0f); // Top left
		gl.glVertex3f(1.0f, 1.0f, 0.0f); // Top right
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glEnd(); // Done drawing the quad

		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}

	public void update(GLAutoDrawable canvas)
	{
		// TODO Auto-generated method stub
		System.err.println(" TestTriangle.update(GLCanvas canvas)");
	}

	public void destroy()
	{
		// TODO Auto-generated method stub
		System.err.println(" TestTriangle.destroy(GLCanvas canvas)");
	}
	
	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
}
