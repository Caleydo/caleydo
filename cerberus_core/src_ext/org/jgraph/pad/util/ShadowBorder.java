/*
 * Created on Jul 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jgraph.pad.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;

import javax.swing.border.Border;

/**
 * @author Gaudenz Alder
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ShadowBorder implements Border, Serializable {
	private Insets insets;
	
	public static ShadowBorder sharedInstance = new ShadowBorder();

	public ShadowBorder() {
		insets = new Insets(1, 1, 3, 3);
	}

	public Insets getBorderInsets(Component c) {
		return insets;
	}

	public boolean isBorderOpaque() {
		// we'll be filling in our own background.
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		// choose which colors we want to use
		Color bg = c.getBackground();
		if (c.getParent() != null)
			bg = c.getParent().getBackground();
		Color mid = bg.darker();
		Color rect = mid.darker();
		Color edge = average(mid, bg);

		// fill in the corners with the parent-background
		// so it looks see-through
		g.setColor(bg);
		g.fillRect(0, h - 3, 3, 3);
		g.fillRect(w - 3, 0, 3, 3);
		g.fillRect(w - 3, h - 3, 3, 3);

		// draw the outline
		g.setColor(rect);
		g.drawRect(0, 0, w - 3, h - 3);

		// draw the drop-shadow
		g.setColor(mid);
		g.drawLine(1, h - 2, w - 2, h - 2);
		g.drawLine(w - 2, 1, w - 2, h - 2);

		g.setColor(edge);
		g.drawLine(2, h - 1, w - 2, h - 1);
		g.drawLine(w - 1, 2, w - 1, h - 2);
	}

	private static Color average(Color c1, Color c2) {
		int red = c1.getRed() + (c2.getRed() - c1.getRed()) / 2;
		int green = c1.getGreen() + (c2.getGreen() - c1.getGreen()) / 2;
		int blue = c1.getBlue() + (c2.getBlue() - c1.getBlue()) / 2;
		return new Color(red, green, blue);
	}
}