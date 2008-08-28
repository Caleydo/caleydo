package org.caleydo.core.view.swt.widget;

import javax.media.opengl.GLCapabilities;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Class takes a composite in the constructor, embeds an AWT Frame in it and
 * finally creates a GLCanvas. The GLCanvas can be retrieved by the
 * getGLCanvas() method.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class SWTEmbeddedJoglWidget
	extends ASWTEmbeddedWidget
{
	protected GLCaleydoCanvas gLCanvas = null;

	/**
	 * Constructor that takes the composite in which it should embed the
	 * GLCanvas.
	 * 
	 * @param Composite Reference to the composite that is supposed to be
	 *            filled.
	 */
	public SWTEmbeddedJoglWidget(Composite parentComposite)
	{
		super(parentComposite);
	}

	@Override
	public void createEmbeddedComposite()
	{
		super.createEmbeddedComposite();

		GLCapabilities glCapabilities = new GLCapabilities();
		glCapabilities.setStencilBits(1);

		gLCanvas = new GLCaleydoCanvas(glCapabilities);

		embeddedFrame.add(gLCanvas);
	}

	public GLCaleydoCanvas getGLCanvas()
	{
		return gLCanvas;
	}
}
