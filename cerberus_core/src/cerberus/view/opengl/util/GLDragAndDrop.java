package cerberus.view.opengl.util;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cerberus.view.opengl.canvas.pathway.GLPathwayManager;
import cerberus.view.opengl.canvas.pathway.GLPathwayTextureManager;

import com.sun.opengl.util.texture.Texture;

/**
 * Object stores the pathway that is currently dragged
 * until it is dropped.
 * 
 * @author Marc Streit
 *
 */
public class GLDragAndDrop {

	private int iDragObjectId = -1;
	
	private boolean bDragActionRunning = false;
	
	private Texture dragThumbnailTexture;
	
	private float[] fArCurrentMousePos;
	
	private GLPathwayTextureManager pathwayTextureManager;
	
	public GLDragAndDrop(
			final GLPathwayTextureManager pathwayTextureManager) {
		
		this.pathwayTextureManager = pathwayTextureManager;
		
		fArCurrentMousePos = new float[2];
	}
	
	public int getDraggedObjectedId() {
		
		return iDragObjectId;
	}
	
	public void startDragAction(final int iDragObjectId,
			final Texture dragThumbnailTexture) {
		
		bDragActionRunning = true;
		this.iDragObjectId = iDragObjectId;
		this.dragThumbnailTexture = dragThumbnailTexture;
	}
	
	public void stopDragAction() {
		
		bDragActionRunning = false;
		iDragObjectId = -1;
	}
	
	public boolean isDragActionRunning() {
		
		return bDragActionRunning;
	}
	
	public void setDragThumbnailTexture(final Texture dragThumbnailTexture) {
		
		this.dragThumbnailTexture = dragThumbnailTexture;
	}
	
	public Texture getDragThumbnailTexture() {
		
		return dragThumbnailTexture;
	}
	
	public void setCurrentMousePos(final GL gl,
			final Point currentMousePos) {
	
		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0;// GL y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - currentMousePos.y - 1;

		GLU glu = new GLU();
		glu.gluUnProject((double) currentMousePos.x, (double) realy, 0.0, //
				mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		
		fArCurrentMousePos[0] = (float)wcoord[0];
		fArCurrentMousePos[1] = (float)wcoord[1];
	}
	
	public void renderDragThumbnailTexture(final GL gl) {
		
		gl.glPushMatrix();
		gl.glTranslatef(fArCurrentMousePos[0], fArCurrentMousePos[1], 5);
		gl.glScalef(0.2f, 0.2f, 0.2f);
		pathwayTextureManager.renderPathway(gl, 
				iDragObjectId, 0.5f, false);
		gl.glPopMatrix();
	}
	
    public void convertWindowCoordinatesToWorldCoordinates(final GL gl, 
    		final int iWindowCoordinatePositionX, final int iWindowCoordinatePositionY) {
    	
    }
}
