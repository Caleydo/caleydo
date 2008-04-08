package org.geneview.core.view.jogl.mouse;

import java.awt.event.MouseEvent;

/**
 * Print debug messages to System.*
 * 
 * @author Michael Kalkusch
 *
 */
public class JoglMouseListenerDebug 
extends JoglMouseListener {

	/**
	 * Constructor.
	 */
	public JoglMouseListenerDebug() {

		super();
	}

	public void mousePressed(MouseEvent e) {

		/* --- Left -- Mouse Button --- */
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			System.err.println("-- Left --" + this.toString());
		}

		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			System.err.println("-- Right --" + this.toString() );
		}

		/* --- Middle -- Mouse Button --- */
		if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
		{
			System.err.println("-- Middle --" + this.toString() );
		}
		
		super.mousePressed(e);

		
	}

	public void mouseReleased(MouseEvent e) {

		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			System.err.println("-- End Left --" + this.toString() );
		}

		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			System.err.println("-- End Right --" + this.toString() );
		}

		if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
		{
			System.err.println("-- END Middle --" + this.toString() );
		}
		
		super.mouseReleased(e);

		
	}

	// Methods required for the implementation of MouseMotionListener
	public void mouseDragged(MouseEvent e) {

		if (!bMouseRightButtonDown)
		{

			if (!bMouseMiddleButtonDown)
			{
				System.out.println("dragging... -rot-" + this.toString() );
			} else
			{
				System.out.println("dragging -zoom-..." + this.toString() );
			}

		} else
		{
			System.out.println("dragging -PAN-..." + this.toString() );
		}
		
		super.mouseDragged(e);
	}

}
