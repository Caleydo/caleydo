/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.pad.coreframework.jgraphsubclassers;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.coreframework.GPBarFactory;
import org.jgraph.pad.coreframework.GPDocument;
import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.coreframework.GPPluginInvoker.DocAwarePlugin;
import org.jgraph.pad.util.IEdgeFactory;
import org.jgraph.pad.util.IVertexFactory;

/**
 * MarqueeHandler that can insert cells. Vertex and Edge are created in
 * a polymorphic way. Thus you propably don't need to subclass it when reusing
 * GPGraphpad, rather register your custom shape buttons and popup menu in the factory.
 *  
 *  @see org.jgraph.pad.coreframework.GPGraphpadModel
 *  @see org.jgraph.pad.actions.celltoolboxes.AbstractDefaultEdgeCreator
 *  @see org.jgraph.pad.actions.celltoolboxes.AbstractDefaultVertexnPortsCreator
 */
public class GPMarqueeHandler extends BasicMarqueeHandler implements DocAwarePlugin {

	protected int m_XDifference, m_YDifference, dx, dy;

	protected boolean m_dragging;

	protected Container c;

	/**
	 * A reference to the graphpad object
	 */
	protected GPDocument document;

	/**
	 * The default color for borders
	 */
	protected transient Color defaultBorderColor = Color.black;

	protected transient JToggleButton buttonSelect;

	protected transient JToggleButton buttonZoomArea;

	protected Point2D start, current;

	protected Rectangle2D bounds;

	protected PortView port;

	protected PortView firstPort;

	protected PortView lastPort;
	
	protected static GPMarqueeHandler _instance;
	
	protected ArrayList edgeCreators;

	protected ArrayList vertexnPortsCreators;

	/**
	 * A boolean telling wether or not a button creating an Edge or a Vertex
	 * should be remanent or not. If true, then you will continue with this
	 * button selectionned while if false you will the selection action will be
	 * restored after you added a cell.
	 */
	protected boolean remanent = false;
	
	public GPMarqueeHandler() {
		super();
	}
    
    public void setDocument(GPDocument document) {
        this.document = document;
        // here we group the buttons in order
        // that only one can be selected at the same time:
        ButtonGroup grp = new ButtonGroup();
        buttonSelect = (JToggleButton) ((GPAbstractActionDefault) document.getCommand("ToolBoxSelect")).getToolComponent(null);
        buttonZoomArea = (JToggleButton) ((GPAbstractActionDefault) document.getCommand("ToolBoxZoomArea")).getToolComponent(null);
        grp.add(buttonSelect);
        grp.add(buttonZoomArea);
        buttonSelect.setSelected(true);// default
        // buttons for vertex:
        for (int i = 0; i < document.getVertexnPortsCreators().size(); i++) {
            grp.add(((IVertexFactory) document.getVertexnPortsCreators().get(i)).getButton());
        }

        // buttons for edges:
        for (int i = 0; i < document.getEdgeCreators().size(); i++) {
            grp.add(((IEdgeFactory) document.getEdgeCreators().get(i)).getButton());
        }
    }

	/* Return true if this handler should be preferred over other handlers. */
	public boolean isForceMarqueeEvent(MouseEvent e) {
		return !buttonSelect.isSelected() || isPopupTrigger(e)
				|| super.isForceMarqueeEvent(e);
	}

	protected boolean isPopupTrigger(MouseEvent e) {
		if (e == null)
			return false;
		return SwingUtilities.isRightMouseButton(e) && !e.isShiftDown();
	}

	public void mousePressed(MouseEvent event) {
		m_XDifference = event.getX();
		m_YDifference = event.getY();
		dx = 0;
		dy = 0;
		if (!isPopupTrigger(event) && !event.isConsumed()
				&& !buttonSelect.isSelected()) {
			start = document.getGraph().snap(event.getPoint());
			firstPort = port;
			if (port != null) {
				start = document.getGraph().toScreen(port.getLocation());
			}
			if ((isEdgeToolBoxSelected() >= 0) && firstPort != null)
				start = document.getGraph().toScreen(
						firstPort.getLocation());
			event.consume();
		}
		if (!isPopupTrigger(event)) {
			super.mousePressed(event);
			event.consume();
		} else {
			boolean selected = false;
			Object[] cells = document.getGraph().getSelectionCells();
			for (int i = 0; i < cells.length && !selected; i++)
				selected = document.getGraph().getCellBounds(cells[i])
						.contains(event.getPoint());
			if (!selected)
				document.getGraph().setSelectionCell(
						document.getGraph().getFirstCellForLocation(
								event.getX(), event.getY()));
			event.consume();
		}
	}

