/**
 * 
 */
package cerberus.view.gui.jogl;

import java.awt.event.MouseEvent;
import cerberus.view.gui.jogl.JoglMouseListener;

/**
 * Print debug messages to System.*
 * 
 * @author Michael Kalkusch
 *
 */
public class JoglMouseListenerDebug extends JoglMouseListener {

	/**
	 * @param refParentGearsMain
	 */
	public JoglMouseListenerDebug(IJoglMouseListener refParentGearsMain) {

		super(refParentGearsMain);
	}

	public void mousePressed(MouseEvent e) {

		super.mousePressed(e);

		/* --- Left -- Mouse Button --- */
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			System.err.println(" -- Left --");
		}

		/* --- Right -- Mouse Button --- */
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			System.err.println(" -- Right --");
		}

		/* --- Middle -- Mouse Button --- */
		if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
		{
			System.err.println(" -- Middle --");
		}
	}

	public void mouseReleased(MouseEvent e) {

		super.mouseReleased(e);

		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			System.err.println(" -- End Left --");
		}

		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			System.err.println(" -- End Right --");
		}

		if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
		{
			System.err.println(" -- END Middle --");
		}
	}

	// Methods required for the implementation of MouseMotionListener
	public void mouseDragged(MouseEvent e) {

		if (!bMouseRightButtonDown)
		{

			if (!bMouseMiddleButtonDown)
			{
				System.out.println("dragging... -rot-");
			} else
			{
				System.out.println("dragging -zoom-...");
			}

		} else
		{
			System.out.println("dragging -PAN-...");
		}
		
		super.mouseDragged(e);
	}

}
