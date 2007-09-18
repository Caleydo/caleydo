/*
 * @(#)JGraphHeavyweightRedirector.java 1.0 12-OCT-2004
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgraph.plugins.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.pad.graphcellsbase.cellviews.JGraphHeavyweightView;

/**
 * @author Gaudenz Alder
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class JGraphHeavyweightRedirector extends MouseInputAdapter {

	/**
	 * Specifies whether this class only redirects to heavyweight cells.
	 * Default is false. 
	 */
	protected boolean heavyweightOnly = false;

	/**
	 * Specifies whether this class only redirects to selected cells.
	 * Default is true.
	 */
	protected boolean selectedOnly = true;
	
	public JGraphHeavyweightRedirector() {
		this(false, true);
	}
	
	public JGraphHeavyweightRedirector(boolean heavyweightOnly, boolean selectedOnly) {
		this.heavyweightOnly = heavyweightOnly;
		this.selectedOnly = selectedOnly;
	}

    // catch all mouse events and redispatch them
    public void mouseMoved(MouseEvent e) {
      redispatchMouseEvent(e);
    }

    public void mouseDragged(MouseEvent e) {
      redispatchMouseEvent(e);
    }

    public void mouseClicked(MouseEvent e) {
      redispatchMouseEvent(e);
    }

    public void mouseEntered(MouseEvent e) {
      redispatchMouseEvent(e);
    }

    public void mouseExited(MouseEvent e) {
      redispatchMouseEvent(e);
    }

    public void mousePressed(MouseEvent e) {
      redispatchMouseEvent(e);
    }

    public void mouseReleased(MouseEvent e) {
      redispatchMouseEvent(e);
    }

	private void redispatchMouseEvent(final MouseEvent evt) {
		if (evt.getSource() instanceof JGraph) {
			final JGraph graph = (JGraph) evt.getSource();
			Point containerPoint = evt.getPoint();
			Object cell = graph.getFirstCellForLocation((int) containerPoint.getX(),
					(int) containerPoint.getY());
			if (cell != null && (!selectedOnly || graph.isCellSelected(cell))) {
				CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
				if (!heavyweightOnly || view instanceof JGraphHeavyweightView) {
					Component renderer = view.getRendererComponent(graph,
							false, false, false);
					final Rectangle2D rectBounds = view.getBounds();
					containerPoint.x -= rectBounds.getX();
					containerPoint.y -= rectBounds.getY();
					boolean isContainer = false;
					if (renderer instanceof Container) {
						isContainer = ((Container) renderer)
								.getComponentCount() > 1;
					}
					if (isContainer) {
						// TODO: JScrollPane is a special case
						renderer.setBounds(new Rectangle((int) rectBounds
								.getX(), (int) rectBounds.getY(),
								(int) rectBounds.getWidth(), (int) rectBounds
										.getHeight()));
						if (renderer instanceof JComponent) {	
							((JComponent) renderer).setPreferredSize(new Dimension(
								(int) rectBounds.getWidth(), (int) rectBounds
										.getHeight()));
						}
						// find the component under this point
						renderer = SwingUtilities.getDeepestComponentAt(
								renderer, containerPoint.x, containerPoint.y);

						if (renderer != null) {
							containerPoint.x -= renderer.getLocation().getX();
							containerPoint.y -= renderer.getLocation().getY();
						}

					}
					// redispatch the event
					if (renderer != null) {
						final Component component = renderer;
						final MouseEvent event = new MouseEvent(component, evt
								.getID(), evt.getWhen(), evt.getModifiers(),
								(int) containerPoint.getX(),
								(int) containerPoint.getY(), evt
										.getClickCount(), evt.isPopupTrigger(), evt.getButton());
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								component.dispatchEvent(event);
								graph.repaint(new Rectangle((int) rectBounds
										.getX(), (int) rectBounds.getY(),
										(int) rectBounds.getWidth(), (int) rectBounds
												.getHeight()));
							}
						});
					}
				}
			}
			evt.consume();
		}
	}
}

