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

import javax.media.opengl.GLAutoDrawable;

/**
 * Interface for GLEventListener sources.
 * 
 * @author Michael Kalkusch
 * 
 * @see net.java.games.jogl.GLEventListener
 * @see prometheus.net.dwt.swing.jogl.listener.GLEventObserverListener
 * @see prometheus.net.dwt.swing.jogl.listener.GLEventListenerMultiSource
 * @see prometheus.net.dwt.swing.jogl.listener.GLEventListenerSingleSource
 *
 */
public interface GLEventListenerTarget {

	/** 
	 * Wrap init()
	 * 
	 * @see net.java.games.jogl.GLEventListener#init(net.java.games.jogl.GLDrawable)
	 */
	public void initGL(GLAutoDrawable drawable);

	/**
	 * Warp display()
	 * 
	 * @see net.java.games.jogl.GLEventListener#display(net.java.games.jogl.GLDrawable)
	 */
	public void displayGL(GLAutoDrawable drawable);

	/**
	 * Warp reshape()
	 * 
	 * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable, int, int, int, int)
	 */
	public void reshapeGL(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4);

	/** 
	 * Wrap dispalyChanged()
	 * 
	 * @see net.java.games.jogl.GLEventListener#displayChanged(net.java.games.jogl.GLDrawable, boolean, boolean)
	 */
	public void displayChangedGL(GLAutoDrawable drawable, boolean arg1, boolean arg2);
}
