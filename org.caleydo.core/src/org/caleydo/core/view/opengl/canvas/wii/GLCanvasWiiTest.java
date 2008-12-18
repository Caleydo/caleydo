package org.caleydo.core.view.opengl.canvas.wii;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteDiscoveredEvent;
import wiiremotej.event.WiiRemoteDiscoveryListener;

/**
 * <p>
 * This class is used to represent the movement of the WiiRemote on screen, with
 * the help of OpenGL. Currently only the movement of WiiRemote is displayed,
 * but adding support for the Nunchuk is easily done.
 * </p>
 * <p>
 * I currently aware of display issue, based on the rotation angle of the
 * remote, which I need to fix. Inexperience with OpenGL has so far prevented me
 * from resolving the issue.
 * </p>
 * <p>
 * In order to use this class you need to have the following libraries to build
 * and run this project:
 * <ul>
 * <li>JOGL ( https://jogl.dev.java.net/ )
 * <li>WiiRemoteJ ( http://www.wiili.org/WiiremoteJ )
 * <li>An implemention of the Java Bluetooth API ( see
 * http://www.wiili.org/WiiremoteJ )
 * </ul>
 * <p>
 * 
 * @author Andre-John Mas
 */
public class GLCanvasWiiTest
	extends AGLEventListener
	implements Runnable, KeyListener
{

	/** */
	private static final long serialVersionUID = 1L;

	private GLU glu = new GLU();

	private GLCanvas canvas;

	private float x = 0.0f;

	private float y = 0.0f;

	private float z = 0.0f;

	private float screenY = 0.0f;

	private float screenX = 0.0f;

	private double pointerX = 0.0f;

	private double pointerY = 0.0f;

	private boolean pointerVisible = false;

	private int inc = 1;

	private int[] samplesX;

	private int sampleXOffset;

	private int[] samplesY;

	private int sampleYOffset;

	private int[] samplesZ;

	private int sampleZOffset;

	private boolean remoteConnected = false;

	/**
	 * Constructor.
	 */
	public GLCanvasWiiTest(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{

		super(iGLCanvasID, sLabel, viewFrustum, true);

		// new Thread(this).start();

		samplesX = new int[800];
		samplesZ = new int[800];
		samplesY = new int[800];

		WiimoteDiscoveryListener discoveryListener = new WiimoteDiscoveryListener();
		WiiRemoteJ.findRemotes(discoveryListener);

	}

	private void displayBox(GL gl, float width, float height, float length)
	{

		gl.glColor3i(255, 0, 0);

		float left = (width / 2.0f) * -1;
		float right = (width / 2.0f);

		float top = (height / 2.0f);
		float bottom = (height / 2.0f) * -1;

		float front = (length / 2.0f) * -1;
		float back = (length / 2.0f);

		// Front

		gl.glColor3f(0.0f, 0.0f, 1.0f);

		gl.glBegin(GL.GL_QUADS);

		gl.glColor3f(0.5f, 0.2f, 0.2f);

		gl.glNormal3f(0.0f, 0.0f, -1.0f);

		gl.glVertex3f(left, bottom, front);
		gl.glVertex3f(right, bottom, front);
		gl.glVertex3f(right, top, front);
		gl.glVertex3f(left, top, front);

		// Back

		gl.glColor3f(0.7f, 0.7f, 0.7f);

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		gl.glVertex3f(left, bottom, back);
		gl.glVertex3f(right, bottom, back);
		gl.glVertex3f(right, top, back);
		gl.glVertex3f(left, top, back);

		// Left

		gl.glNormal3f(-1.0f, 0.0f, 0.0f);

		gl.glVertex3f(left, top, front);
		gl.glVertex3f(left, top, back);
		gl.glVertex3f(left, bottom, back);
		gl.glVertex3f(left, bottom, front);

		// Right

		gl.glNormal3f(1.0f, 0.0f, -0.0f);

		gl.glVertex3f(right, top, front);
		gl.glVertex3f(right, top, back);
		gl.glVertex3f(right, bottom, back);
		gl.glVertex3f(right, bottom, front);

		// Top

		gl.glColor3f(1.0f, 1.0f, 1.0f);

		gl.glNormal3f(0.0f, 1.0f, -0.0f);

		gl.glVertex3f(left, top, front);
		gl.glVertex3f(right, top, front);
		gl.glVertex3f(right, top, back);
		gl.glVertex3f(left, top, back);

		// Bottom

		gl.glColor3f(0.8f, 0.8f, 1.0f);

		gl.glNormal3f(0.0f, -1.0f, 0.0f);

		gl.glVertex3f(left, bottom, front);
		gl.glVertex3f(right, bottom, front);
		gl.glVertex3f(right, bottom, back);
		gl.glVertex3f(left, bottom, back);

		gl.glEnd();

		gl.glFlush();

	}

	private void displayBox(GL gl, float x, float y, float z, float width, float height,
			float length)
	{

		gl.glColor3i(255, 0, 0);

		float left = x;
		float right = x + width;

		float top = y + height;
		float bottom = y;

		float front = z;
		float back = z + length;

		// Front

		gl.glColor3f(0.0f, 0.0f, 1.0f);

		gl.glBegin(GL.GL_QUADS);

		gl.glColor3f(0.5f, 0.2f, 0.2f);

		gl.glNormal3f(0.0f, 0.0f, -1.0f);

		gl.glVertex3f(left, bottom, front);
		gl.glVertex3f(right, bottom, front);
		gl.glVertex3f(right, top, front);
		gl.glVertex3f(left, top, front);

		// Back

		gl.glColor3f(0.7f, 0.7f, 0.7f);

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		gl.glVertex3f(left, bottom, back);
		gl.glVertex3f(right, bottom, back);
		gl.glVertex3f(right, top, back);
		gl.glVertex3f(left, top, back);

		// Left

		gl.glNormal3f(-1.0f, 0.0f, 0.0f);

		gl.glVertex3f(left, top, front);
		gl.glVertex3f(left, top, back);
		gl.glVertex3f(left, bottom, back);
		gl.glVertex3f(left, bottom, front);

		// Right

		gl.glNormal3f(1.0f, 0.0f, -0.0f);

		gl.glVertex3f(right, top, front);
		gl.glVertex3f(right, top, back);
		gl.glVertex3f(right, bottom, back);
		gl.glVertex3f(right, bottom, front);

		// Top

		gl.glColor3f(1.0f, 1.0f, 1.0f);

		gl.glNormal3f(0.0f, 1.0f, -0.0f);

		gl.glVertex3f(left, top, front);
		gl.glVertex3f(right, top, front);
		gl.glVertex3f(right, top, back);
		gl.glVertex3f(left, top, back);

		// Bottom

		gl.glColor3f(0.8f, 0.8f, 1.0f);

		gl.glNormal3f(0.0f, -1.0f, 0.0f);

		gl.glVertex3f(left, bottom, front);
		gl.glVertex3f(right, bottom, front);
		gl.glVertex3f(right, bottom, back);
		gl.glVertex3f(left, bottom, back);

		gl.glEnd();

		gl.glFlush();

	}

	private void drawCircle(GL gl, float radius, float x, float y, float z)
	{

		gl.glBegin(GL.GL_LINE_LOOP);

		for (int i = 0; i < 360; i++)
		{
			float angle = (float) Math.toRadians(i);
			gl.glVertex3d(x + Math.cos(angle) * radius, y + Math.sin(angle) * radius, z);
		}

		gl.glEnd();

	}

	private void drawGraph(GL gl, int[] samples, int sampleOffset, float x, float z)
	{

		float sampleInterval = 20.0f / 800;

		gl.glBegin(GL.GL_LINE_STRIP);

		for (int i = 0; i < sampleOffset; i++)
		{
			float n = 2.0f - (samples[i] / 800.0f) * 4.0f;
			gl.glVertex3d(x + (sampleInterval * i), 4.0f + n, z);
		}

		gl.glEnd();

	}

	@Override
	public void display(final GL gl)
	{

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear
		// Screen
		// And
		// Depth
		// Buffer
		gl.glLoadIdentity();
		//        
		// gl.glTranslatef(0.0f,-5.0f,-3.0f);

		gl.glTranslatef(0.0f, 0.0f, -10.0f);

		// gl.glRotatef(-40.0f, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(screenY, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(screenX, 1.0f, 1.0f, 0.0f);

		float left = -8.0f;
		float right = 8.0f;
		float top = 6.0f;
		float bottom = -6.0f;
		float front = -8.0f;

		gl.glBegin(GL.GL_QUADS);

		gl.glColor3f(0.5f, 0.5f, 0.5f);

		gl.glNormal3f(0.0f, 0.0f, -1.0f);

		gl.glVertex3f(left, bottom, front);
		gl.glVertex3f(right, bottom, front);
		gl.glVertex3f(right, top, front);
		gl.glVertex3f(left, top, front);

		gl.glEnd();

		if (this.pointerVisible)
		{
			gl.glColor3f(0.0f, 0.0f, 0.9f);

			float screenWidth = right - left;
			float screenHeight = top - bottom;

			drawCircle(gl, 0.5f, (float) (left + (screenWidth * (1.0 - this.pointerX))),
					(float) (bottom + (screenHeight * (1.0 - this.pointerY))), -8.0f);
		}

		// ------
		gl.glColor3f(1.0f, 1.0f, 1.0f);

		gl.glBegin(GL.GL_LINE_STRIP);

		gl.glVertex3d(-10f, 4.0f, -8.0f);
		gl.glVertex3d(10f, 4.0f, -8.0f);

		gl.glEnd();

		// info: draw acceleration info for X

		gl.glColor3f(1.0f, 0.0f, 0.0f);

		drawGraph(gl, samplesX, sampleXOffset, -10f, -8.0f);

		// info: draw acceleration info for Y

		gl.glColor3f(0.0f, 1.0f, 0.0f);

		drawGraph(gl, samplesY, sampleYOffset, -10f, -8.0f);

		// info: draw acceleration info for Z

		gl.glColor3f(0.0f, 0.0f, 1.0f);

		drawGraph(gl, samplesZ, sampleZOffset, -10f, -8.0f);

		float z = 0 - (180 * this.z);

		float x = 0 - (180 * this.x);

		gl.glRotatef(x, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(y, 0.0f, 1.0f, 0.0f);

		gl.glRotatef(z, 0.0f, 0.0f, 1.0f);

		// float xV = (float) (1.0f * Math.random());
		//        
		// gl.glTranslatef(0.5f-xV,0.0f,0.0f);

		// gl.glRotatef(x, 0.5f, 0.5f, 0.5f);
		// gl.glRotatef(y, 0.0f, 1.0f, 1.0f);
		// gl.glRotatef(z, 0.0f, 0.0f, 1.0f);

		if (this.remoteConnected)
		{
			gl.glColor3i(255, 0, 0);

			displayBox(gl, 1.0f, 0.5f, 4.0f);

			displayBox(gl, -0.5f, -0.0f, -2.5f, 1.0f, 0.25f, 0.5f);
		}

		// z-=inc;
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged)
	{

		// 

	}

	@Override
	public void init(final GL gl)
	{

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // This Will Clear The
		// Background Color To Black
		gl.glClearDepth(1.0); // Enables Clearing Of The Depth Buffer
		gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Test To Do
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really
		// Nice
		// Perspective
		// Calculations
		// Enable Light One

		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f); // Full Brightness. 50% Alpha
		// (new )

	}

	// public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	// int height) {
	// GL gl = drawable.getGL();
	// ;
	//
	// if (height == 0)
	// height = 1;
	// gl.glViewport(0, 0, width, height); // Reset The Current Viewport And
	// // Perspective Transformation
	// gl.glMatrixMode(GL.GL_PROJECTION); // Select The Projection Matrix
	// gl.glLoadIdentity(); // Reset The Projection Matrix
	// glu.gluPerspective(45.0f, width / (height * 1.0f), 0.1f, 100.0f); //
	// Calculate
	// // The
	// // Aspect
	// // Ratio
	// // Of
	// // The
	// // Window
	// gl.glMatrixMode(GL.GL_MODELVIEW); // Select The Modelview Matrix
	// gl.glLoadIdentity();
	//
	// }

	/* ---------------------------------------------------- */
	/* KeyListener methods */
	/* ---------------------------------------------------- */

	public void keyPressed(KeyEvent e)
	{

		switch (e.getKeyCode())
		{
			case KeyEvent.VK_LEFT:
				screenY -= inc;
				canvas.repaint(0);
				break;
			case KeyEvent.VK_RIGHT:
				screenY += inc;
				canvas.repaint(0);
				break;
			case KeyEvent.VK_UP:
				screenX -= inc;
				canvas.repaint(0);
				break;
			case KeyEvent.VK_DOWN:
				screenX += inc;
				canvas.repaint(0);
				break;
			case KeyEvent.VK_SPACE:
				screenX = 0.0f;
				screenY = 0.0f;
				canvas.repaint(0);
				break;

		}
	}

	public void keyReleased(KeyEvent e)
	{

	}

	public void keyTyped(KeyEvent e)
	{

	}

	/* ---------------------------------------------------- */
	/* Runnable methods */
	/* ---------------------------------------------------- */

	public void run()
	{

		while (true)
		{
			Thread.yield();
			try
			{
				Thread.sleep(100);

				canvas.repaint(0);

			}
			catch (InterruptedException e)
			{
			}
			canvas.repaint();
		}
	}

	/* ---------------------------------------------------- */
	/* Private classes */
	/* ---------------------------------------------------- */

	private class WiimoteDiscoveryListener
		implements WiiRemoteDiscoveryListener
	{

		public void findFinished(int numberFound)
		{

			System.out.println("Found " + numberFound + " remotes!");
		}

		public void wiiRemoteDiscovered(WiiRemoteDiscoveredEvent evt)
		{

			WiiRemote remote = evt.getWiiRemote();
			try
			{
				remote.setAccelerometerEnabled(true);
				remote.setIRSensorEnabled(true, WRIREvent.BASIC);
				remote.setLEDIlluminated(0, true);
				remote.setLEDIlluminated(2, true);
				// remote.setInterleaved(true);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			evt.getWiiRemote().addWiiRemoteListener(new WiimoteActions(evt.getWiiRemote()));
			GLCanvasWiiTest.this.remoteConnected = true;
			// Wiimote3D.this.wiiRemote = remote;
		}

	}

	private class WiimoteActions
		extends WiiRemoteAdapter
	{

		WiimoteActions(WiiRemote remote)
		{

		}

		/*
		 * (non-Javadoc)
		 * @see
		 * wiiremotej.event.WiiRemoteAdapter#combinedInputReceived(wiiremotej
		 * .event.WRCombinedEvent)
		 */
		@Override
		public void combinedInputReceived(WRCombinedEvent event)
		{

			WRAccelerationEvent accelerationEvent = event.getAccelerationEvent();

			if (accelerationEvent != null)
			{

				if (accelerationEvent.isStill())
				{
					GLCanvasWiiTest.this.z = (float) (accelerationEvent.getRoll() / 3.2f);
					GLCanvasWiiTest.this.x = (float) (accelerationEvent.getPitch() / 3.2f);
					// System.out.println

					// System.out.println ( "roll: " +
					// accelerationEvent.getRoll());
				}

				GLCanvasWiiTest.this.samplesX[GLCanvasWiiTest.this.sampleXOffset++] = (int) (accelerationEvent
						.getXAcceleration() / 5 * 300) + 300;
				if (GLCanvasWiiTest.this.sampleXOffset == 800)
				{
					GLCanvasWiiTest.this.sampleXOffset = 0;
				}

				GLCanvasWiiTest.this.samplesY[GLCanvasWiiTest.this.sampleYOffset++] = (int) (accelerationEvent
						.getYAcceleration() / 5 * 300) + 300;
				if (GLCanvasWiiTest.this.sampleYOffset == 800)
				{
					GLCanvasWiiTest.this.sampleYOffset = 0;
				}

				GLCanvasWiiTest.this.samplesZ[GLCanvasWiiTest.this.sampleZOffset++] = (int) (accelerationEvent
						.getZAcceleration() / 5 * 300) + 300;
				if (GLCanvasWiiTest.this.sampleZOffset == 800)
				{
					GLCanvasWiiTest.this.sampleZOffset = 0;
				}

			}

		}

		/*
		 * (non-Javadoc)
		 * @see wiiremotej.event.WiiRemoteAdapter#disconnected()
		 */
		@Override
		public void disconnected()
		{

			GLCanvasWiiTest.this.remoteConnected = false;
			// Wiimote3D.this.wiiRemote = null;
			System.out.println("Remote Disconnected");
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * wiiremotej.event.WiiRemoteAdapter#IRInputReceived(wiiremotej.event
		 * .WRIREvent)
		 */
		@Override
		public void IRInputReceived(WRIREvent event)
		{

			boolean positionData = false;

			IRLight[] irLights = event.getIRLights();
			if (irLights != null)
			{
				for (IRLight irLight : irLights)
				{
					if (irLight != null)
					{
						System.out.println(" IRLIGHT: x=" + irLight.getX() + ", y="
								+ irLight.getY() + ", size=" + irLight.getSize());
						GLCanvasWiiTest.this.pointerX = irLight.getX();
						GLCanvasWiiTest.this.pointerY = irLight.getY();
						positionData = true;
					}
				}
			}

			// // MARC: Just for testing
			// float firstPointX = 0;
			// float firstPointY = 0;
			//
			// float secondPointX = 0;
			// float secondPointY = 0;
			//            
			// if (irLights != null)
			// {
			// firstPointX = (float) irLights[0].getX();
			// firstPointY = (float) irLights[0].getY();
			//                
			// secondPointX = (float) irLights[1].getX();
			// secondPointY = (float) irLights[1].getY();
			//      
			// positionData = true;
			// }

			// float radiansPerPixel = 0.3f;
			// float movementScaling = 1;
			// float dotDistanceInMM = 100;
			// float screenHeightinMM = 500;
			// boolean cameraIsAboveScreen = true;
			// float cameraVerticaleAngle = 45;
			//            
			// float dx = firstPointX - secondPointX;
			// float dy = firstPointY - secondPointY;
			// float pointDist = (float)Math.sqrt(dx * dx + dy * dy);
			//
			// float angle = radiansPerPixel * pointDist / 2;
			// //in units of screen hieght since the box is a unit cube and box
			// hieght is 1
			// float headDist = movementScaling * (float)((dotDistanceInMM / 2)
			// / Math.tan(angle)) / screenHeightinMM;
			//
			// float avgX = (firstPointX + secondPointX) / 2.0f;
			// float avgY = (firstPointY + secondPointY) / 2.0f;
			//
			// float headX = (float)(movementScaling * Math.sin(radiansPerPixel
			// * (avgX - 512)) * headDist);
			//
			// float relativeVerticalAngle = (avgY - 384) *
			// radiansPerPixel;//relative angle to camera axis
			// float headY = 0;
			// if(cameraIsAboveScreen)
			// headY = .5f+(float)(movementScaling *
			// Math.sin(relativeVerticalAngle + cameraVerticaleAngle)
			// *headDist);
			// else
			// headY = -.5f + (float)(movementScaling *
			// Math.sin(relativeVerticalAngle + cameraVerticaleAngle) *
			// headDist);
			//            
			GLCanvasWiiTest.this.pointerVisible = positionData;

			System.out.println("---------------");
		}
	}

	@Override
	public void displayLocal(GL gl)
	{

		display(gl);

	}

	@Override
	public void displayRemote(GL gl)
	{

		// TODO Auto-generated method stub

	}

	@Override
	public String getShortInfo()
	{
		return "WIIIIIIIIIIIIII";
	}

	@Override
	public String getDetailedInfo()
	{

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int externalID, Pick pick)
	{

		// TODO Auto-generated method stub

	}

	@Override
	public void initLocal(GL gl)
	{

		init(gl);

	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID, final RemoteLevel layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering remoteRenderingGLCanvas)
	{

	}

	@Override
	public void broadcastElements(ESelectionType type)
	{

	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType)
	{
		return 0;
	}
}