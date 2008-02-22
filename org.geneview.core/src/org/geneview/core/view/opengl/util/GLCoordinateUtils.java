package org.geneview.core.view.opengl.util;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class GLCoordinateUtils 
{

	  public static float[] convertWindowCoordinatesToWorldCoordinates(final GL gl, 
	    		final int iWindowCoordinatePositionX, final int iWindowCoordinatePositionY) 
	  {
		  	float[] fArWorldCoordinatePosition = new float[3];
	    	
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
			realy = viewport[3] - iWindowCoordinatePositionY - 1;

//			System.out.println("Coordinates at cursor are (" + point.x + ", "
//					+ realy);
			
			GLU glu = new GLU();
			glu.gluUnProject((double) iWindowCoordinatePositionX, (double) realy, 0.0, //
					mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
			
//			System.out.println("World coords at z=0.0 are ( " //
//					+ wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]);
			
			if (fArWorldCoordinatePosition == null)
				fArWorldCoordinatePosition = new float[3];
			
			fArWorldCoordinatePosition[0] = (float)wcoord[0];
			fArWorldCoordinatePosition[1] = (float)wcoord[1];
			fArWorldCoordinatePosition[2] = (float)wcoord[2];
			
			return fArWorldCoordinatePosition;
	    }
	
}