	public void mouseDragged(MouseEvent event) {
		if (!event.isConsumed() && !buttonSelect.isSelected()) {
			Graphics g = document.getGraph().getGraphics();
			Color bg = document.getGraph().getBackground();
			Color fg = Color.black;
			g.setColor(fg);
			g.setXORMode(bg);
			PortView newPort = getPortViewAt(event.getX(), event.getY());
			JGraph graph = document.getGraph();
			overlay(graph, g, true);
			current = document.getGraph().snap(event.getPoint());
			if (isEdgeToolBoxSelected() >= 0) {
				if (newPort != null) {
					if (newPort != firstPort)
						current = document.getGraph().toScreen(
								newPort.getLocation());
				}
			}
			if (start != null && current != null) {
				if ((newPort != port || newPort == null || newPort == firstPort)
						&& (isEdgeToolBoxSelected() >= 0)) {
					port = newPort;
				}
				Point tempStart = new Point((int) start.getX(), (int) start
						.getY());
				Point tempCurrent = new Point((int) current.getX(),
						(int) current.getY());
				bounds = new Rectangle(tempStart).union(new Rectangle(
						tempCurrent));
				g.setColor(bg);
				g.setXORMode(fg);
			}
			overlay(graph, g, false);
			event.consume();
		} else if (!event.isConsumed() && isForceMarqueeEvent(event)
				&& isPopupTrigger(event)) {
			c = document.getGraph().getParent();
			if (c instanceof JViewport) {
				JViewport jv = (JViewport) c;
				Point p = jv.getViewPosition();
				int newX = p.x - (event.getX() - m_XDifference);
				int newY = p.y - (event.getY() - m_YDifference);
				dx += (event.getX() - m_XDifference);
				dy += (event.getY() - m_YDifference);

				int maxX = document.getGraph().getWidth()
						- jv.getWidth();
				int maxY = document.getGraph().getHeight()
						- jv.getHeight();
				if (newX < 0)
					newX = 0;
				if (newX > maxX)
					newX = maxX;
				if (newY < 0)
					newY = 0;
				if (newY > maxY)
					newY = maxY;

				jv.setViewPosition(new Point(newX, newY));
				event.consume();
			}
		}
		if (!event.isConsumed()) {
			super.mouseDragged(event);
			event.consume();
		}
	}

	// Default Port is at index 0
	public PortView getPortViewAt(int x, int y) {
		return document.getGraph().getPortViewAt(x, y);
	}

