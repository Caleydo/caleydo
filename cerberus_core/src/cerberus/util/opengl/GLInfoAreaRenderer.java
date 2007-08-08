package cerberus.util.opengl;

//import java.awt.Font;
import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import cerberus.data.pathway.element.PathwayVertex;
//import com.sun.opengl.*;
//import com.sun.opengl.util.j2d.TextRenderer;

public class GLInfoAreaRenderer {

	private Point point;
	
	private float fScaleFactor = 0.0f;
	
    public void drawPickedObjectInfo(final GL gl,
			final PathwayVertex pickedVertex) {

    	if (point == null)
    		return;
    	
    	if (fScaleFactor < 1.0)
    		fScaleFactor += 0.07;
    	
    	gl.glPushMatrix();
    	gl.glScalef(fScaleFactor, fScaleFactor, fScaleFactor);
    	
		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0;// GL y coord pos
		double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - (int) point.y - 1;

//		System.out.println("Coordinates at cursor are (" + point.x + ", "
//				+ realy);
		
		GLU glu = new GLU();
		glu.gluUnProject((double) point.x, (double) realy, 0.0, //
				mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		
//		System.out.println("World coords at z=0.0 are ( " //
//				+ wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]);

		float fZValue = -4f;
		
		gl.glLineWidth(2);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f((float)wcoord[0], (float)wcoord[1], fZValue);
		gl.glVertex3f((float)wcoord[0] + 0.5f, (float)wcoord[1] - 0.5f, fZValue);
		gl.glVertex3f((float)wcoord[0] + 0.5f, (float)wcoord[1] - 0.2f, fZValue);
		gl.glEnd();
		
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f((float)wcoord[0], (float)wcoord[1], fZValue);
		gl.glVertex3f((float)wcoord[0] + 0.5f, (float)wcoord[1] - 0.5f, fZValue);
		gl.glVertex3f((float)wcoord[0] + 0.5f, (float)wcoord[1] - 0.2f, fZValue);
		gl.glEnd();
		
		gl.glTranslatef(0.5f, -0.5f, 0.0f);
		
		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.8f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f((float)wcoord[0], (float)wcoord[1], fZValue);
		gl.glVertex3f((float)wcoord[0] + 1f, (float)wcoord[1], fZValue);
		gl.glVertex3f((float)wcoord[0] + 1f, (float)wcoord[1] + 0.3f, fZValue);
		gl.glVertex3f((float)wcoord[0], (float)wcoord[1] + 0.3f, fZValue);
		gl.glEnd();
		
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f((float)wcoord[0], (float)wcoord[1], fZValue);
		gl.glVertex3f((float)wcoord[0] + 1f, (float)wcoord[1], fZValue);
		gl.glVertex3f((float)wcoord[0] + 1f, (float)wcoord[1] + 0.3f, fZValue);
		gl.glVertex3f((float)wcoord[0], (float)wcoord[1] + 0.3f, fZValue);
		gl.glEnd();
		
//		TextRenderer textRenderer = new TextRenderer(
//				new Font("Arial", Font.BOLD, 12), true);
//		textRenderer.begin3DRendering();
//		textRenderer.setColor(1,1,1,1);
//		
//		textRenderer.draw3D("ID: " +pickedVertex.getElementTitle(), 
//				(float)wcoord[0] + 0.05f, (float)wcoord[1] + 0.1f, fZValue - 0.01f, 0.01f);
//		
//		textRenderer.end3DRendering();
		
		if (fScaleFactor < 0.8f)
		{
			gl.glPopMatrix();
			return;
		}
		
		gl.glColor3f(1, 1, 1);
		GLTextUtils.renderText(gl, "ID: " +pickedVertex.getElementTitle(), 12,
				(float) wcoord[0] + 0.05f, (float) wcoord[1] + 0.1f, fZValue - 0.01f);

		
		float fLineHeight = 0.1f;
		gl.glTranslatef(0.0f, fLineHeight, 0.0f);
		
		GLTextUtils.renderText(gl, "Name: " +pickedVertex.getVertexRepByIndex(0).getName(), 12,
				(float) wcoord[0] + 0.05f, (float) wcoord[1] + 0.1f, fZValue - 0.01f);
		

		gl.glPopMatrix();
	}
    
    public void setPoint(final Point point) {
    	
    	this.point = point;
    }
    
    public final Point getPoint() {
    	
    	return point;
    }
    
    public void resetAnimation() {

    	fScaleFactor = 0.0f;
    }
}
