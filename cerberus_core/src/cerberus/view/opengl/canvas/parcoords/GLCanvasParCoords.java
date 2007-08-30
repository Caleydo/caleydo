package cerberus.view.opengl.canvas.parcoords;

import javax.media.opengl.GL;

import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.IGeneralManager;
import cerberus.view.opengl.canvas.AGLCanvasUser;

/**
 * 
 * 
 * @author Alexander Lex
 *
 */
public class GLCanvasParCoords extends AGLCanvasUser {

	private IGeneralManager refGeneralManager;
	
	public GLCanvasParCoords(IGeneralManager refGeneralManager,
			int viewId,
			int parentContainerId,
			String label) {

		super(refGeneralManager, null, viewId, parentContainerId, label);

		this.refViewCamera.setCaller(this);
		this.refGeneralManager = refGeneralManager;
		
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) {
		
		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(2.0f, 0.0f, 0.0f); // Set the color to red
		gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 2.0f, -1.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

	}

}
