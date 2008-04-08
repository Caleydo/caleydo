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

package cerberus.view.swing.multicanvas;


import java.awt.*;

//import java.awt.event.MouseMotionAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//import java.awt.image.*;

//import java.io.IOException;
//import java.util.List;
//import java.util.LinkedList;
import java.util.Iterator;
import java.util.Vector;

//import java.nio.*;
//import java.util.*;
//import javax.imageio.*;
//import javax.imageio.stream.*;
import javax.swing.*;
//import javax.swing.SwingUtilities;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLAutoDrawable;
import com.sun.opengl.util.Animator;
//import com.sun.opengl.util.texture.Texture;


import gleem.BSphere;
import gleem.BSphereProvider;
import gleem.ExaminerViewer;
import gleem.ManipManager;
import gleem.MouseButtonHelper;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import demos.common.Demo;
import demos.common.DemoListener;

import demos.util.SystemTime;
import demos.util.Time;

import cerberus.view.swing.multicanvas.JoglCanvasItem;



/**
  Wavelength-dependent refraction demo<br>
  It's a chromatic aberration!<br>
  sgreen@nvidia.com 4/2001<br><p>

  Currently 3 passes - could do it in 1 with 4 texture units<p>

  Cubemap courtesy of Paul Debevec<p>

  Ported to Java and ARB_fragment_program by Kenneth Russell
*/

public class JoglMultiCanvas extends Demo {
	
  public static void main(String[] args) {
    GLCanvas canvas = new GLCanvas();
    final Animator animator = new Animator(canvas);
    
    JoglMultiCanvas demo = new JoglMultiCanvas(canvas,animator);
    canvas.addGLEventListener(demo);
    
    demo.setDemoListener(new DemoListener() {
        public void shutdownDemo() {
          runExit(animator);
        }
        public void repaint() {}
      });


    
    animator.start();
  }

  protected Vector <JoglCanvasItem> vecJogleCanvasItem;
  
  private boolean initComplete;
  private boolean firstRender = true;

  private int obj = 2;

//  private GLU  glu  = new GLU();
//  private GLUT glut = new GLUT();

  
  private GLAutoDrawable drawable;
  private ExaminerViewer viewer;
  
  /**
   * Center view
   */
  private boolean doViewAll = true;
  
  /**
   * Enable disable wire frame mode
   */
  private boolean wire = false;
  
  
  private boolean toggleWire = false;
  
  private Time  time = new SystemTime();
 // private float animRate = (float) Math.toRadians(-6.0f); // Radians / sec



  
  public JoglMultiCanvas( GLCanvas canvas, final Animator animator ) {
	  
	    JFrame frame = new JFrame("Jogl Multi Canvas");
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
	    
	    vecJogleCanvasItem = new Vector <JoglCanvasItem> (5);
	    
	    JoglCanvasPlane plane = new JoglCanvasPlane();    	
	      plane.setParameters(3.0f, 1.0f, 0.7f, 30, 30);
	      
	    vecJogleCanvasItem.add( plane );
	    
	    JoglCanvasPlane plane2 = new JoglCanvasPlane();    	
	      plane2.setParameters(1.0f, 0.5f, 0.2f, 10, 10);
	      
	    vecJogleCanvasItem.add( plane2 );
  }
  
  public JoglMultiCanvas( ) {
	  	  
  }
  
  
  public void init(GLAutoDrawable drawable) {
    initComplete = false;
    GL gl = drawable.getGL();
    float cc = 1.0f;
    gl.glClearColor(cc, cc, cc, 1);
    gl.glColor3f(1,1,1);
    gl.glEnable(GL.GL_DEPTH_TEST);

    bArray256[' '] = true; // animate by default
    

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
	  
	  displayHistogram(drawable);
	  
//	  if (getFlag('s')) {
//		  
//		  GL gl = drawable.getGL();
//		  
//	      // single pass
//	      //drawObj(gl, obj);
//	
//	      if ( ! vecJogleCanvasItem.isEmpty() ) {
//		      Iterator <JoglCanvasItem> iter = vecJogleCanvasItem.iterator();
//			  while ( iter.hasNext() ) {
//				  iter.next().displayCanvas( gl );
//			  }
//	      }
//		  
//	    } 
	  
	 
  }
  
 
  
  private void displayMessageFromThread( final String messageText ) {
	  
    new Thread(new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(null, messageText, "Unavailable extension", JOptionPane.ERROR_MESSAGE);
        }
      }).start();
  }
  
 
  
  public void displayHistogram(GLAutoDrawable drawable) {
	  
	    if (!initComplete) {
	      return;
	    }

	    time.update();

	    GL gl = drawable.getGL();
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);

	    if (doViewAll) {
	    	System.out.println(" do View All ..");
	      viewer.viewAll(gl);
	      doViewAll = false;
	    }

//	    if (getFlag(' ')) {
//	      viewer.rotateAboutFocalPoint(new Rotf(Vec3f.Y_AXIS, (float) (time.deltaT() * animRate)));
//	    }

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
//	    gl.glDisable(GL.GL_DEPTH_TEST);
//	    drawSkyBox(gl);
//	    gl.glEnable(GL.GL_DEPTH_TEST);


	    viewer.update(gl);
	    ManipManager manipMng = ManipManager.getManipManager();
	    
	    manipMng.updateCameraParameters(
	    		drawable, 
	    		viewer.getCameraParameters());
	    manipMng.render(drawable, gl);
	    
	    Vec3f rayPoint = Vec3f.VEC_NULL;
	    Vec3f rayDirection = Vec3f.VEC_NULL;
	    
	    manipMng.getScreenToRayMapping().mapScreenToRay( new Vec2f(0.5f,0.5f), 
	    		viewer.getCameraParameters(),
	    		rayPoint,
	    		rayDirection );
	   
	    gl.glDisable( GL.GL_LIGHTING );

	    gl.glColor3i( 255,0,0 );
	    gl.glPointSize( 3 );
	    gl.glBegin( GL.GL_POINT );
	      gl.glVertex3f( rayPoint.x(), rayPoint.y(), rayPoint.z() );
	    gl.glEnd();
	    
	    //else {
		    gl.glBegin( GL.GL_TRIANGLES );
				gl.glNormal3f( 0.0f, 0.0f, 1.0f );
				gl.glColor3f( 1,0,0 );
				gl.glVertex3f( -1.0f, -1.0f, -0.5f );
				gl.glColor3f( 1,0,1 );
				gl.glVertex3f( 1.0f, 1.0f, -0.5f );
				gl.glColor3f( 0,1,0 );
				gl.glVertex3f( 1.0f, -1.0f, -0.5f );
			gl.glEnd();
	    //}
	    
			
			 if (getFlag('s')) {
				  
			      // single pass
			      //drawObj(gl, obj);
			
			      if ( ! vecJogleCanvasItem.isEmpty() ) {
				      Iterator <JoglCanvasItem> iter = vecJogleCanvasItem.iterator();
					  while ( iter.hasNext() ) {
						  iter.next().displayCanvas( gl );
					  }
			      }
				  
			    } 
			
	    gl.glEnable( GL.GL_LIGHTING );
	    
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glPopMatrix();
	  }
  
 
  
  
  // Unused routines
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
  
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

  //----------------------------------------------------------------------
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


}
