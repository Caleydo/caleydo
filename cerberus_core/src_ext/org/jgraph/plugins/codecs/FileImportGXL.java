/*
 * @(#)FileExportGXL.java	1.2 01.02.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
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
package org.jgraph.plugins.codecs;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.coreframework.GPUserObject;
import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;
import org.jgraph.plugins.layouts.JGraphLayoutRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileImportGXL extends GPAbstractActionDefault {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		FileDialog f = new FileDialog(graphpad.getFrame(), "GXL File", FileDialog.LOAD);
		f.setVisible(true);
		if (f.getFile() == null)
			return;

		try {
			String file = f.getDirectory() + f.getFile();
			JGraph graph = getCurrentGraph();
			
			// Check in case there is no document available
			if ( null == graph ) {
				graphpad.addDocument(null, null);
				graph = getCurrentGraph();
			}
			
			JGraphGXLCodec.decode(new FileInputStream(file) , graph);
		} catch (Exception ex) {
			graphpad.error(ex.toString());
		}
	}

	//
	//
	// IMPORT GXL
	//
	//

    /**
     * Extracts visual properties of the node from the child 'view' element
     * Currently recognized properties:
     * - font, attrs: name, size, style(plain, bold, italic)
     * - color
     * - background-color
     * - autosize
     */
    static void fetchNodeViewProperties(Node gnode, Map gnode_attrs) {
        NodeList gnode_children = gnode.getChildNodes();
        for (int gnode_child_i = 0; gnode_child_i < gnode_children.getLength(); gnode_child_i++) {
            Node gnode_child = gnode_children.item(gnode_child_i);
            if (gnode_child.getNodeName().equals("view")) { // View properties of the node
                Element node_view = (Element)gnode_child;
                Font font = GraphConstants.DEFAULTFONT;
                String fontName = null;
                if (node_view.getAttribute("font-name") != null) {
                    fontName = node_view.getAttribute("font-name").toString();
                }
                float fontSize = font.getSize2D();
                if (node_view.getAttribute("font-size") != null) {
                    try {
                        fontSize = Float.parseFloat(node_view.getAttribute("font-size"));
                    } catch (NumberFormatException nfe) {
                        // Will use default size
                    }
                }
                int styleMask = 0;
                if (node_view.getAttribute("font-style") != null) {
                    String style = node_view.getAttribute("font-style");
                    if (style.equals("plain")) {
						styleMask = Font.PLAIN;
                    }
                    if (style.indexOf("italic") != -1) {
						styleMask += Font.ITALIC;
                    }
                    if (style.indexOf("bold") != -1) {
						styleMask += Font.BOLD;
                    }
                }
                if (fontName != null)
                	font = new Font(fontName, styleMask, (int) fontSize);
                else
                	font = font.deriveFont(styleMask, fontSize);
                GraphConstants.setFont(gnode_attrs, font);
                if (node_view.getAttribute("color") != null) {
                    try {
                        int color = Integer.parseInt(node_view.getAttribute("color"));
                        GraphConstants.setForeground(gnode_attrs, new Color(color));
                        GraphConstants.setBorderColor(gnode_attrs, new Color(color));
                        GraphConstants.setLineColor(gnode_attrs, new Color(color));
                    } catch (NumberFormatException nfe) {
                    }
                }
                if (node_view.getAttribute("background-color") != null) {
                    try {
                        int color = Integer.parseInt(node_view.getAttribute("background-color"));
                        GraphConstants.setBackground(gnode_attrs, new Color(color));
                    } catch (NumberFormatException nfe) {
                    }
                }
                if (node_view.getAttribute("auto-size") != null) {
                   GraphConstants.setAutoSize(gnode_attrs, "true".equals(node_view.getAttribute("auto-size")));
                }
            }
        }
    }
    static void fetchEdgeViewProperties(Node enode, Map enode_attrs) {
        fetchNodeViewProperties(enode, enode_attrs);
    }

	public static void parseGXLFileInto(
		String name,
		JGraph graph) throws Exception {
            String defaultLayout = null;
		GraphModel model = graph.getModel();
		File f = new File(name);
		// Create a DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// Create a DocumentBuilder
		DocumentBuilder db = dbf.newDocumentBuilder();
		// Parse the input file to get a Document object
		Document doc = db.parse(f);
		// Get the first child (the graph-element)
                // List for the new Cells
                List newCells = new ArrayList();
                // ConnectionSet for the Insert method
                ConnectionSet cs = new ConnectionSet();
                // Hashtable for the ID lookup (ID to Vertex)
                Map ids = new Hashtable();
                // Hashtable for Attributes (Vertex to Map)
                Hashtable attributes = new Hashtable();

                Element gxl = doc.getDocumentElement(); // First gxl element

                NodeList graph_list = gxl.getChildNodes();
                if (graph_list.getLength() == 0) {
                    return;
                }
                for (int graph_index = 0; graph_index < graph_list.getLength(); graph_index++) {
                    Node graph_node = graph_list.item(graph_index);
                    if (graph_node.getNodeName().equals("graph")) {
                        Element graph_elem = (Element)graph_node;
                        NodeList list = graph_elem.getChildNodes();
                        boolean defaultDirected = "directed".equals(graph_elem.getAttribute("edgemode")) ||
                            "defaultdirected".equals(graph_elem.getAttribute("edgemode"));
                        // End of Opheamro

                        // Get Graph's Child Nodes (the cells)


                        // Loop Children
                        for (int i = 0; i < list.getLength(); i++) {
                            Node node = list.item(i);
                            // Fetch Label
                            String label = getLabel(node);
                            // If Valid Node
                            if (node.getAttributes() != null && node.getNodeName() != null) {
                                // Fetch Type
                                String type = node.getNodeName().toString().toLowerCase();

                                // Create Vertex
                                if (type.equals("node")) {
                                    // Fetch ID Node
                                    String id = null;
                                    Node tmp = node.getAttributes().getNamedItem("id");
                                    // Fetch ID Value
                                    if (tmp != null)
                                        id = tmp.getNodeValue();
                                    // Need unique valid ID
                                    if (id != null && !ids.keySet().contains(id)) {
                                        // Create Vertex with label
                                        DefaultGraphCell vertex = new DefaultGraphCell(new GPUserObject(label));
                                        // Add One Floating Port
                                        vertex.add(new DefaultPort());
                                        // Add ID, Vertex pair to Hashtable
                                        ids.put(id, vertex);
                                        // Add Default Attributes
                                        Map node_attrs = createDefaultAttributes();
                                        fetchNodeViewProperties(node, node_attrs);
                                        attributes.put(vertex, node_attrs);
                                        // Add Vertex to new Cells
                                        newCells.add(vertex);
                                    }

                                    // Create Edge
                                } else if (type.equals("edge")) {
                                    Element edge_node = (Element)node;
                                    // Fetch Source ID Node
                                    Node tmp = node.getAttributes().getNamedItem("from");
                                    // Fetch Source ID Value
                                    String source = null;
                                    if (tmp != null)
                                        source = tmp.getNodeValue();
                                    // Fetch Target ID Node
                                    tmp = node.getAttributes().getNamedItem("to");
                                    // Fetch Target ID Value
                                    String target = null;
                                    if (tmp != null)
                                        target = tmp.getNodeValue();
                                    // Create Edge with label
                                    DefaultEdge edge = new DefaultEdge(new GPUserObject(label));
                                    // Find Source Port
                                    if (source != null) {
                                        // Fetch Vertex for Source ID
                                        DefaultGraphCell vertex =
                                            (DefaultGraphCell) ids.get(source);
                                        if (vertex != null)
                                            // Connect to Source Port
                                            cs.connect(edge, vertex.getChildAt(0), true);
                                    }
                                    // Find Target Port
                                    if (target != null) {
                                        // Fetch Vertex for Target ID
                                        DefaultGraphCell vertex =
                                            (DefaultGraphCell) ids.get(target);
                                        if (vertex != null)
                                            // Connect to Target Port
                                            cs.connect(edge, vertex.getChildAt(0), false);
                                    }

                                    boolean edge_directed = ("true".equals(edge_node.getAttribute("isdirected")) || defaultDirected)
                                        && !("false".equals(edge_node.getAttribute("isdirected")));
                                    AttributeMap map = new AttributeMap();
                                    if (edge_directed) {
                                        GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
                                        GraphConstants.setEndFill(map, true);
                                    }
                                    fetchEdgeViewProperties(edge_node, map);
                                    attributes.put(edge, map);

                                    // Add Edge to new Cells
                                    newCells.add(edge);
                                } else if (type.equals("view")) { // Graph view attributes
                                    // Currently defined: defaultlayout
                                    defaultLayout = ((Element)node).getAttribute("defaultlayout");
                                }
                            }
                        }
                    }
                }
		// Insert the cells (View stores attributes)
		model.insert(newCells.toArray(), attributes, cs, null, null);
                if (defaultLayout != null) {
                    applyLayout(graph, defaultLayout);
                }
	}


    /**
     * Applies the layout given by name in <code>layout</code>.
     * Searches LayoutRegistry for the layout controller with string representation
     * equals to the <code>layout</code> and performs its algorithm on the imported
     * graph.
     */
    static void applyLayout(JGraph graph, String layout) {
        if (layout == null) {
            return;
        }
        Iterator controllers = JGraphLayoutRegistry.getSharedJGraphLayoutRegistry().getLayouts().iterator();
        while (controllers.hasNext()) {
        	JGraphLayoutAlgorithm controller = (JGraphLayoutAlgorithm)controllers.next();
            if (layout.equals(controller.toString())) {
				Object[] cells = DefaultGraphModel.getAll(graph.getModel());
				if (cells != null && cells.length > 0)
					JGraphLayoutAlgorithm.applyLayout(graph, controller, cells, null);
                return;
            }
        }
    }

	/**
	 * Returns an GPAttributeMap for the specified position and color.
	 */
	public static Map createDefaultAttributes() {
		// Create an GPAttributeMap
		AttributeMap map = new AttributeMap();
		// Set a Black Line Border (the Border-Attribute must be Null!)
		GraphConstants.setBorderColor(map, Color.black);
		// Return the Map
		return map;
	}


	// Fetch Cell Label from Node
	protected static String getLabel(Node node) {
		String lab = null;
		NodeList children = node.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node attr = children.item(j);
			if (attr.getNodeName().equals("attr")
				&& attr
					.getAttributes()
					.getNamedItem("name")
					.getNodeValue()
					.equals(
					"Label")) {
				NodeList values = attr.getChildNodes();
				for (int k = 0; k < values.getLength(); k++) {
					if (values.item(k).getNodeName().equals("string")) {
						Node labelNode = values.item(k).getFirstChild();
						if (labelNode != null)
							lab = labelNode.getNodeValue();
					}
				}
			}
		}
		return (lab != null) ? lab : new String("");
	}

}
