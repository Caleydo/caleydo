/*
 * Portions Copyright (C) 2003 Sun Microsystems, Inc.
 * All rights reserved.
 */

/*
 *
 * COPYRIGHT NVIDIA CORPORATION 2003. ALL RIGHTS RESERVED.
 * BY ACCESSING OR USING THIS SOFTWARE, YOU AGREE TO:
 *
 *  1) ACKNOWLEDGE NVIDIA'S EXCLUSIVE OWNERSHIP OF ALL RIGHTS
 *     IN AND TO THE SOFTWARE;
 *
 *  2) NOT MAKE OR DISTRIBUTE COPIES OF THE SOFTWARE WITHOUT
 *     INCLUDING THIS NOTICE AND AGREEMENT;
 *
 *  3) ACKNOWLEDGE THAT TO THE MAXIMUM EXTENT PERMITTED BY
 *     APPLICABLE LAW, THIS SOFTWARE IS PROVIDED *AS IS* AND
 *     THAT NVIDIA AND ITS SUPPLIERS DISCLAIM ALL WARRANTIES,
 *     EITHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED
 *     TO, IMPLIED WARRANTIES OF MERCHANTABILITY  AND FITNESS
 *     FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL NVIDIA OR ITS SUPPLIERS BE LIABLE FOR ANY
 * SPECIAL, INCIDENTAL, INDIRECT, OR CONSEQUENTIAL DAMAGES
 * WHATSOEVER (INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS
 * OF BUSINESS PROFITS, BUSINESS INTERRUPTION, LOSS OF BUSINESS
 * INFORMATION, OR ANY OTHER PECUNIARY LOSS), INCLUDING ATTORNEYS'
 * FEES, RELATING TO THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF NVIDIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 */

package cerberus.view.swing.scatterplot;


import java.awt.*;
import java.awt.event.*;
//import java.awt.image.*;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
//import java.nio.*;
//import java.util.*;
//import javax.imageio.*;
//import javax.imageio.stream.*;
import javax.swing.*;
//import javax.swing.SwingUtilities;

import javax.media.opengl.GL;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;
import demos.common.*;
import demos.util.*;
import gleem.*;
import gleem.linalg.*;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;

import cerberus.math.statistics.minmax.MinMaxDataInteger;
import cerberus.math.statistics.histogram.HistogramData;
import cerberus.math.statistics.histogram.HistogramStatisticInteger;
import cerberus.math.statistics.histogram.HistogramStatisticsSet;
import cerberus.math.statistics.histogram.StatisticHistogramType;
import cerberus.view.swing.loader.FileLoader;

/**
  Wavelength-dependent refraction demo<br>
  It's a chromatic aberration!<br>
  sgreen@nvidia.com 4/2001<br><p>

  Currently 3 passes - could do it in 1 with 4 texture units<p>

  Cubemap courtesy of Paul Debevec<p>

  Ported to Java and ARB_fragment_program by Kenneth Russell
*/

public class JoglScatterPlot2D extends Demo {
	
  public static void main(String[] args) {
    GLCanvas canvas = new GLCanvas();
    final Animator animator = new Animator(canvas);
    
    JoglScatterPlot2D demo = new JoglScatterPlot2D(canvas,animator);
    canvas.addGLEventListener(demo);
    
    demo.setDemoListener(new DemoListener() {
        public void shutdownDemo() {
          runExit(animator);
        }
        public void repaint() {}
      });


    
    animator.start();
  }
  
  private MinMaxDataInteger doMinMaxData;
  
  private boolean initComplete;
  private boolean firstRender = true;

  private Texture cubemap;
  private int obj = 2;

  private GLU  glu  = new GLU();
  private GLUT glut = new GLUT();

//  private int iHistrogemMaxValue = 0;
//  private int iHistrogemMinValue = 0;
//  private int iHistrogemMaxCountAllIntervalls = 0;
//  private int[] iHistogramIntervalls = null;
  private boolean bUpdateHistogram = true;
  
  private GLAutoDrawable drawable;
  private ExaminerViewer viewer;
  private boolean doViewAll = true;

  private Time  time = new SystemTime();
  private float animRate = (float) Math.toRadians(-6.0f); // Radians / sec

  private int iSetCacheId = 0;

  private boolean wire = false;
  private boolean toggleWire = false;

  private List < HistogramData > listHistogramData;
  
  private SetMultiDim refSet = null;

  private StatisticHistogramType enumCurrentHistogramMode = StatisticHistogramType.REGULAR_LINEAR;
  
  private int iCurrentHistogramMode;
  
  private int iCurrentHistogramLength = 200;
  
