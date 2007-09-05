package cerberus.view.opengl.canvas.parcoords;

import javax.media.opengl.GL;

//import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.IGeneralManager;
import cerberus.view.opengl.canvas.AGLCanvasUser;

/**
 * 
 * 
 * @author Alexander Lex
 *
 */
public class GLCanvasParCoords extends AGLCanvasUser {

//	private IGeneralManager refGeneralManager;
	private float axisSpacing;
	
	public GLCanvasParCoords(IGeneralManager refGeneralManager,
			int viewId,
			int parentContainerId,
			String label) {

		super(refGeneralManager, null, viewId, parentContainerId, label);

		this.refViewCamera.setCaller(this);
		this.axisSpacing = 2;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
	}
	

	/*
	 * (non-Javadoc)
	 * @see cerberus.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) 
	{
		
		renderCoordinateSystem(gl, 4, 3);
	/*
		//int count = 0;
		//while(count < 100000)
		//{
			//gl.glPushMatrix();
			//gl.glRotatef(0.1f, 0.0f, 0.0f, 1.0f);
			gl.glPointSize(2.0f);
			gl.glColor3f(0.0f, 0.0f, 0.0f); // Set the color to red
			
			gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles			
			gl.glVertex3f(0.1f, 0.0f, 0.0f); // Top
			gl.glVertex3f(2.0f, 0.0f, 0.0f);
			
			//gl.glVertex3f(-0.1f, 0.0f, 0.0f);
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			//gl.glVertex3f(-4.0f, 0.0f, 0.0f);
			
			gl.glVertex3f(0.0f, 0.1f, 0.0f);
			gl.glVertex3f(0.0f, 4.0f, 0.0f);
			
			gl.glVertex3f(0.0f, -0.1f, 0.0f);
			gl.glVertex3f(0.0f, -4.0f, 0.0f);
			gl.glEnd();
			
			//gl.glColor3f(0.0f, 2.0f, -1.0f); // Set the color to green
			//gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
			//gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to blue
			//gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
			//gl.glColor3f(1.0f, 0.0f, 1.0f);
			//gl.glVertex3f(-2.0f, -2.0f, 0.0f);
			//gl.glPopMatrix();
			//count++;
		//}
	
	
		gl.glEnd(); // Finish drawing the triangle

		*/
	}
	
	private void renderCoordinateSystem(GL gl, int numberParameters, float maxHeight)
	{
		// draw X-Axis
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glLineWidth(3.0f);
				
		gl.glBegin(GL.GL_LINES);		
	
		gl.glVertex3f(-0.1f, 0.0f, 0.0f); 
		gl.glVertex3f((numberParameters * axisSpacing)+0.1f, 0.0f, 0.0f);
		
		//gl.glVertex3f(0.0f, 0.0f, 0.0f);
		//gl.glVertex3f(0.0f, maxHeight, 0.0f);			
		
		gl.glEnd();
		
		// draw all Y-Axis
		gl.glColor3f(0.0f, 2.0f, 0.0f);
		gl.glLineWidth(1.0f);
		gl.glBegin(GL.GL_LINES);	
		
		int count = 0;
		while (count <= numberParameters)
		{
			gl.glVertex3f(count*axisSpacing, 0.0f, 0.0f);
			gl.glVertex3f(count*axisSpacing, maxHeight, 0.0f);
			count++;
		}
		
		gl.glEnd();
	}
	
	private void renderGraphs(GL gl)
	{
		
		
	}
	

}
