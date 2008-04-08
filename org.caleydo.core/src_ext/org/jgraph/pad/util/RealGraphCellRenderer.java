/*
 * @(#)RealGraphCellRenderer.java	1.2 11/11/02
 *
 * Copyright (C) 2001 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jgraph.pad.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;

public class RealGraphCellRenderer extends JComponent {

	protected CellRendererPane rendererPane;
	protected CellView[] views;
	protected JGraph graph;
	protected double scale = 1.0;

	public RealGraphCellRenderer(JGraph graph, CellView[] views) {
		add(rendererPane = new CellRendererPane());
		this.views = views;
		this.graph = graph;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public Dimension getPreferredSize() {
		if (views != null) {
			Rectangle2D r = (AbstractCellView.getBounds(views));
			double scaleFix = scale;
			if (views[0] instanceof EdgeView)
				scaleFix = 0.04;
			r.setRect(r.getX(),
					  r.getY(),
					  r.getWidth() * scale,
					  r.getHeight() * scaleFix );//TODO: it was scale before but that was strange, what is correct?
			return new Dimension((int)(r.getWidth() + 2),
					             (int)(r.getHeight() + 2));
		}
		return new Dimension(10, 10);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform at = g2.getTransform();
		g2.scale(scale, scale);
		if (views != null) {
			Rectangle2D r = AbstractCellView.getBounds(views);
			g.translate((int)-r.getX(), (int)-r.getY());
			for (int i = 0; i < views.length; i++) {
				Rectangle2D b = views[i].getBounds();
				Component c;
				boolean isGroup;
		        CellView view = graph.getGraphLayoutCache().getMapping(views[i].getCell(), false);
		        if (view != null)
		        	isGroup = !view.isLeaf();
		        else
		        	isGroup = false;
				if (isGroup)
					c =
						new RealGraphCellRenderer(
							graph,
							(views[i]).getChildViews());
				else
					c =
						views[i].getRendererComponent(
							graph,
							false,
							false,
							false);
				rendererPane.paintComponent(
					g,
					c,
					this,
					(int)b.getX(),
					(int)b.getY(),
					(int)b.getWidth(),
					(int)b.getHeight());
			}
		}
		g2.setTransform(at);
	}

}