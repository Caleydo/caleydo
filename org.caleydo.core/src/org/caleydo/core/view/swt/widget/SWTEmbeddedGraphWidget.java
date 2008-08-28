package org.caleydo.core.view.swt.widget;

import java.awt.Frame;
import org.eclipse.swt.widgets.Composite;

/**
 * Class takes a composite in the constructor and embeds an AWT Frame in it. The
 * Frame can be retrieved over the getEmbeddedFrame() method.
 * 
 * @author Marc Streit
 */
public class SWTEmbeddedGraphWidget
	extends ASWTEmbeddedWidget
{

	/**
	 * Constructor that takes the composite in which it should place the content
	 * and creates an embedded AWT frame.
	 * 
	 * @param Composite Reference to the composite that is supposed to be
	 *            filled.
	 */
	public SWTEmbeddedGraphWidget(Composite parentComposite)
	{

		super(parentComposite);
	}

	/**
	 * Get the embedded frame.
	 * 
	 * @return The embedded AWT Frame.
	 */
	public final Frame getEmbeddedFrame()
	{

		return embeddedFrame;
	}
}
