/*
 * Copyright (c) 2005, Mauro Tramacere
 * Copyright (c) 2001-2005, Gaudenz Alder
 * Copyirght (c) 2005, David Benson
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
 *
 */
package org.jgraph.pad.graphcellsbase.cellviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicLabelUI;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * A representation of a swimlane in the workflow sense. It is designed to
 * act as the parent group of cells that are contained within the swimlane.
 * The label of the cell is drawn vertically is the standard position
 * for a swimlane. 
 */
public class JGraphSwimlaneView extends VertexView {
	public static transient JSwimlaneRenderer renderer = new JSwimlaneRenderer();
	private static transient final int LABEL_SIZE=25;
	
	public JGraphSwimlaneView() {
		super();
	}

	public JGraphSwimlaneView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static int getLabelSize(){
		return LABEL_SIZE;
	}
	
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Rectangle2D bounds = getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
	}

	/* (non-Javadoc)
	 * Scale the Pool but not the children
	 */
	public void scale(double sx, double sy, Point2D origin) {
		getAttributes().scale(sx, sy, origin);
	}

	/*
	 * Implements swimlane label UI
	 */
	static class SwimlaneLabelUI extends BasicLabelUI
	{

		protected boolean clockwise;
		SwimlaneLabelUI( boolean clockwise ){
			super();
			this.clockwise = clockwise;
		}


		public Dimension getPreferredSize(JComponent c){
			Dimension dim = super.getPreferredSize(c);
			return new Dimension( dim.height, dim.width );
		}

		private static Rectangle paintIconR = new Rectangle();
		private static Rectangle paintTextR = new Rectangle();
		private static Rectangle paintViewR = new Rectangle();
		private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

		public void paint(Graphics g, JComponent c){
			JLabel label = (JLabel)c;
			String text = label.getText();
			Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

			if ((icon == null) && (text == null)) {
				return;
			}

			FontMetrics fm = g.getFontMetrics();
			paintViewInsets = c.getInsets(paintViewInsets);

			paintViewR.x = paintViewInsets.left;
			paintViewR.y = paintViewInsets.top;

			// Use inverted height & width
			paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
			paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

			paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
			paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

			String clippedText =
				layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

			Graphics2D g2 = (Graphics2D) g;
			AffineTransform tr = g2.getTransform();
			if( clockwise ) {
				g2.rotate( Math.PI / 2 );
				g2.translate( 0, - c.getWidth() );
			}
			else {
				g2.rotate( - Math.PI / 2 );
				g2.translate( - c.getHeight(), 0 );
			}

			if (icon != null) {
				icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
			}

			if (text != null) {
				int textX = paintTextR.x;
				int textY = paintTextR.y + fm.getAscent();

				if (label.isEnabled()) {
					paintEnabledText(label, g, clippedText, textX, textY);
				}
				else {
					paintDisabledText(label, g, clippedText, textX, textY);
				}
			}


			g2.setTransform( tr );
		}
	}

	/**
	 * The Swimlane renderer is based on a JPanel since the label is treated
	 * as a seperate component
	 */
	public static class JSwimlaneRenderer extends JPanel implements CellViewRenderer {

		/**
		 * Cache the current graph for drawing
		 */
		protected transient JGraph graph = null;

		private transient static JLabel label= new JLabel();
		private transient static JLabel container= new JLabel();
		private transient Rectangle2D rect;

		transient protected Color gradientColor = null;

		/** Cached hasFocus and selected value. */
		transient protected boolean hasFocus,
		selected,
		preview;

		public JSwimlaneRenderer() {
			super(new BorderLayout());
			//Set the VerticalUi to the label
			label.setUI( new SwimlaneLabelUI(false) );
			label.setPreferredSize(new Dimension(LABEL_SIZE,100));
			label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			this.add(label, BorderLayout.LINE_START);
			this.add(container, BorderLayout.CENTER);

		}


		public Component getRendererComponent(
				JGraph graph,
				CellView view,
				boolean sel,
				boolean focus,
				boolean preview) {
			label.setText(view.getCell().toString());
			this.graph = graph;
			this.selected = sel;
			this.preview = preview;
			this.hasFocus = focus;
			Map attributes = view.getAllAttributes();
			installAttributes(graph, attributes);
			return this;
		}

		/**
		 * Paint the renderer. Overrides superclass paint to add specific
		 * painting.
		 */
		public void paint(Graphics g) {
			try {
				if (gradientColor != null && !preview) {
					setOpaque(false);
					Graphics2D g2d = (Graphics2D) g;
					g2d.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(),	getHeight(), gradientColor, true));
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}
				super.paint(g);
				paintSelectionBorder(g);
			} catch (IllegalArgumentException e) {
				// JDK Bug: Zero length string passed to TextLayout constructor
			}
		}

		/**
		 * Provided for subclassers to paint a selection border.
		 */
		protected void paintSelectionBorder(Graphics g) {
			((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
			if (hasFocus && selected)
				g.setColor(graph.getLockedHandleColor());
			else if (selected)
				g.setColor(graph.getHighlightColor());
			if (selected) {
				Dimension d = getSize();
				g.drawRect(0, 0, d.width - 1, d.height - 1);
			}
		}

		protected void installAttributes(JGraph graph, Map attributes) {
			setOpaque(GraphConstants.isOpaque(attributes));
			Color foreground = GraphConstants.getForeground(attributes);
			setForeground((foreground != null) ? foreground : graph.getForeground());
			Color background = GraphConstants.getBackground(attributes);
			setBackground((background != null) ? background : graph.getBackground());
			Font font = GraphConstants.getFont(attributes);
			label.setFont((font != null) ? font : graph.getFont());
			rect =GraphConstants.getBounds(attributes);
			setBounds((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
			Border border= GraphConstants.getBorder(attributes);
			Color bordercolor = GraphConstants.getBorderColor(attributes);
			if(border != null){
				label.setBorder(border);
				container.setBorder(border);
			}
			else if (bordercolor != null) {
				int borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
				label.setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
				container.setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
			}
			gradientColor = GraphConstants.getGradientColor(attributes);
		}
	}
}
