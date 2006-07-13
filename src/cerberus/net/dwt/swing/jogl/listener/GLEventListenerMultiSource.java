/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.jogl.listener;

import java.util.Vector;
import java.util.Iterator;

//--- old JOGL version ---
//import net.java.games.jogl.GLDrawable;
//import net.java.games.jogl.GLEventListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import cerberus.net.dwt.swing.jogl.listener.GLEventListenerTarget;
import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;

/**
 * @author Michael Kalkusch
 *
 */
public class GLEventListenerMultiSource 
implements GLEventListener,GLEventObserverListener, GLEventForwardListener
{

	
	private Vector <GLEventListenerTarget> vecSource;
	
	
	/**
	 * 
	 */
	public GLEventListenerMultiSource() {
		
		vecSource = new Vector <GLEventListenerTarget> ();
	}

	/**
	 * TEsts is a source is registered.
	 * 
	 * @param refSource source to be tested
	 * @return TRUE if source is handled
	 */
	private boolean isSourceRegisterd( final GLEventListenerTarget refSource ) {
		return vecSource.contains( refSource );
	}
	
	/**
	 * Registers a source. If the source has already been registered it is not added a second time.
	 * 
	 * @param refSource source to be registered
	 */
	public synchronized void registerSource( final GLEventListenerTarget refSource ) {
		if ( isSourceRegisterd(refSource) ) {
			return;
		}
		vecSource.addElement( refSource );
	}
	
	/**
	 * Unregisters a source.
	 * 
	 * @param refSource source to be unregisterd
	 * @return TRUE if source was in list and removed, FALSE is source was not in list
	 */
	public synchronized boolean unregisterSource( final GLEventListenerTarget refSource ) {
		if ( ! isSourceRegisterd(refSource) ) {
			return false;
		}
		return vecSource.remove( refSource );
	}
	
	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#init(net.java.games.jogl.GLDrawable)
	 */
	public void init(GLAutoDrawable drawable) {
		Iterator<GLEventListenerTarget> iter = this.vecSource.iterator();
		
		while ( iter.hasNext() ) {
			iter.next().initGL( drawable );
		}
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#display(net.java.games.jogl.GLDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
		Iterator<GLEventListenerTarget> iter = this.vecSource.iterator();
		
		while ( iter.hasNext() ) {
			iter.next().displayGL( drawable );
		}
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		Iterator<GLEventListenerTarget> iter = this.vecSource.iterator();
		
		while ( iter.hasNext() ) {
			iter.next().reshapeGL( drawable, arg1, arg2, arg3, arg4 );
		}
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#displayChanged(net.java.games.jogl.GLDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
		Iterator<GLEventListenerTarget> iter = this.vecSource.iterator();
		
		while ( iter.hasNext() ) {
			iter.next().displayChangedGL( drawable, arg1, arg2 );
		}
	}

}
