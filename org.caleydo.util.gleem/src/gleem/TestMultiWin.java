/*
 * gleem -- OpenGL2 Extremely Easy-To-Use Manipulators. Copyright (C) 1998-2003 Kenneth B. Russell
 * (kbrussel@alum.mit.edu) Copying, distribution and use of this software in source and binary forms, with or
 * without modification, is permitted provided that the following conditions are met: Distributions of source
 * code must reproduce the copyright notice, this list of conditions and the following disclaimer in the
 * source code header files; and Distributions of binary code must reproduce the copyright notice, this list
 * of conditions and the following disclaimer in the documentation, Read me file, license file and/or other
 * materials provided with the software distribution. The names of Sun Microsystems, Inc. ("Sun") and/or the
 * copyright holder may not be used to endorse or promote products derived from this software without specific
 * prior written permission. THIS SOFTWARE IS PROVIDED "AS IS," WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR
 * IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, NON-INTERFERENCE, ACCURACY OF INFORMATIONAL CONTENT OR NON-INFRINGEMENT,
 * ARE HEREBY EXCLUDED. THE COPYRIGHT HOLDER, SUN AND SUN'S LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN
 * NO EVENT WILL THE COPYRIGHT HOLDER, SUN OR SUN'S LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA,
 * OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. YOU ACKNOWLEDGE THAT THIS SOFTWARE IS NOT DESIGNED, LICENSED OR
 * INTENDED FOR USE IN THE DESIGN, CONSTRUCTION, OPERATION OR MAINTENANCE OF ANY NUCLEAR FACILITY. THE
 * COPYRIGHT HOLDER, SUN AND SUN'S LICENSORS DISCLAIM ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR SUCH
 * USES.
 */

package gleem;

import gleem.linalg.Vec3f;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

/** Tests viewing manipulators in multiple winodws. */
@SuppressWarnings("all")
public class TestMultiWin {
	private static final int X_SIZE = 400;
	private static final int Y_SIZE = 400;

	private static HandleBoxManip manip;

	static class HandleBoxManipBSphereProvider implements BSphereProvider {
		private HandleBoxManip manip;

		private HandleBoxManipBSphereProvider(HandleBoxManip manip) {
			this.manip = manip;
		}

		public BSphere getBoundingSphere() {
			BSphere bsph = new BSphere();
			bsph.setCenter(manip.getTranslation());
			Vec3f scale0 = manip.getScale();
			Vec3f scale1 = manip.getGeometryScale();
			Vec3f scale = new Vec3f();
			scale.setX(2.0f * scale0.x() * scale1.x());
			scale.setY(2.0f * scale0.y() * scale1.y());
			scale.setZ(2.0f * scale0.z() * scale1.z());
			bsph.setRadius(scale.length());
			return bsph;
		}
	}

	static class Listener implements GLEventListener {
		private GLU glu = new GLU();
		private CameraParameters params = new CameraParameters();
		private ExaminerViewer viewer;

		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL();

			gl.glClearColor(0, 0, 0, 0);
			float[] lightPosition = new float[]{1, 1, 1, 0};
			float[] ambient = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
			float[] diffuse = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);

			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
			gl.glEnable(GL2.GL_DEPTH_TEST);

			params.setPosition(new Vec3f(0, 0, 0));
			params.setForwardDirection(new Vec3f(0, 0, -1));
			params.setUpDirection(new Vec3f(0, 1, 0));
			params.setVertFOV((float) (Math.PI / 8.0));
			params.setImagePlaneAspectRatio(1);
			params.xSize = X_SIZE;
			params.ySize = Y_SIZE;

			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(45, 1, 1, 100);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();

			// Register the window with the ManipManager
			ManipManager manager = ManipManager.getManipManager();
			manager.registerWindow(drawable);

			manager.showManipInWindow(manip, drawable);

			// Instantiate ExaminerViewer
			viewer = new ExaminerViewer(MouseButtonHelper.numMouseButtons());
			viewer.attach(drawable, new HandleBoxManipBSphereProvider(manip));
			viewer.viewAll(gl);
		}

		public void display(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL();
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
			viewer.update(gl);
			ManipManager.getManipManager().updateCameraParameters(drawable,
					viewer.getCameraParameters());
			ManipManager.getManipManager().render(drawable, gl);
		}

		// Unused routines
		public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		}

		public void displayChanged(GLAutoDrawable drawable,
				boolean modeChanged, boolean deviceChanged) {
		}
	}

	private static void showFrame(String name, Point location) {
		Frame frame = new Frame(name);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setLayout(new BorderLayout());
		GLCanvas canvas = new GLCanvas();
		canvas.setSize(400, 400);
		canvas.addGLEventListener(new Listener());
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setLocation(location);
		frame.show();
	}

	public static void main(String[] args) {
		// Instantiate HandleBoxManip
		manip = new HandleBoxManip();
		manip.setTranslation(new Vec3f(0, 0, -10));

		showFrame("MultiWin Test 1/2", new Point(0, 0));
		showFrame("MultiWin Test 2/2", new Point(400, 0));
	}
}
