/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.text;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;

public class BitmapTextRendererAWTTest extends Frame {
	private BufferedImage image;

	/**
	 *
	 */
	public BitmapTextRendererAWTTest() {
		super();
		setSize(1024, 1024);
		setVisible(true);

		image = new BufferedImage(640, 640, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = image.createGraphics();
		g.setBackground(Color.BLACK.getAWTColor());
		g.setColor(Color.WHITE.getAWTColor());
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		ABitmapTextRenderer t = new ABitmapTextRenderer(new Font("Arial", Font.PLAIN, 50)) {

			@Override
			protected void markDirty(Rectangle bounds) {

			}

			@Override
			protected Pair<Graphics2D, Dimension> createGraphics(Rectangle maxBounds) {

				return Pair.make(g, new Dimension(image.getWidth(), image.getHeight()));
			}
		};
		g.dispose();

	}

	@Override
	public void paint(Graphics ga) {
		super.paint(ga);

		final Graphics2D g = (Graphics2D) ga;

		// Finally, scale the image by a factor of 10 and display it
		// in the window. This will allow us to see the anti-aliased pixels
		g.drawImage(image, AffineTransform.getScaleInstance(10, 10), this);

		// Draw the image one more time at its original size, for comparison
		g.drawImage(image, 0, 0, this);

	}

	public static void main(String[] args) {
		new BitmapTextRendererAWTTest();
	}

}
