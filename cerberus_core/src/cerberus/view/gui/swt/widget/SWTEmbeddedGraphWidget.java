/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.view.gui.swt.widget;

import java.awt.Frame;

import cerberus.view.gui.swt.widget.ASWTEmbeddedWidget;

import org.eclipse.swt.widgets.Composite;

/**
 * Class takes a composite in the constructor and
 * embedds an AWT Frame in it.
 * The Frame can be retrieved over the getEmbeddedFrame()
 * method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedGraphWidget 
extends ASWTEmbeddedWidget {
	
	/**
	 * Constructor that takes the composite in which it should 
	 * place the content and creates an embedded AWT frame.
	 * 
	 * @param Composite Reference to the composite 
	 * that is supposed to be filled.
	 */
	public SWTEmbeddedGraphWidget(Composite refParentComposite, 
			final int iWidth, 
			final int iHeight) {
		
		super(refParentComposite, iWidth, iHeight);
	}

	/**
	 * Get the embedded frame.
	 * 
	 * @return The embedded AWT Frame.
	 */
	public final Frame getEmbeddedFrame() {
		
		return refEmbeddedFrame;
	}
}
