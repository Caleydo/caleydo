/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package org.geneview.core.view.swt.widget;

import java.awt.Frame;

import org.geneview.core.view.swt.widget.ASWTEmbeddedWidget;

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
	public SWTEmbeddedGraphWidget(Composite refParentComposite) {
		
		super(refParentComposite);
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
