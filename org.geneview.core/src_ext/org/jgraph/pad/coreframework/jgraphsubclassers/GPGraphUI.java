package org.jgraph.pad.coreframework.jgraphsubclassers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphTransferHandler;
import org.jgraph.graph.GraphTransferable;
import org.jgraph.graph.ParentMap;
import org.jgraph.pad.coreframework.GPDocument;
import org.jgraph.pad.coreframework.GPUserObject;
import org.jgraph.pad.coreframework.GPPluginInvoker.DocAwarePlugin;
import org.jgraph.pad.graphcellsbase.cells.ImageCell;
import org.jgraph.pad.graphcellsbase.cells.TextCell;
import org.jgraph.pad.util.ImageIconBean;
import org.jgraph.plaf.basic.BasicGraphUI;

/**
 * The base class JGraph BasicGraphUI subclasser for GPGraphpad.
 * It allows to render backround pictures and handles the copy/paste outside
 * from GPGraphpad.
 */
public class GPGraphUI extends BasicGraphUI implements DocAwarePlugin {
	
	GPDocument document;
    
    public void setDocument(GPDocument document) {
        this.document = document;
    }
    
    public GPGraphUI() {
    		super();
    }

	//
	// Override Parent Methods
	//
	// @jdk14
	protected TransferHandler createTransferHandler() {
		return new GPTransferHandler();
	}

	/**
	 * Paint the background of this graph. Calls paintGrid.
	 */
	protected void paintBackground(Graphics g) {
		Rectangle pageBounds = graph.getBounds();
		if (document == null)
			return;
		if (document.getBackgroundImage() != null) {
			// Use clip and pageBounds
			double s = graph.getScale();
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform tmp = g2.getTransform();
			g2.scale(s, s);
			g.drawImage(document.getBackgroundImage(), 0, 0, graph);
			g2.setTransform(tmp);
		} else if (document.isPageVisible()) { // FIX: Use clip
			int w = (int) (document.getPageFormat().getWidth());
			int h = (int) (document.getPageFormat().getHeight());
			Point2D p = graph.toScreen(new Point(w, h));
			w = (int) p.getX();
			h = (int) p.getY();
			g.setColor(graph.getHandleColor());
			g.fillRect(0, 0, graph.getWidth(), graph.getHeight());
			g.setColor(Color.darkGray);
			g.fillRect(3, 3, w, h);
			g.setColor(graph.getBackground());
			g.fillRect(1, 1, w - 1, h - 1);
			pageBounds = new Rectangle(0, 0, w, h);
		}
		if (graph.isGridVisible())
			paintGrid(graph.getGridSize(), g, pageBounds);
	}

	/**
	 * TransferHandler that can import text.
	 */
	public class GPTransferHandler extends GraphTransferHandler {

		protected GraphTransferable create(
			JGraph graph,
			Object[] cells,
			Map viewAttributes,
			Rectangle2D bounds,
			ConnectionSet cs,
			ParentMap pm) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.getJGraphpadCEFile().saveFile(output);
			return new GPTransferable(output.toString(), cells, viewAttributes, bounds, cs, pm);
		}
		
		public boolean canImport(JComponent comp, DataFlavor[] flavors) {
			return true;
		}

