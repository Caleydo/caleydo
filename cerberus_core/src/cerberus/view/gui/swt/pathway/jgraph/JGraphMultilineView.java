/*
 * @(#)JGraphEllipseView.java 1.0 12-MAY-2004
 * 
 * Copyright (c) 2001-2004, Jenya Burstein
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
 *
 */
package cerberus.view.gui.swt.pathway.jgraph;

//import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;


/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JGraphMultilineView extends VertexView {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -1057488833884340593L;
	
	protected static transient MultiLinedRenderer renderer = new MultiLinedRenderer();
    protected static transient MultiLinedEditor editor = new MultiLinedEditor();
    
    public JGraphMultilineView() {
        super();
    }
    
    public JGraphMultilineView(Object cell) {
        super(cell);
    }
    
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public GraphCellEditor getEditor() {
        return editor;
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
    
    public static class MultiLinedEditor extends DefaultGraphCellEditor {
    	
        /**
		 * 
		 */
		private static final long serialVersionUID = -687484239632778374L;

		public class RealCellEditor extends AbstractCellEditor implements GraphCellEditor {
			
            /**
			 * 
			 */
			private static final long serialVersionUID = 1494405876665957529L;
			
			JTextArea editorComponent = new JTextArea();
            @SuppressWarnings("serial")
			public RealCellEditor() {
                editorComponent.setBorder(UIManager.getBorder("Tree.editorBorder"));
                editorComponent.setLineWrap(true);
                editorComponent.setWrapStyleWord(true);

                //substitute a JTextArea's VK_ENTER action with our own that will stop an edit.
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
								KeyEvent.SHIFT_DOWN_MASK), "shiftEnter");
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
								KeyEvent.CTRL_DOWN_MASK), "metaEnter");
                editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
                editorComponent.getActionMap().put("enter", new AbstractAction(){
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                    }
                });
                AbstractAction newLineAction = new AbstractAction() {

					public void actionPerformed(ActionEvent e) {
						Document doc = editorComponent.getDocument();
						try {
							doc.insertString(editorComponent
									.getCaretPosition(), "\n", null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
                };
				editorComponent.getActionMap().put("shiftEnter",
						newLineAction);
				editorComponent.getActionMap().put("metaEnter",
						newLineAction);
            }

            
            
            public Component getGraphCellEditorComponent(
                    JGraph graph,
                    Object value,
                    boolean isSelected) {
                editorComponent.setText(value.toString());
                editorComponent.selectAll();
                return editorComponent;
            }

            public Object getCellEditorValue() {
                return editorComponent.getText();
            }

            public boolean stopCellEditing() {
                //set the size of a vertex to that of an editor.
                CellView view = graph.getGraphLayoutCache().getMapping(graph.getEditingCell(), false);
                Map map = view.getAllAttributes();
                Rectangle2D cellBounds = GraphConstants.getBounds(map);
                Rectangle editingBounds = editorComponent.getBounds();
                GraphConstants.setBounds(map, new Rectangle((int) cellBounds.getX(), (int) cellBounds.getY(), editingBounds.width, editingBounds.height));

                return super.stopCellEditing();
            }

            public boolean shouldSelectCell(EventObject event) {
				editorComponent.requestFocus();
				return super.shouldSelectCell(event);
			}
        }

        public MultiLinedEditor() {
            super();
        }
        /**
         * Overriding this in order to set the size of an editor to that of an edited view.
         */
        public Component getGraphCellEditorComponent(
                JGraph graph,
                Object cell,
                boolean isSelected) {

            Component component = super.getGraphCellEditorComponent(graph, cell, isSelected);

            //set the size of an editor to that of a view
			CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
			Rectangle2D tmp = view.getBounds();
			editingComponent.setBounds((int) tmp.getX(), (int) tmp.getY(),
					(int) tmp.getWidth(), (int) tmp.getHeight());

            //I have to set a font here instead of in the RealCellEditor.getGraphCellEditorComponent() because
            //I don't know what cell is being edited when in the RealCellEditor.getGraphCellEditorComponent().
            Font font = GraphConstants.getFont(view.getAllAttributes());
            editingComponent.setFont((font != null) ? font : graph.getFont());

            return component;
        }

        protected GraphCellEditor createGraphCellEditor() {
            return new MultiLinedEditor.RealCellEditor();
        }

        /**
         * Overriding this so that I could modify an editor container.
         * see http://sourceforge.net/forum/forum.php?thread_id=781479&forum_id=140880
         */
        protected Container createContainer() {
            return new MultiLinedEditor.ModifiedEditorContainer();
        }

        @SuppressWarnings("serial")
		class ModifiedEditorContainer extends EditorContainer {
            public void doLayout() {
                super.doLayout();
                //substract 2 pixels that were added to the preferred size of the container for the border.
                Dimension cSize = getSize();
                Dimension dim = editingComponent.getSize();
                editingComponent.setSize(dim.width - 2, dim.height);

                //reset container's size based on a potentially new preferred size of a real editor.
                setSize(cSize.width, getPreferredSize().height);
            }
        }
    }

    public static class MultiLinedRenderer extends JTextArea implements CellViewRenderer {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 4248607485548265343L;

		protected transient JGraph graph = null;

    	transient protected Color gradientColor = null;
    	
    	/** Cached hasFocus and selected value. */
    	transient protected boolean hasFocus,
    		selected,
    		preview;

    	public MultiLinedRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getRendererComponent(
                JGraph graph,
                CellView view,
                boolean sel,
                boolean focus,
                boolean preview) {
            setText(view.getCell().toString());
            this.graph = graph;
            this.selected = sel;
            this.preview = preview;
            this.hasFocus = focus;
            Map attributes = view.getAllAttributes();
            installAttributes(graph, attributes);
            return this;
        }
        
    	public void paint(Graphics g) {
			int b = 1;//borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			boolean tmp = selected;
			int roundRectArc = JGraphRoundRectView.getArcSize(d.width - b,
					d.height - b);
			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(), getHeight(), gradientColor, true));
				}
				g.fillRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
			try {
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			} finally {
				selected = tmp;
			}
//			if (bordercolor != null) {
//				g.setColor(bordercolor);
//				g2.setStroke(new BasicStroke(b));
//				g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
//						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
//			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(Color.RED); //highlightColor);
				g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
    		
//    		try {
//    			if (gradientColor != null && !preview) {
//    				setOpaque(false);
//    				Graphics2D g2d = (Graphics2D) g;
//    				g2d.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(),
//    						getHeight(), gradientColor, true));
//    				g2d.fillRect(0, 0, getWidth(), getHeight());
//    			}
//    			super.paint(g);
//    			paintSelectionBorder(g);
//    		} catch (IllegalArgumentException e) {
//    			// JDK Bug: Zero length string passed to TextLayout constructor
//    		}
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
            setFont((font != null) ? font : graph.getFont());
            Border border= GraphConstants.getBorder(attributes);
            Color bordercolor = GraphConstants.getBorderColor(attributes);
            if(border != null)
                setBorder(border);
            else if (bordercolor != null) {
                int borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
                setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
            }
            else if (bordercolor == null)//case with no border
            {
            	setBorder(BorderFactory.createLineBorder(null, 0));
            }
    		gradientColor = GraphConstants.getGradientColor(attributes);
        }
    }
  
}