  public JoglScatterPlot2D( GLCanvas canvas, final Animator animator ) {
	  
	  doMinMaxData = new MinMaxDataInteger(2);  
	  
	    JFrame frame = new JFrame("Refraction Using Vertex Programs");
	    frame.setLayout(new BorderLayout());
	    canvas.setSize(512, 512);
	    frame.add(canvas, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);
	    canvas.requestFocus();

	    frame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	          runExit(animator);
	        }
	      });

	    JMenu menu = new JMenu("load..");
	    
	    JMenuItem item = new JMenuItem();
	    item.addActionListener( new ActionListener() { 
	    		 public void actionPerformed(ActionEvent e) {
	    			 displayMessageFromThread("Load Menu");
	    		 }
	    		 });
	    		 
	    menu.add( item );
	    
	    JMenuBar menuBar = new JMenuBar();
	    menuBar.add( menu );
	    
	    frame.setJMenuBar( menuBar );
  }
  
  public JoglScatterPlot2D( ) {
	  doMinMaxData = new MinMaxDataInteger(2);  
  }
  
  
  public void init(GLAutoDrawable drawable) {
    initComplete = false;
    GL gl = drawable.getGL();
    float cc = 1.0f;
    gl.glClearColor(cc, cc, cc, 1);
    gl.glColor3f(1,1,1);
    gl.glEnable(GL.GL_DEPTH_TEST);

    bArray256[' '] = true; // animate by default
  
    listHistogramData = new LinkedList <HistogramData> ();
    
    try {
      cubemap = Cubemap.loadFromStreams(getClass().getClassLoader(),
                                        "demos/data/cubemaps/uffizi_",
                                        "png",
                                        true);
    } catch (IOException e) {
      shutdownDemo();
      throw new RuntimeException(e);
    }

    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

    gl.glDisable(GL.GL_CULL_FACE);

    doViewAll = true;

    // Do this only once per drawable, not once each time the OpenGL
    // context is created
    if (firstRender) {
      firstRender = false;

      drawable.addKeyListener(new KeyAdapter() {
          public void keyTyped(KeyEvent e) {
            dispatchKey(e.getKeyChar());
          }
        });

      // Register the window with the ManipManager
      ManipManager manager = ManipManager.getManipManager();
      manager.registerWindow(drawable);
      this.drawable = drawable;

      viewer = new ExaminerViewer(MouseButtonHelper.numMouseButtons());
      viewer.setNoAltKeyMode(true);
      viewer.setAutoRedrawMode(false);
      viewer.attach(drawable, new BSphereProvider() {
          public BSphere getBoundingSphere() {
            return new BSphere(new Vec3f(0, 0, 0), 1.0f);
          }
        });
      viewer.setVertFOV((float) (15.0f * Math.PI / 32.0f));
      viewer.setZNear(0.1f);
      viewer.setZFar(10.0f);
    }
  
    initComplete = true;
  }

 
  public void display(GLAutoDrawable drawable) {
	  //displayHistogram(drawable);
	  displayHistogram(drawable);
	  
	  
	  if (getFlag('s')) {
		  
		  GL gl = drawable.getGL();
		  
	      // single pass
	      drawObj(gl, obj);
	
	    } 
  }
  
  //public int[] createHistogram(final int iHistogramLevels) {
  public void createMinMax() {
	  
	  doMinMaxData.useSet( this.refSet );
	  
  }
  
  private void displayMessageFromThread( final String messageText ) {
	  
    new Thread(new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(null, messageText, "Unavailable extension", JOptionPane.ERROR_MESSAGE);
        }
      }).start();
  }
  
  public void toggleMode() {
	  
	  iCurrentHistogramMode++;	  
	  if ( iCurrentHistogramMode > 2 ) {
		  iCurrentHistogramMode = 0;
	  }
	  
	  enumCurrentHistogramMode = 
		  StatisticHistogramType.getTypeByIndex(iCurrentHistogramMode);	  
	  
	  System.out.println(" TOGGLE MODE: " + 
			  Integer.toString(iCurrentHistogramMode) + "  -->" +
			  enumCurrentHistogramMode.toString() + " DONE");
	  
	  createMinMax();
	  
	  iSetCacheId = refSet.getCacheId();
  }
  
  public int getHistogramLength() {
	  return iCurrentHistogramLength;
  }
  
  public void setHistogramLength( final int iSetLegth ) {
	  if (( iSetLegth > 5 )&&(iSetLegth < 10000 )) {
		  iCurrentHistogramLength = iSetLegth;
		  
		  createMinMax();
		  
		  iSetCacheId = refSet.getCacheId();
	  }
	  else {
		  
		  System.out.println("exceed range [3..10000]");
		  
//		  throw new RuntimeException("setHistogramLength(" +
//				  Integer.toString(iSetLegth) + ") exceeded range [3..10000]");
	  }
  }
  
  public void displayHistogram(GLAutoDrawable drawable) {
		if (!initComplete) {
			return;
		}

		time.update();

		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		if (doViewAll) {
			viewer.viewAll(gl);
			doViewAll = false;
		}

		// if (getFlag(' ')) {
		// viewer.rotateAboutFocalPoint(new Rotf(Vec3f.Y_AXIS, (float)
		// (time.deltaT() * animRate)));
		// }

		if (toggleWire) {
			toggleWire = false;
			wire = !wire;
			if (wire) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			}
		}

		// draw background
		// gl.glDisable(GL.GL_DEPTH_TEST);
		// drawSkyBox(gl);
		// gl.glEnable(GL.GL_DEPTH_TEST);

		viewer.update(gl);
		ManipManager.getManipManager().updateCameraParameters(drawable,
				viewer.getCameraParameters());
		ManipManager.getManipManager().render(drawable, gl);

		gl.glDisable(GL.GL_LIGHTING);

		if (this.refSet != null) {

			if (refSet.getReadToken()) {

				if (refSet.getDimensions() < 2) {
					System.out
							.println("JoglScatterPlot2D.displayHistogram() Can not use a ISet with only one dimension!");
				}

				IStorage refStorageX = this.refSet.getStorageByDimAndIndex(0, 0);
				IStorage refStorageY = this.refSet.getStorageByDimAndIndex(1, 0);

				int[] i_dataValuesX = refStorageX.getArrayInt();
				int[] i_dataValuesY = refStorageY.getArrayInt();

				if ((i_dataValuesX != null) && (i_dataValuesY != null)) {

					if (refSet.hasCacheChanged(iSetCacheId)) {

						createMinMax();
						
						System.out.println(" UPDATED!");
						
						iSetCacheId = refSet.getCacheId();
					}
					// System.out.print("+");				

					IVirtualArrayIterator iterX = refSet.getVirtualArrayByDimAndIndex(
							0, 0).iterator();
					IVirtualArrayIterator iterY = refSet.getVirtualArrayByDimAndIndex(
							1, 0).iterator();

					if (!doMinMaxData.isValid()) {
						doMinMaxData.updateData();
					}

					float fMinX = -0.7f;
					float fMaxX = 0.7f;

					float fMinY = -0.7f;
					float fMaxY = 0.7f;

					float fIncX = (fMaxX - fMinX)
							/ (float) (doMinMaxData.getMax(0) - doMinMaxData.getMin(0));
					float fIncY = (fMaxY - fMinY) 
							/ (float) (doMinMaxData.getMax(1) - doMinMaxData.getMin(1));

					gl.glNormal3f(1.0f, 0.05f, 0.05f);
					gl.glPointSize( 1.5f );
					
					gl.glBegin(GL.GL_POINTS);

					while ((iterX.hasNext()) && (iterY.hasNext())) {

						float x = (float) i_dataValuesX[iterX.next()] * fIncX + fMinX;
						float y = (float) i_dataValuesY[iterY.next()] * fIncY + fMinY;
						gl.glVertex3f(x, y, 0.0f);

					} // end while: (( iterX.hasNext() )&&( iterY.hasNext() ))
						// {

					gl.glEnd();

					gl.glColor3f(0.1f, 0.1f, 1.0f);
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(fMinX, fMinY, 0.0f);
					gl.glVertex3f(fMaxX, fMinY, 0.0f);
					gl.glVertex3f(fMaxX, fMaxY, 0.0f);
					gl.glVertex3f(fMinX, fMaxY, 0.0f);
					gl.glEnd();
				} // end: if ((i_dataValuesX != null) && (i_dataValuesY != null)) {

				refSet.returnReadToken();

			} // end: if ( refSet.getReadToken() ) {

		} // end: if ( this.refSet != null ) {

		// else {
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glColor3f(1, 0, 0);
		gl.glVertex3f(-1.0f, -1.0f, -0.5f);
		gl.glColor3f(1, 0, 1);
		gl.glVertex3f(1.0f, 1.0f, -0.5f);
		gl.glColor3f(0, 1, 0);
		gl.glVertex3f(1.0f, -1.0f, -0.5f);
		gl.glEnd();
		// }

		gl.glEnable(GL.GL_LIGHTING);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPopMatrix();
	}
  
 
  
  
  // Unused routines
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

  // ----------------------------------------------------------------------
  // Internals only below this point
  //
  public void shutdownDemo() {
	  
    if (drawable != null) {
      viewer.detach();
      ManipManager.getManipManager().unregisterWindow(drawable);
      drawable.removeGLEventListener(this);
      
      //patch Michael Kalkusch
      drawable = null;
    }
    super.shutdownDemo();
  }


  
 
  private boolean[] bArray256 = new boolean[256];
  
  private void dispatchKey(char k) {
    setFlag(k, !getFlag(k));

    // Quit on escape or 'q'
    if ((k == (char) 27) || (k == 'q')) {
      shutdownDemo();
      return;
    }

    switch (k) {
    case '1':
      obj = 0;
      break;

    case '2':
      obj = 1;
      break;

    case '3':
      obj = 2;
      break;

    case '4':
      obj = 3;
      break;

    case 'v':
      doViewAll = true;
      break;

    case 'w':
    case 'W':
      toggleWire = true;
      break;

    case 'l':
    case 'L':
    	displayMessageFromThread("LOAD...");   	
    	break;
    	
    default:
      break;
    }
  }

  private void setFlag(char key, boolean val) {
	  System.out.println("  Histo: Key=[" + key + "]");
	  
    bArray256[((int) key) & 0xFF] = val;
  }

  private boolean getFlag(char key) {
	  
	  
    return bArray256[((int) key) & 0xFF];
  }

 


  private void drawSkyBox(GL gl) {
    // Compensates for ExaminerViewer's modification of modelview matrix
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();

    gl.glActiveTexture(GL.GL_TEXTURE1);
    gl.glDisable(GL.GL_TEXTURE_CUBE_MAP);
  
    gl.glActiveTexture(GL.GL_TEXTURE0);
    cubemap.bind();
    cubemap.enable();

    // This is a workaround for a driver bug on Mac OS X where the
    // normals are not being sent down to the hardware in
    // GL_NORMAL_MAP texgen mode. Temporarily enabling lighting
    // causes the normals to be sent down. Thanks to Ken Dyke.
    gl.glEnable(GL.GL_LIGHTING);

    gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
    gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);
    gl.glTexGeni(GL.GL_R, GL.GL_TEXTURE_GEN_MODE, GL.GL_NORMAL_MAP);

    gl.glEnable(GL.GL_TEXTURE_GEN_S);
    gl.glEnable(GL.GL_TEXTURE_GEN_T);
    gl.glEnable(GL.GL_TEXTURE_GEN_R);

    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

    gl.glMatrixMode(GL.GL_TEXTURE);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glScalef(1.0f, -1.0f, 1.0f);
    viewer.updateInverseRotation(gl);
    
    glut.glutSolidSphere(5.0, 40, 20);

    gl.glDisable(GL.GL_LIGHTING);

    gl.glPopMatrix();
    gl.glMatrixMode(GL.GL_MODELVIEW);

    gl.glDisable(GL.GL_TEXTURE_GEN_S);
    gl.glDisable(GL.GL_TEXTURE_GEN_T);
    gl.glDisable(GL.GL_TEXTURE_GEN_R);
  }

  private void drawObj(GL gl, int obj) {
    switch(obj) {
    case 0:
    
    case 1:
      glut.glutSolidSphere(0.5, 64, 64);
      break;

    case 2:
      glut.glutSolidTorus(0.25, 0.5, 64, 64);
      break;

    case 3:
    	drawHeatmapPlane(gl, 1.0f, 1.0f, 50, 50);
      break;
      
    case 4:
        drawPlane(gl, 1.0f, 1.0f, 50, 50);
        break;
    }
  }

  // draw square subdivided into quad strips
  private void drawPlane(GL gl, float w, float h, int rows, int cols) {
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
        gl.glColor3f(s, t, 0.0f);
        gl.glVertex3f(vx, vy, 0.0f);

        gl.glColor3f(s, t + tt, 0.0f);
        gl.glTexCoord2f(s, t + tt);
        gl.glVertex3f(vx, vy + th, 0.0f);
      }
      gl.glEnd();
    }
  }
  
  // draw square subdivided into quad strips
  private void drawHeatmapPlane(GL gl, float w, float h, int rows, int cols) {
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
        gl.glColor3f(s, t, 0.0f);
        gl.glVertex3f(vx, vy, 0.0f);

        gl.glColor3f(s, t + tt, 0.0f);
        gl.glTexCoord2f(s, t + tt);
        gl.glVertex3f(vx, vy + th, 0.0f);
      }
      gl.glEnd();
    }
  }
  
  private static void runExit(final Animator animator) {
	    // Note: calling System.exit() synchronously inside the draw,
	    // reshape or init callbacks can lead to deadlocks on certain
	    // platforms (in particular, X11) because the JAWT's locking
	    // routines cause a global AWT lock to be grabbed. Instead run
	    // the exit routine in another thread.
	    new Thread(new Runnable() {
	        public void run() {
	          animator.stop();
	          System.exit(0);
	        }
	      }).start();
	    }

  public void setSet(SetMultiDim setRefSet) {
		refSet = setRefSet;
		
		/** force update... */
		iSetCacheId = refSet.getCacheId() - 1;
		}
}
