/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.jogl.listener;

//--- old JOGL version ---
//import net.java.games.jogl.GLDrawable;
//import net.java.games.jogl.GLEventListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import cerberus.net.dwt.swing.jogl.listener.GLEventListenerTarget;
import cerberus.net.dwt.swing.jogl.listener.GLEventObserverListener;
import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;


/**
 * Forwards GLEvents back to one source set in the constructor.
 * 
 * @author Michael Kalkusch
 *
 */
public class GLEventListenerSingleSource 
implements GLEventListener, GLEventObserverListener, GLEventForwardListener
{
 
	/**
	 * Reference to source of events, that is triggered.
	 */
	private GLEventListenerTarget refGLEventSourceInterface;
	
	/**
	 * Constructor sets the only source, taht is triggered by the events from the listener.
	 * 
	 * @param setGLEventSourceInterface source to be triggered
	 */
	public GLEventListenerSingleSource( final GLEventListenerTarget setGLEventSourceInterface ) {		
		refGLEventSourceInterface = setGLEventSourceInterface;
	}
	
	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#init(net.java.games.jogl.GLDrawable)
	 */
	public void init(GLAutoDrawable drawable) {
		refGLEventSourceInterface.initGL(drawable);
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#display(net.java.games.jogl.GLDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
		refGLEventSourceInterface.displayGL(drawable);
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		refGLEventSourceInterface.reshapeGL(drawable, arg1, arg2, arg3, arg4);
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#displayChanged(net.java.games.jogl.GLDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
		refGLEventSourceInterface.displayChangedGL(drawable, arg1, arg2);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.net.dwt.swing.jogl.listener.GLEventObserverListener#registerSource(prometheus.net.dwt.swing.jogl.listener.GLEventListenerTarget)
	 */
	public synchronized void registerSource( final GLEventListenerTarget refSource ) {
		assert refSource != null : "Can not register to null-pointer";
		
		refGLEventSourceInterface = refSource;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.net.dwt.swing.jogl.listener.GLEventObserverListener#unregisterSource(prometheus.net.dwt.swing.jogl.listener.GLEventListenerTarget)
	 */
	public synchronized boolean unregisterSource( final GLEventListenerTarget refSource ) {
		if ( refGLEventSourceInterface == refSource ) {
			assert false : "Created null-pointer in GLEventListenerSingleSource, next call different from registerSource() on this class will fail";
		
			refGLEventSourceInterface = null;
		}
		return true;
	}

}
