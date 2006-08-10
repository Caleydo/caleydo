/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.view.gui.swt.widget;

import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Class takes a composite in the constructor and
 * embedds an AWT Frame in it.
 * The Frame can be retrieved over the getEmbeddedFrame()
 * method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedGraphWidget extends ASWTWidget 
{
	/**
	 * Embedded AWT Frame.
	 */
	protected final java.awt.Frame refEmbeddedFrame;
	
	/**
	 * Constructor that takes the composite in which it should 
	 * place the content and creates an embedded AWT frame.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedGraphWidget(Composite refComposite)
	{
		super(refComposite);
		refEmbeddedFrame = SWT_AWT.new_Frame(refComposite);
	}

	/**
	 * Get the embedded frame.
	 * 
	 * @return The embedded AWT Frame.
	 */
	public final java.awt.Frame getEmbeddedFrame()
	{
		return refEmbeddedFrame;
	}
}
