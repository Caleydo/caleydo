package org.caleydo.core.view.opengl.util.text;
/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/


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
