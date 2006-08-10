/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.view.gui.swt.widget;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Class takes a composite in the constructor,
 * embedds an AWT Frame in it and finally creates a GLCanvas.
 * The GLCanvas can be retrieved by the getGLCanvas() method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedJoglWidget extends ASWTWidget 
{
	/**
	 * Embedded AWT Frame.
	 */
	protected final java.awt.Frame refEmbeddedFrame;
	
	/**
	 * GLCanvas.
	 */
	protected final GLCanvas refGLCanvas;
	
	
	/**
	 * Constructor that takes the composite in which it should 
	 * embedd the GLCanvas.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedJoglWidget(Composite refComposite)
	{
		super(refComposite);
		refEmbeddedFrame = SWT_AWT.new_Frame(refComposite);
		refGLCanvas = new GLCanvas();
		refEmbeddedFrame.add(refGLCanvas);
	}
	
	/**
	 * Get the GLCanvas.
	 * 
	 * @return The GLCanvas that is supposed to be filled by the View.
	 */
	public GLCanvas getGLCanvas()
	{
		return refGLCanvas;
	}
}
