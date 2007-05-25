/*
 * @(#)IconExample.java 1.0 28-SEPT-04
 * 
 * Copyright (c) 2001-2004, Dean Mao All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package org.jgraph.example;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.UndoableEditEvent;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;

/**
 * @author Dean Mao
 * 
 * Hint: Use <code>ant example</code> to run this example. Make sure you the
 * jgraph source distribution located at <code>../jgraph relative</code> to
 * this project's root directory (see <code>build.xml</code>).
 * 
 * @created Sep 28, 2004
 */
public class IconExample extends GraphEd {

	// Shared icon
	protected ImageIcon jgraphIcon = null;

	/**
	 * 
	 */
	public IconExample() {
		// Use Border Layout
		getContentPane().setLayout(new BorderLayout());
		// Construct the Graph
		graph = new IconGraph(new MyModel());

		// Construct Command History
		//
		// Create a GraphUndoManager which also Updates the ToolBar
		undoManager = new GraphUndoManager() {
			// Override Superclass
			public void undoableEditHappened(UndoableEditEvent e) {
				// First Invoke Superclass
				super.undoableEditHappened(e);
				// Then Update Undo/Redo Buttons
				updateHistoryButtons();
			}
		};

		// Add Listeners to Graph
		//
		// Register UndoManager with the Model
		graph.getModel().addUndoableEditListener(undoManager);
		// Update ToolBar based on Selection Changes
		graph.getSelectionModel().addGraphSelectionListener(this);
		// Listen for Delete Keystroke when the Graph has Focus
		graph.addKeyListener(this);

		// Construct Panel
		//
		// Add a ToolBar
		getContentPane().add(createToolBar(), BorderLayout.NORTH);
		// Add the Graph as Center Component
		getContentPane().add(new JScrollPane(graph), BorderLayout.CENTER);

		//
		// Load Icon
		String iconPath = "org/jgraph/example/resources/jgraph.gif";
		URL jgraphUrl = IconExample.class.getClassLoader()
				.getResource(iconPath);
		// If Valid URL
		if (jgraphUrl != null) {
			// Load Icon
			jgraphIcon = new ImageIcon(jgraphUrl);
		} else {
			throw new RuntimeException(
					"Can't load without the default icon file!  I tried to find: "
							+ iconPath);
		}
	}

	public Map createCellAttributes(Point2D point) {
		Map map = super.createCellAttributes(point);
		GraphConstants.setIcon(map, jgraphIcon);
		return map;
	}

	protected DefaultGraphCell createDefaultGraphCell() {
		return new CustomCell(jgraphIcon, "default\ndescription");
	}

	/**
	 * Define a custom graph that implements the CellViewFactory method,
	 * createView(), so that we can create our custom icon/description vertex.
	 * 
	 * @author Dean Mao
	 * @created Sep 28, 2004
	 */
	public class IconGraph extends GraphEd.MyGraph {
		public IconGraph(GraphModel model) {
			super(model);
			getGraphLayoutCache().setAutoSizeOnValueChange(true);
			getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
				public CellView createView(GraphModel model, Object c) {
					CellView view = null;
					if (c instanceof CustomCell) {
						return new JGraphIconView(c);
					} else if (c instanceof Port) {
						view = new InvisiblePortView(c);
					} else {
						view = super.createView(model, c);
					}
					return view;
				}
			});
		}
	}

	/**
	 * CustomCell that allows user to define an icon and a description for the
	 * graph vertex.
	 * 
	 * @author Dean Mao
	 * @created Sep 28, 2004
	 */
	public class CustomCell extends DefaultGraphCell {
		private ImageIcon icon;

		private String description;

		public CustomCell(ImageIcon icon, String description) {
			this.icon = icon;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public ImageIcon getIcon() {
			return icon;
		}

		/**
		 * Sets the description on a cell. This is called from the multi-lined
		 * editor.
		 */
		public void setUserObject(Object obj) {
			if (obj != null && obj instanceof String) {
				this.description = obj.toString();
			}
		}

		/**
		 * Return the description of the cell so that it will be the initial
		 * value of the in-graph editor.
		 */
		public String toString() {
			return description;
		}
	}

	/**
	 * This "invisible port" is the same size as the icon on the
	 * icon/description vertex. We do this by navigating up the tree to get the
	 * CellView, then the CustomCell to get the actual icon height/width. Keep
	 * in mind that we are also changing the location of the port such that it
	 * is in the same place as the icon displayed on the screen.
	 * 
	 * The port renderer is designed so that it doesn't paint anything.
	 * 
	 * @author Dean Mao
	 * @created Sep 28, 2004
	 */
	public class InvisiblePortView extends PortView {

		public InvisiblePortView(Object cell) {
			super(cell);
		}

		public Rectangle2D getBounds() {
			Rectangle2D parentBounds = getParentView().getBounds();
			double height = ((CustomCell) getParentView().getCell()).getIcon()
					.getIconHeight();
			double width = ((CustomCell) getParentView().getCell()).getIcon()
					.getIconWidth();
			double x = parentBounds.getX()
					+ ((parentBounds.getWidth() - width) / 2);
			double y = parentBounds.getY() + 5;
			return new Rectangle2D.Double(x, y, width, height);
		}

		public CellViewRenderer getRenderer() {
			return portRenderer;
		}

		public Point2D getLocation(EdgeView edge) {
			if (edge == null)
				return new Point2D.Double(this.getBounds().getCenterX(), this
						.getBounds().getCenterY());
			else
				return super.getLocation(edge);
		}
	}

	protected static InvisiblePortRenderer portRenderer = new InvisiblePortRenderer();

	public static class InvisiblePortRenderer extends PortRenderer {
		public void paint(Graphics g) {
			if (preview)
				super.paint(g);
			// else: null implementation (ie, don't paint anything!!)
		}
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		// Construct Frame
		JFrame frame = new JFrame("IconExample");
		// Set Close Operation to Exit
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add an Editor Panel
		frame.getContentPane().add(new IconExample());
		// Fetch URL to Icon Resource
		URL jgraphUrl = IconExample.class.getClassLoader().getResource(
				"org/jgraph/example/resources/jgraph.gif");
		// If Valid URL
		if (jgraphUrl != null) {
			// Load Icon
			ImageIcon jgraphIcon = new ImageIcon(jgraphUrl);
			// Use in Window
			frame.setIconImage(jgraphIcon.getImage());
		}
		// Set Default Size
		frame.setSize(520, 390);
		// Show Frame
		frame.setVisible(true);
	}
}