		//public boolean importData(JComponent comp, Transferable t) {
		public boolean importDataImpl(JComponent comp, Transferable t) {
			if (super.importDataImpl(comp, t)) {
				return true;
			} else if (graph.isDropEnabled() && comp instanceof JGraph) {
				try {
					JGraph graph = (JGraph) comp;
					//TODO JGraph graph = (JGraph) comp;
					// Drop Files from OS
					if (t
						.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						Point2D p = null;
						if (getInsertionLocation() != null)
							p =
								graph.fromScreen(
									graph.snap(
										new Point(getInsertionLocation())));
						double gs = graph.getGridSize();
						if (p == null)
							p = new Point((int)gs, (int)gs);
						Map attributes = new Hashtable();
						java.util.List cells = new LinkedList();
						java.util.List fileList =
							(java.util.List) t.getTransferData(
								DataFlavor.javaFileListFlavor);
						Iterator iterator = fileList.iterator();
						while (iterator.hasNext()) {
							File file = (File) iterator.next();

							Hashtable hashtable = new Hashtable();
							hashtable.put("name", file.getName());
							//hashtable.put(
							//	"url",
							//	"file://" + file.getAbsolutePath());
							hashtable.put(GPUserObject.keyURI, file.toURL().toString());
							hashtable.put("path", file.getAbsolutePath());

							// Try icon 																					// 1.3, 1.4 use imageIO
							URL url = file.toURL();
							ImageIcon icon = null;
							String name = file.getName();
							if (url.toString().toLowerCase().endsWith(".gif")
								|| url.toString().toLowerCase().endsWith(".jpg")
								|| url.toString().toLowerCase().endsWith(".jpeg")
								|| url.toString().toLowerCase().endsWith(".png")) {
								icon = new ImageIconBean(url);
								name = "";
							}

							Object userObject = new GPUserObject(name, hashtable);//TODO will break custom user object implementation
							//Object userObject = null;//TODO change back
							DefaultGraphCell cell;
							
							if (icon != null)
								cell = new ImageCell(userObject);
							else
								cell = new DefaultGraphCell(userObject);
							cell.add(new DefaultPort());

							Dimension d = new Dimension((int) gs, (int) (2 * gs));
							if (icon == null) {
								GraphConstants.setResize(cell.getAttributes(), true);
							} else
								d =
									new Dimension(
										icon.getIconWidth(),
										icon.getIconHeight());

							AttributeMap map = new AttributeMap();
							GraphConstants.setBounds(map,
									                 new Rectangle((Point)p, d));
							if (icon != null)
								GraphConstants.setIcon(map, icon);
							else
								GraphConstants.setBorderColor(map, Color.black);
							attributes.put(cell, map);
							cells.add(cell);
							((Point)p).translate(0, (int)(d.getHeight() + (int) (1.5 * gs)));
							graph.snap(p);
						}
						if (!cells.isEmpty()) {
							graph.getGraphLayoutCache().insert(
								cells.toArray(),
								attributes,
								null,
								null,
								null);
							graph.requestFocus();
							return true;
						}

					// Try to drop as text
					} else {
						Object cell = null;
						AttributeMap map = new AttributeMap();
						DataFlavor bestFlavor =
							DataFlavor.selectBestTextFlavor(
								t.getTransferDataFlavors());
						if (bestFlavor != null && cell == null) {
							Reader reader = bestFlavor.getReaderForText(t);
							StringBuffer s = new StringBuffer();
							char[] c = new char[1];
							while (reader.read(c) != -1)
								s.append(c);
							Point2D p =
								graph.fromScreen(
									graph.snap(
										new Point(getInsertionLocation())));
							double gs = graph.getGridSize();
							if (p == null)
								p = new Point((int)gs, (int)gs);
							Hashtable props = new Hashtable();
							if (s.toString().startsWith("http:") ||
									s.toString().startsWith("mailto:") ||
									s.toString().startsWith("ftp:") ||
									s.toString().startsWith("telnet:") ||
									s.toString().startsWith("gopher:")||
									s.toString().startsWith("https:")||
									s.toString().startsWith("webdav:"))
								props.put(GPUserObject.keyURI, s.toString());
							cell = new TextCell(new GPUserObject(s.toString(), props));//TODO will break custom user object implementation
							//cell = new TextCell(null);//TODO change back
							((DefaultGraphCell) cell).add(new DefaultPort());
							
							CellView view =
								graphLayoutCache.getMapping(cell, true);
							Dimension2D d =
								graph.snap(getPreferredSize(graph, view));
							if (d == null)
								d = new Dimension((int)(2 * gs), (int)(2 * gs));
							GraphConstants.setBounds(map,
									                 new Rectangle((Point)p, (Dimension)d));
						}
						if (cell != null) {
							Map viewMap = new Hashtable();
							viewMap.put(cell, map);
							graph.getModel().insert(
								new Object[] { cell },
								viewMap,
								null,
								null,
								null);
						}
					}
				} catch (Exception exception) {
					//System.out.println(
					//	"Cannot import data: " + exception.getMessage());
				}
			}
			return false;
		}

	}

    public GPDocument getDocument() {
        return document;
    }
}
