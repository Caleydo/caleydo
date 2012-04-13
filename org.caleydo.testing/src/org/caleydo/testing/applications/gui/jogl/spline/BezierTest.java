/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.testing.applications.gui.jogl.spline;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

/**
 * This program uses evaluators to draw a Bezier curve.
 * 
 * @author Kiet Le (Java conversion)
 */
public class BezierTest
	extends JFrame
	implements GLEventListener, KeyListener
{
	private GLCapabilities caps;
	private GLCanvas canvas;
	private float ctrlpoints[][] = new float[][] { {1.0f, -4.0f, -1.0f },
			{ -2.0f, 4.0f, 0.0f }, { 2.0f, -4.0f, 0.0f }, { 4.0f, 4.0f, 2.0f } };
	private FloatBuffer ctrlpointBuf = //
	FloatBuffer.allocate(ctrlpoints[0].length * ctrlpoints.length);

	public BezierTest()
	{
		super("bezcurve");

		caps = new GLCapabilities();
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);

		getContentPane().add(canvas);
	}

	public void run()
	{
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		canvas.requestFocusInWindow();
	}

	public static void main(String[] args)
	{
		new BezierTest().run();
	}

	public void init(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();

		// need to convert 2d array to buffer type
		for (int i = 0; i < ctrlpoints.length; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				ctrlpointBuf.put(ctrlpoints[i][j]);
			}
		}
		ctrlpointBuf.rewind();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//		gl.glShadeModel(GL.GL_FLAT);
		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpointBuf);
		gl.glEnable(GL.GL_MAP1_VERTEX_3);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glLineWidth(30);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i <= 3; i++)
		{
			gl.glEvalCoord1f((float) i / (float) 3.0);
		}
		gl.glEnd();
		
		gl.glPointSize(50);
		gl.glBegin(GL.GL_POINTS);
		for (int i = 0; i <= 3; i++)
		{
			gl.glEvalCoord1f((float) i / (float) 3.0);
		}
		gl.glEnd();
		
		/* The following code displays the control points as dots. */
		gl.glPointSize(5.0f);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glBegin(GL.GL_POINTS);
		for (int i = 0; i < 4; i++)
		{
			gl.glVertex3fv(ctrlpointBuf);
			ctrlpointBuf.position(i * 3);
		}
		gl.glEnd();
		gl.glFlush();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		GL gl = drawable.getGL();

		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		if (w <= h) //
			gl.glOrtho(-5.0, 5.0, -5.0 * (float) h / (float) w, //
					5.0 * (float) h / (float) w, -5.0, 5.0);
		else
			gl.glOrtho(-5.0 * (float) w / (float) h, //
					5.0 * (float) w / (float) h,//
					-5.0, 5.0, -5.0, 5.0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged)
	{
	}

	public void keyTyped(KeyEvent key)
	{
	}

	public void keyPressed(KeyEvent key)
	{
		switch (key.getKeyChar())
		{
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;

			default:
				break;
		}
	}

	public void keyReleased(KeyEvent key)
	{
	}

}