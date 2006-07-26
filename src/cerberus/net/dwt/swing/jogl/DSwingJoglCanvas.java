/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.jogl;

import java.awt.BorderLayout;
import java.awt.Graphics;

//import java.awt.Frame;
//import javax.swing.JFrame;

// --- JOGL old version ---
//import net.java.games.jogl.GLCanvas;
//import net.java.games.jogl.GLCapabilities;
//import net.java.games.jogl.GLEventListener;
//import net.java.games.jogl.GLDrawableFactory;
//--- JOGL old version ---

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
//import javax.media.opengl.glu.*;


import cerberus.data.collection.view.ViewCanvas;
import cerberus.net.dwt.swing.jogl.ViewCanvasBaseItem;
import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;

//import cerberus.util.exception.CerberusRuntimeException;



/**
 * @author Michael Kalkusch
 *
 * @deprecated since JOGL version 2006
 */
public class DSwingJoglCanvas 
extends ViewCanvasBaseItem
//extends Frame
//extends DSwingJPanel 
//implements DNetEventComponentInterface, 
implements ViewCanvas
{
	
	//protected final GeneralManager refGeneralManager;

	protected final GLEventForwardListener refGLEventListener;

	protected DInternalFrame refDInternalFrame = null;
	
	protected GLCanvas canvas = null;

	protected int iTargetFrameId;
	
	/**
	 * Makle sure canvas is only initialized once.
	 */
	private boolean bCanvasIsAleadyInit = false;
	
	/**
	 * 
	 */
	public DSwingJoglCanvas( GeneralManager refGeneralManager,
			GLEventForwardListener setGLEventListener,
			final int iTargetFrameId ) {
		
		// super( refGeneralManager );
		super();

		assert setGLEventListener!= null : "can not init with null-pointer";
		
		//this.refGeneralManager= refGeneralManager;	
		this.refGLEventListener = setGLEventListener;
		
		this.iTargetFrameId = iTargetFrameId;
	}

	
	/**
	 * Get the GLcanvas
	 * @return
	 */
	public final GLCanvas getGLCanvas() {
		return canvas;
	}
	
	/**
	 * Create an initialze the GL canvas.
	 * 
	 * @param useGLEventListener 
	 * @param sHeaderText Test for Header
	 */
	public final void initGLCanvas( final String sHeaderText,
			final DInternalFrame callingInternalFrame ) {
		
		if ( bCanvasIsAleadyInit ) { 
			return;
		}
		
		assert false : "NOT TESTED and migrated to new JOGL version 2006";
		
		System.err.println("NOT TESTED and migrated to new JOGL version 2006");
		
		throw new RuntimeException("NOT TESTED and migrated to new JOGL version 2006");
	
		
//		canvas = GLDrawableFactory.getFactory().getGLDrawable(
//				(Object) callingInternalFrame,
//				new GLCapabilities(),
//				null );	
//		canvas.setSize(600, 600);		
//		canvas.addGLEventListener( refGLEventListener );
//		
////		String sTargetFrameId = Integer.toString( iTargetFrameId);
//		
//		/*
//		 * Late binding! Create internal frame not until now.
//		 */
////		refDInternalFrame = (DInternalFrame)
////			refGeneralManager.getSingelton().getViewCanvasManager().createCanvas( 
////					ManagerObjectType.VIEW_NEW_IFRAME, sTargetFrameId );
//		
//		refDInternalFrame = callingInternalFrame;
//		
//		refDInternalFrame.setTitle( sHeaderText );
//		refDInternalFrame.setLayout(new BorderLayout());
//		refDInternalFrame.add( canvas, BorderLayout.CENTER);
//		refDInternalFrame.pack();
//		
//		//canvas.requestFocus();
//		
//		bCanvasIsAleadyInit = true;
	}
	
	/**
	 * Get reference to the GLEventListener.
	 * 
	 * @return reference to GLEventListener
	 */
	public GLEventForwardListener getGLEventListener() {
		return refGLEventListener;
	}
	
	/**
	 * Returns the internal frame around the GL canvas.
	 * 
	 * @return internal frame around the GL canvas.
	 */
	protected DInternalFrame getDInternalFrame() {
		return refDInternalFrame;
	}
	
	/**
	 * Must override this methode in derived class.
	 * 
	 * @see cerberus.data.StatefulItem#updateState()
	 */
	public void updateState() {
			
	}
	
	public String createMementoXML() {
		return null;
	}
	
	public String createMementoXMLperObject() {
		return null;
	}
	
	public void paintDComponent( Graphics g ) {
		
	}
}
