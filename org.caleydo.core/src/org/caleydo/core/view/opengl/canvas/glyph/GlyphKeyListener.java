package org.caleydo.core.view.opengl.canvas.glyph;

public class GlyphKeyListener
	extends java.awt.event.KeyAdapter
{

	boolean bKeyControlPressed = false;

	public void keyPressed(java.awt.event.KeyEvent event)
	{

		if (event.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL)
		{
			if (event.isControlDown())
				bKeyControlPressed = true;
		}
		else
		{
			// System.out.println("other");
		}
	}

	public void keyReleased(java.awt.event.KeyEvent event)
	{

		if (event.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL)
		{
			bKeyControlPressed = false;
		}
	}

	public boolean isControlDown()
	{

		return bKeyControlPressed;
	}

}
