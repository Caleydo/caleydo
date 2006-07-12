package cerberus.view.swing.multicanvas;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import cerberus.view.swing.multicanvas.JoglCanvasItem;

public class JoglCanvasPlane implements JoglCanvasItem {

	private float w;
	private float h;
	private float z;
	private int rows;
	private int cols;
	
	public JoglCanvasPlane() {
		
	}

	public void setParameters( float w, float h, float z, int rows, int cols) {
		this.w = w;
		this.h = h;
		this.rows = rows;
		this.cols = cols;
		this.z = z;
	}
	
	public void displayCanvas( GL gl ) {

	    displayCanvas_Plane( gl );
	}
	
	protected void displayCanvas_Plane( GL gl ) {
		
		 // draw square subdivided into quad strips
		    int x, y;
		    float vx, vy, s, t;
		    float ts, tt, tw, th;

		    ts = 1.0f / cols;
		    tt = 1.0f / rows;

		    tw = w / cols;
		    th = h / rows;

		    gl.glNormal3f(0.0f, 0.0f, 1.0f);

		    for(y=0; y<rows; y++) {
		      gl.glBegin(GL.GL_QUAD_STRIP);
		      for(x=0; x<=cols; x++) {
		        vx = tw * x -(w/2.0f);
		        vy = th * y -(h/2.0f);
		        s = ts * x;
		        t = tt * y;

		        gl.glTexCoord2f(s, t);
		        gl.glColor3f(s, t, z);
		        gl.glVertex3f(vx, vy,z );

		        gl.glColor3f(s, t + tt, z);
		        gl.glTexCoord2f(s, t + tt);
		        gl.glVertex3f(vx, vy + th, z);
		      }
		      gl.glEnd();
		    }
		    
		    System.out.println("draw panel..." + w + " / " + h);
		}
}