	public void mouseReleased(MouseEvent event) {
		// creation of a vertex if an appropriate button is pressed:
		if (bounds != null) {
			int vertexToAddIndex = isVertexToolBoxSelected();
			if (vertexToAddIndex >= 0) {
				((IVertexFactory) document.getVertexnPortsCreators().get(vertexToAddIndex)).addVertexnPorts(bounds);
			if (!remanent)
				buttonSelect.doClick();
			}
		}

		if (isPopupTrigger(event)) {
			if (Math.abs(dx) < document.getGraph().getTolerance()
					&& Math.abs(dy) < document.getGraph().getTolerance()) {
				Object cell = document.getGraph()
						.getFirstCellForLocation(event.getX(), event.getY());
				if (cell == null)
					document.getGraph().clearSelection();
				Container parent = document.getGraph();
				do {
					parent = parent.getParent();
				} while (parent != null && !(parent instanceof GPGraphpad));

				GPGraphpad pad = (GPGraphpad) parent;
				if (pad != null) {
					JPopupMenu pop = GPBarFactory.getInstance().createPopupMenu(document.getGraph(), document);
					pop.show(document.getGraph(), event.getX(), event
							.getY());
				}
			}
			event.consume();
		} else if (event != null && !event.isConsumed() && bounds != null
				&& !buttonSelect.isSelected()) {
			document.getGraph().fromScreen(bounds);
			bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth() + 1,
					bounds.getHeight() + 1);
			boolean doNotConsume = false;
			if (buttonZoomArea.isSelected()) {
				Rectangle view = document.getGraph().getBounds();
				if (document.getGraph().getParent() instanceof JViewport)
					view = ((JViewport) document.getGraph().getParent())
							.getViewRect();
				if (bounds.getWidth() != 0 && bounds.getHeight() != 0
						&& SwingUtilities.isLeftMouseButton(event)) {
					double scale = Math.min(view.width
							/ bounds.getWidth(), view.height
							/ bounds.getHeight());
					if (scale > 0.1) {
						document.getGraph().setScale(scale);
						document.getGraph().scrollRectToVisible(
								(Rectangle) (document.getGraph()
										.toScreen(bounds)));
					}
				} else
					document.getGraph().setScale(1);
				// FIX: Set ResizeAction to null!
			}

			else if (isEdgeToolBoxSelected() >= 0) {
				((IEdgeFactory) document.getEdgeCreators().get(isEdgeToolBoxSelected())).addEdge(
						start, current, firstPort, port);
				if (!remanent)
					buttonSelect.doClick();
			} else
				doNotConsume = true;
			if (!doNotConsume)
				event.consume();
		}
		if (event != null && event.isShiftDown() && event.isControlDown())
			buttonSelect.doClick();
		if (event != null) {
			if (!event.isConsumed()) {
				super.mouseReleased(event);
			}
		}
		firstPort = null;
		port = null;
		start = null;
		current = null;
		bounds = null;
	}

	public void mouseMoved(MouseEvent event) {
		if (!buttonSelect.isSelected() && !event.isConsumed()) {
			document.getGraph().setCursor(
					new Cursor(Cursor.CROSSHAIR_CURSOR));
			event.consume();
			if (isEdgeToolBoxSelected() >= 0) {
				PortView oldPort = port;
				PortView newPort = getPortViewAt(event.getX(), event.getY());
				if (oldPort != newPort) {
					Graphics g = document.getGraph().getGraphics();
					Color bg = document.getGraph().getBackground();
					Color fg = document.getGraph().getMarqueeColor();
					g.setColor(fg);
					g.setXORMode(bg);
					JGraph gpgraph = document.getGraph();
					overlay(gpgraph, g, true);
					port = newPort;
					g.setColor(bg);
					g.setXORMode(fg);
					overlay(gpgraph, g, false);
				}
			}
		}
		super.mouseMoved(event);
	}

	public void overlay(JGraph gpgraph, Graphics g, boolean clear) {
		super.overlay(gpgraph, g, clear);
		if (bounds != null && start != null) {
			if (buttonZoomArea.isSelected())
				((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
			else if ((isEdgeToolBoxSelected() >= 0) && current != null)
				g.drawLine((int) start.getX(), (int) start.getY(),
						(int) current.getX(), (int) current.getY());
			else if (!buttonSelect.isSelected())
				g.drawRect((int) bounds.getX(), (int) bounds.getY(),
						(int) bounds.getWidth(), (int) bounds.getHeight());
		}
	}

	protected int isEdgeToolBoxSelected() {//TODO optimize!
		for (int i = 0; i < document.getEdgeCreators().size(); i++) {
			if (((IEdgeFactory) document.getEdgeCreators().get(i)).getButton().isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	protected int isVertexToolBoxSelected() {//TODO optimize!
		for (int i = 0; i < document.getVertexnPortsCreators().size(); i++) {
			if (((IVertexFactory) document.getVertexnPortsCreators().get(i)).getButton().isSelected()) {
				return i;
			}
		}
		return -1;
	}

	public PortView getPort() {
		return port;
	}

	public void setPort(PortView port) {
		this.port = port;
	}

	public boolean isM_dragging() {
		return m_dragging;
	}

	public void setM_dragging(boolean m_dragging) {
		this.m_dragging = m_dragging;
	}

	public boolean isRemanent() {
		return remanent;
	}

	public void setRemanent(boolean remanent) {
		this.remanent = remanent;
	}

	public GPDocument getDocument() {
		return document;
	}
}