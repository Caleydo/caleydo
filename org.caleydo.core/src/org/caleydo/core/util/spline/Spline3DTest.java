package org.caleydo.core.util.spline;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL;

/**
 * 
 * Simple test of a 3D spline.
 * 
 * @author Marc Streit
 *
 */
public class Spline3DTest
{
	private Spline3D spline;
	
	public void init(final GL gl)
	{
		//SPLINE TEST
	    Vec3f[] vecs = new Vec3f[4];
	    vecs[0] = new Vec3f(1.0f, 2.0f, 1.0f);
	    vecs[1] = new Vec3f(2.0f, 0.0f, 2.0f);
	    vecs[2] = new Vec3f(0.0f, 1.0f, 0.0f);
	    vecs[3] = new Vec3f(3.0f, -1.0f, -3.0f);

	    float accuracy = 0.001f;
	    float margin = 0.01f;
	    spline = new Spline3D(vecs, accuracy, margin);
	}
	
	public void display(final GL gl)
	{
		gl.glClearColor(1, 1, 1, 1);
		
		gl.glLineWidth(4);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		
		for (int i=0; i<300*2; i++)
		{
			Vec3f vec = spline.getPositionAt((float)i / 100*2);
			gl.glVertex3f(vec.x(), vec.y(), vec.z());			
		}
		gl.glEnd();
		
		
//	    Vec3f pos = spline.getPositionAtDistance(...); // for moving along the spline
//	    Vec3f pos = spline.getPositionAt(...); // 0.0F --> "spline.pointCount()"   -  for drawing the spline
	}
}
