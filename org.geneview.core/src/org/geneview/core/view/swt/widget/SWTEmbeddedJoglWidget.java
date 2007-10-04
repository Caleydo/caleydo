/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package org.geneview.core.view.swt.widget;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLJPanel;

import org.eclipse.swt.widgets.Composite;

import com.sun.opengl.util.Animator;

import org.geneview.core.view.swt.widget.ASWTEmbeddedWidget;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;


/**
 * Class takes a composite in the constructor,
 * embedds an AWT Frame in it and finally creates a GLCanvas.
 * The GLCanvas can be retrieved by the getGLCanvas() method.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class SWTEmbeddedJoglWidget 
extends ASWTEmbeddedWidget {
	
	/**
	 * GLCanvas.
	 */
	//protected GLCanvas refGLCanvas = null;
	protected GLJPanel refGLCanvas = null;
	
	protected Animator refAnimator = null;
	
	/**
	 * Constructor that takes the composite in which it should 
	 * embedd the GLCanvas.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedJoglWidget(Composite refParentComposite) {
	
		super(refParentComposite);
	}
	
	public void createEmbeddedComposite() {
		super.createEmbeddedComposite();
		
		try {
			assert refGLCanvas == null : "GLCanvas was already created!";
			
			//refGLCanvas = new GLCanvas();
			refGLCanvas = new GLJPanel();
		} 
		catch (UnsatisfiedLinkError ule) {
			System.err.println("Can not open Jogl frame inside SWT container!");
			System.err.println("Solution: Copy jogl related *.jar and native binary to path.");
			System.err.println("SWTEmbeddedJoglWidget.createEmbeddedComposite() ERROR: "				
					+ ule.toString() );		
			
			throw new GeneViewRuntimeException( "SWTEmbeddedJoglWidget.createEmbeddedComposite() ERROR: " + 
					ule.toString(),
					GeneViewRuntimeExceptionType.JOGL_SWT );
		}
		
		/* Add GLCanvas to SWT embedded composit..*/
		refEmbeddedFrame.add(refGLCanvas);
	}

	
//	/**
//	 * Get the GLCanvas.
//	 * 
//	 * @return The GLCanvas that is supposed to be filled by the View.
//	 */
//	public GLCanvas getGLCanvas() {
//		
//		return refGLCanvas;
//	}
	
	/**
	 * Get the GLCanvas embedded in a JPanel.
	 * Obviously this is faster than the GLCanvas AWT embedded frame!
	 * 
	 * @return The GLCanvas that is supposed to be filled by the View.
	 */	
	public GLJPanel getGLCanvas() {
		
		return refGLCanvas;
	}
	
	public Animator getGLAnimator() {
		return refAnimator;
	}
	
	public void setGLAnimator( Animator setAnimator ) {
		assert refAnimator == null : "Can not assign Animator if it is already assigned!";
		
		refAnimator = setAnimator;
	}
}
