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
//import net.java.games.jogl.GLEventListener;

import javax.media.opengl.GLEventListener;

import cerberus.net.dwt.swing.jogl.listener.GLEventObserverListener;

/**
 * Forwards net.java.games.jogl.GLEventListener methodes.
 * 
 * @author Michael Kalkusch
 * 
 * @see net.java.games.jogl.GLEventListener
 *
 */
public interface GLEventForwardListener 
extends GLEventListener, GLEventObserverListener {

}
