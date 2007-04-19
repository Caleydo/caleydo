/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

import cerberus.data.collection.ISet;
import cerberus.manager.IGeneralManager;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasHeatmap 
extends AGLCanvasUser_OriginRotation 
//implements IGLCanvasUser
{
	
	protected int[] iResolution;
	
	protected ISet targetSet;
	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHeatmap( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
	}

	public void setResolution( int[] iResolution ) {
		this.iResolution = iResolution;
		
		if ( iResolution.length < 3 ) {
			throw new RuntimeException("GLCanvasHeatmap.setResolution() array must contain 3 items.");
		}
	}
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			refGeneralManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasObjectHeatmap.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!");
		}
	}
	

	
	@Override
	public void renderPart(GL gl)
	{
		
		if  ( targetSet != null ) 
		{
			
		}
		else 
		{
			
		}

		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to red
		gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 1.0f, 1.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(1.0f, 1.0f, 0.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

		float fX_inc = 1.0f / (float) iResolution[0];
		float fY_inc = 1.0f / (float) iResolution[1];
		
		float fX_init = 0.0f;
		
		float fX = fX_init;
		float fY = 0.0f;
		
		gl.glTranslatef(0.0f, 0.0f, -0.5f); // Move right 3 units	
		
		for ( int i=0; i < this.iResolution[0]; i++ ) {
			
			float fY_next = fY + fY_inc;
			
			for ( int j=0; j < this.iResolution[1]; j++ ) {
				
				float fX_next = fX + fX_inc;
				
				gl.glColor3f(fX * fY, 0.2f, 1 - fX); // Set the color to blue one time only
				
				gl.glBegin(GL.GL_QUADS); // Draw a quad
				gl.glVertex3f(fX, fY, 0.0f); // Top left
				gl.glVertex3f(fX_next, fY, 0.0f); // Top right
				gl.glVertex3f(fX_next, fY_next, 0.0f); // Bottom right
				gl.glVertex3f(fX, fY_next, 0.0f); // Bottom left
				gl.glEnd(); // Done drawing the quad
				
				fX = fX_next;
			}
			
			fX = fX_init;
			fY = fY_next;
		}
		
		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}

}
