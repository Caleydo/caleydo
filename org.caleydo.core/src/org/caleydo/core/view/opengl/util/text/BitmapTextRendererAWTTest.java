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

import org.caleydo.core.util.collection.Pair;

public class BitmapTextRendererAWTTest extends Frame {
	/**
	 *
	 */
	public BitmapTextRendererAWTTest() {
		super();
		setSize(1024, 1024);
		setVisible(true);
	}
	@Override
	public void paint(Graphics ga) {
		super.paint(ga);
		final Graphics2D g = (Graphics2D) ga;

		g.clearRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
		g.translate(100, 100);

		ABitmapTextRenderer t = new ABitmapTextRenderer(new Font("Arial", Font.PLAIN, 20)) {

			@Override
			protected void markDirty(Rectangle bounds) {

			}

			@Override
			protected Pair<Graphics2D, Dimension> createGraphics(Rectangle maxBounds) {
				return Pair.make(g, new Dimension(g.getClipBounds().width, g.getClipBounds().height));
			}
		};
	}

	public static void main(String[] args) {
		new BitmapTextRendererAWTTest();
	}

}
