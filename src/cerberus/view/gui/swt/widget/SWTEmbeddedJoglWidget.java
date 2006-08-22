/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.view.gui.swt.widget;

import cerberus.view.gui.swt.widget.ASWTEmbeddedWidget;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Class takes a composite in the constructor,
 * embedds an AWT Frame in it and finally creates a GLCanvas.
 * The GLCanvas can be retrieved by the getGLCanvas() method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedJoglWidget extends ASWTEmbeddedWidget 
{
	
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
	public SWTEmbeddedJoglWidget(Composite refParentComposite, int iWidth, int iHeight)
	{
		super(refParentComposite,iWidth, iHeight);

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
