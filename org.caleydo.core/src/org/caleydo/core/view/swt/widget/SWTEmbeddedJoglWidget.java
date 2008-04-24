package org.caleydo.core.view.swt.widget;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import org.eclipse.swt.widgets.Composite;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.jogl.JoglCanvasForwarder;


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
	
	protected GLCanvas gLCanvas = null;
	
	/**
	 * Constructor that takes the composite in which it should 
	 * embed the GLCanvas.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedJoglWidget(Composite refParentComposite) {
	
		super(refParentComposite);
	}
	
	public void createEmbeddedComposite(final IGeneralManager generalManager,
			final int iGLCanvasID) {
		
		super.createEmbeddedComposite();
		
		assert gLCanvas == null : "GLCanvas was already created!";
			
        GLCapabilities glCapabilities = new GLCapabilities();
        glCapabilities.setStencilBits(1);
		
		gLCanvas = new JoglCanvasForwarder(generalManager, iGLCanvasID, glCapabilities);
		
		refEmbeddedFrame.add(gLCanvas);
	}

	public GLCanvas getGLCanvas() {
		
		return gLCanvas;
	}
}
