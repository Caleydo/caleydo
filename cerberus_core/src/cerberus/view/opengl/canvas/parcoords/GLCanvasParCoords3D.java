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
public class GLCanvasParCoords3D extends AGLCanvasUser {

//	private IGeneralManager refGeneralManager;
	private float axisSpacing;
	
	public GLCanvasParCoords3D(IGeneralManager refGeneralManager,
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
		float[] graphData = {0.3f , 0.2f, 1.8f, 2.0f, 3.0f, 0.1f};
		float[] graphData2 = {0.1f, 0.4f, 1.5f, 2.3f, 1.6f, 0.5f};
		renderCoordinateSystem(gl, graphData.length, 3);
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		renderGraphs(gl, graphData);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		renderGraphs(gl, graphData2);		
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

		gl.glLineWidth(1.0f);
		gl.glBegin(GL.GL_LINES);	
		
		int count = 0;
		while (count <= numberParameters)
		{
			gl.glVertex3f(count * axisSpacing, 0.0f, 0.0f);
			gl.glVertex3f(count * axisSpacing, maxHeight, 0.0f);
			count++;
		}
		
		gl.glEnd();	
		
	}
	
	private void renderGraphs(GL gl, float[] graphData)
	{
		gl.glBegin(GL.GL_LINE_STRIP);
		
		int count = 0;
		while (count < graphData.length)
		{
			gl.glVertex3f(count * axisSpacing, graphData[count], 0.0f);
			
			count++;
		}
		
		gl.glEnd();	
		
	}
	

}
