/*
 * @(#)JGraphGXLCodec.java 1.0 12-MAY-2004
 *
 * Copyright (c) 2001-2004, Gaudenz Alder, Paul Raingeard de la Bl�ti�re
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
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
 */
package org.jgraph.plugins.codecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.pad.util.JGraphUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JGraphGXLCodec {

    static transient Hashtable encodeHash;
    static transient Hashtable decodeHash;

    /**
     * Property for XML parse.
     * True : the parse wil validate the XML withing the DTD.
     * False : It only will be done if it is available.
     */
    private static boolean validateDTD = false;

    /**
     * Retrieves the encoding Hashtable with the node's Id.
     *
     * It may be usefull to sirialize the values of the nodes.
     * @return Hastable with elements : ((key : node), (value : GXL id)).
     */
    public static Hashtable getLastEncodingHashtable() {
       return encodeHash;
    }

    /**
     * Retrieves the decoding Hashtable with the node's Id.
     *
     * It may be usefull to sirialize the values of the nodes.
     * @return Hastable with elements : ((key : node), (value : GXL id)).
     */
    public static Hashtable getLastDecodingHashtable() {
       return decodeHash;
    }

    /**
     * Create a GXL-representation for all the cells.
     *
     * @param graph JGraph to encode.
     * @return Encoded string.
     */
    public static String encode(JGraph graph) {
        Object[] cells = graph.getDescendants(graph.getRoots());
        return encode(graph, cells);
    }

    /**
     * Create a GXL-representation for the specified cells.
     *
     * @param graph JGraph to encode.
     * @param cells Selected cells to be encoded.
     * @return Encoded string.
     */
    public static String encode(JGraph graph, Object[] cells) {
        int counter = 0;
        encodeHash = new Hashtable();
        String gxl = "<?xml version=\"1.0\"?>\n" +
              "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.1.dtd\">\n" +
              "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
              "<graph id=\"jGraph\">\n";

        // Create external keys for nodes
        for (int i = 0; i < cells.length; i++)
            if (JGraphUtilities.isVertex(graph, cells[i])) {
                encodeHash.put(cells[i], "node" + counter);
                counter++;
            }

        // Convert Nodes
        Iterator it = encodeHash.keySet().iterator();
        while (it.hasNext()) {
            Object node = it.next();
            gxl += encodeVertex(graph, (String)encodeHash.get(node), node);
        }

        // Convert Edges
        int edges = 0;
        for (int i = 0; i < cells.length; i++)
            if (graph.getModel().isEdge(cells[i]))
                gxl += encodeEdge(graph, new Integer(edges++), cells[i]);

        // Close main tags
        gxl += "\n</graph>\n</gxl>";
        return gxl;
    }

    /**
     * Create a string with tabs.
     *
     * @param level Tab level.
     * @return Tab string.
     */
    private static String createTab(int level){
        String tab = "";
        for (int i=0; i<level; i++){
            tab += "\t";
        }
        return tab;
    }

    /**
     * Basic value encoding.
     *
     * @param type GXL Type of the value (int, bool, ...)
     * @param value Value to be encoded.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeValue(String type, String value, int level){
        return createTab(level) + "<" + type + ">" + value + "</" + type + ">\n";
    }

    /**
     * Basic boolean encoding.
     *
     * @param value Value to be encoded.
     * @param level Tab level.
     * @return Encoded string.
     */
    private static String encodeValue(boolean value, int level){
        return createTab(level) + "<bool>" + value + "</bool>\n";
    }

    /**
     * Basic integer encoding.
     *
     * @param value Value to be encoded.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeValue(int value, int level){
        return createTab(level) + "<int>" + value + "</int>\n";
    }

    /**
     * Basic String encoding.
     *
     * @param value Value to be encoded.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeValue(String value, int level){
        return createTab(level) + "<string>" + value + "</string>\n";
    }


    /**
     * Attribute encoding.
     *
     * @param values Values of the attribute.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeAttribute(String values, String attributeName, int level){
        String tab = createTab(level);
        return tab + "<attr name=\"" + attributeName + "\">\n" + values + tab + "</attr>\n";
    }

    /**
     * String encoding.
     *
     * @param value Value of the attribute.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeString(String value, String attributeName, int level){
        if (value != null){
            return encodeAttribute(encodeValue(value,level+1), attributeName, level);
        } else {
            return "";
        }
    }

    /**
     * Integer encoding.
     *
     * @param value Value of the attribute.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeInteger(int value, String attributeName, int level){
        return encodeAttribute(encodeValue(value,level+1), attributeName, level);
    }


    /**
     * Boolean encoding.
     *
     * @param value Value of the attribute.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeBoolean(boolean value, String attributeName, int level){
        return encodeAttribute(encodeValue(value,level+1), attributeName, level);
    }

    /**
     * Color encoding.
     *
     * @param color Color of the attribute.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeColor(Color color, String attributeName, int level){
        if (color != null) {
            String tab1 = createTab(level+1);
            int level2 = level+2;
            String values = tab1 + "<tup>\n" +
            encodeValue(color.getRed(), level2) +
            encodeValue(color.getGreen(), level2) +
            encodeValue(color.getBlue(), level2) +
            tab1 + "</tup>\n";
            return encodeAttribute(values, attributeName, level);
        } else {
            return "";
        }
    }



    /**
     * Font encoding.
     *
     * @param font Font of the attribute.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeFont(Font font, String attributeName, int level){
        if (font != null) {
            String tab1 = createTab(level+1);
            int level2 = level+2;
            String values = tab1 + "<tup>\n" +
            encodeValue(font.getFontName(), level2) +
            encodeValue(font.getStyle(), level2) +
            encodeValue(font.getSize(), level2) +
            tab1 + "</tup>\n";
            return encodeAttribute(values, attributeName, level);
        } else {
            return "";
        }
    }



    /**
     * Rectangle encoding.
     *
     * @param rec Rectangle to be encoded.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeRectangle(Rectangle2D rec, String attributeName, int level){
        if (rec != null) {
            String tab1 = createTab(level+1);
            int level2 = level+2;
            String values = tab1 + "<tup>\n" +
            encodeValue((int)rec.getCenterX(), level2) +
            encodeValue((int)rec.getCenterY(), level2) +
            encodeValue((int)rec.getWidth(), level2) +
            encodeValue((int)rec.getHeight(), level2) +
            tab1 + "</tup>\n";
            return encodeAttribute(values, attributeName, level);
        } else {
            return "";
        }
    }

    /**
     * Bean encoding.
     * This is usefull to encode the userObject in the Vertex.
     * It must be a bean with a beanInfo class in order to inspect it.
     *
     * @param bean Bean to be encoded.
     * @param attributeName name of the attribute.
     * @param level Tab level.
     * @return Encoded string.
     */
    protected static String encodeBean(Object bean, String attributeName, int level){
        String encoded = "";
        if (bean != null) {
            try {
                int level1 = level+1;
                BeanInfo bi = null;
                PropertyDescriptor tmpProperties[] = null;
                PropertyDescriptor prop = null;

                bi = Introspector.getBeanInfo(bean.getClass());
                tmpProperties = bi.getPropertyDescriptors();
                encoded += encodeString(bean.getClass().getName(), "ClassName", level1);
                for (int i=0; i<tmpProperties.length; i++) {
                    prop = tmpProperties[i];
                    encoded += encodeString(prop.getReadMethod().invoke(bean,null).toString(), prop.getDisplayName(), level1);
                }
                encoded = encodeAttribute(encoded, attributeName, level);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return encoded;
    }


    /**
     * Encode a Vertex of a graph
     *
     * @param graph Graph containing the vertex.
     * @param id Id of the vertex.
     * @param vertex Vertex to be encoded.
     * @return Encoded string.
     */
    protected static String encodeVertex(JGraph graph, String id, Object vertex) {
        int level = 2;
        String label = graph.convertValueToString(vertex);
        Map attributes = ((GraphCell)vertex).getAttributes();
        String encoded = "\n\t<node id=\"" + id + "\">\n"
            + encodeString(label, "Label", level)
            + encodeRectangle(GraphConstants.getBounds(attributes), "Bounds", level)
            + encodeColor(GraphConstants.getBorderColor(attributes), "BorderColor", level)
            + encodeColor(GraphConstants.getForeground(attributes), "BorderColor", level)
            + encodeColor(GraphConstants.getBackground(attributes), "BorderColor", level)
            + encodeFont(GraphConstants.getFont(attributes), "Font", level)
            + encodeColor(GraphConstants.getLineColor(attributes), "BorderColor", level)
            //+ encodeBoolean(GraphConstants.getOpaque(attributes), "Opaque", level)
            //+ encodeBean(GraphConstants.getValue(attributes), "Value", level)
            + "\t</node>";
        return encoded;
    }



    /**
     * Encode a Edge of a graph
     *
     * @param graph Graph containing the edge.
     * @param id Id of the vertex.
     * @return Encoded string.
     */
    protected static String encodeEdge(JGraph graph, Object id, Object edge) {
        GraphModel model = graph.getModel();
        String from = "";
        if (model.getSource(edge) != null) {
            Object source = encodeHash.get(model.getParent(model.getSource(edge)));
            if (source != null)
                from = source.toString();
        }
        String to = "";
        if (model.getTarget(edge) != null) {
            Object target = encodeHash.get(model.getParent(model.getTarget(edge)));
            if (target != null)
                to = target.toString();
        }
        if (from != null && to != null) {
            int level = 2;
            Map attributes = ((GraphCell)edge).getAttributes();
            String label = graph.convertValueToString(edge);
            return "\n\t<edge id=\"edge"
            + id.toString()
            + "\""
            + " from=\""
            + from
            + "\""
            + " to=\""
            + to
            + "\">\n"
            + encodeString(label, "Label", 2)
            + encodeInteger(GraphConstants.getLineEnd(attributes), "LineEnd", level)
            + encodeColor(GraphConstants.getForeground(attributes), "Foreground", level)
            + encodeFont(GraphConstants.getFont(attributes), "Font", level)
            + encodeInteger(GraphConstants.getLineStyle(attributes), "LineStyle", level)
            + encodeColor(GraphConstants.getLineColor(attributes), "LineColor", level)
            + "\n\t</edge>";
        } else
            return "";
    }


    /**
     * Extracts visual properties of the node from the child 'view' element
     * Currently recognized properties:
     * - Bounds
     * - color
     * - background-color
     * - autosize
     * - Font
     * - Line-End, Line-size, Line-color
     */
    protected static void decodeCell(Node gnode, Map gnode_attrs) {
        NodeList gnode_children = gnode.getChildNodes();
        for (int gnode_child_i = 0; gnode_child_i < gnode_children.getLength(); gnode_child_i++) {
            Node gnode_child = gnode_children.item(gnode_child_i);
            if (gnode_child.getNodeName().equals("attr")) {
                String name = ((Element)gnode_child).getAttribute("name");
                LinkedList values = new LinkedList();
                // Retreaving all the values in the node
                readGXLAttributeValues(gnode_child, values);
                if ((name != null) && (values.size()>0)) {
                    if (name.equals("Bounds")) {
                        if (values.size() == 4){
                            Point p = new Point(Integer.parseInt((String)values.get(0)),
                            Integer.parseInt((String)values.get(1)));
                            Dimension d = new Dimension(Integer.parseInt((String)values.get(2)),
                            Integer.parseInt((String)values.get(3)));
                            Rectangle2D bounds = new Rectangle(p, d);
                            GraphConstants.setBounds(gnode_attrs, bounds);
                        }
                    } else if (name.equals("Font")) {
                        if (values.size() == 3){
                            Font font = new Font((String)values.get(0),
                                        Integer.parseInt((String)values.get(1)),
                                        Integer.parseInt((String)values.get(2)));
                            GraphConstants.setFont(gnode_attrs, font);
                        }
                    } else if (name.equals("Foreground")) {
                        try {
                            Color color = new Color(Integer.parseInt((String)values.get(0)),
                            Integer.parseInt((String)values.get(1)),
                            Integer.parseInt((String)values.get(2)));
                            GraphConstants.setForeground(gnode_attrs, color);
                        } catch (Exception nfe) {
                        }
                    } else if (name.equals("BorderColor")) {
                        try {
                            Color color = new Color(Integer.parseInt((String)values.get(0)),
                            Integer.parseInt((String)values.get(1)),
                            Integer.parseInt((String)values.get(2)));
                            GraphConstants.setBorderColor(gnode_attrs, color);
                        } catch (Exception nfe) {
                        }
                    } else if (name.equals("LineColor")) {
                        try {
                            Color color = new Color(Integer.parseInt((String)values.get(0)),
                            Integer.parseInt((String)values.get(1)),
                            Integer.parseInt((String)values.get(2)));
                            GraphConstants.setLineColor(gnode_attrs, color);
                        } catch (Exception nfe) {
                        }
                    } else if (name.equals("Background")) {
                        try {
                            Color color = new Color(Integer.parseInt((String)values.get(0)),
                            Integer.parseInt((String)values.get(1)),
                            Integer.parseInt((String)values.get(2)));
                            GraphConstants.setBackground(gnode_attrs, color);
                        } catch (Exception nfe) {
                        }
                    } else if (name.equals("LineColor")) {
                        try {
                            Color color = new Color(Integer.parseInt((String)values.get(0)),
                            Integer.parseInt((String)values.get(1)),
                            Integer.parseInt((String)values.get(2)));
                            GraphConstants.setLineColor(gnode_attrs, color);
                        } catch (Exception nfe) {
                        }
                    } else if (name.equals("LineEnd")) {
                        try {
                            GraphConstants.setLineEnd(gnode_attrs, Integer.parseInt((String)values.get(0)));
                        } catch (Exception e){}
                    } else if (name.equals("LineStyle")) {
                        try {
                            GraphConstants.setLineStyle(gnode_attrs, Integer.parseInt((String)values.get(0)));
                        } catch (Exception e){}
                    } else if (name.equals("AutoSize")) {
                        GraphConstants.setAutoSize(gnode_attrs, "true".equals(values.get(0)));
                    }
                }
            }
        }
    }

    /**
     * Reads the values of an GXL Attribute.
     *
     * @param enode Node to read.
     * @param values List to populate : with 2 dimension String arrys.
     *                              return[0] : type of value
     *                              return[1] : value
     */
    protected static void readGXLAttributeValues(Node enode, LinkedList values){
        // read Child Elements
        NodeList child_list = enode.getChildNodes();
        for(int j=0; j<child_list.getLength(); j++){
            Node currentNode = child_list.item(j);
            String nodeName = currentNode.getNodeName();
            if (nodeName.equals("tup") ||
            nodeName.equals("set") ||
            nodeName.equals("enum") ||
            nodeName.equals("seq") ||
            nodeName.equals("bag")) {
                readGXLAttributeValues(currentNode, values);
            } else if (nodeName.equals("int") ||
            nodeName.equals("bool") ||
            nodeName.equals("float") ||
            nodeName.equals("string") ||
            nodeName.equals("locator")) {
                try {
                	Node firstChild = currentNode.getFirstChild();
                	// Check to see if first child exists. Null strings
                	// will just show no value
                	if (null == firstChild) {
                		if (nodeName.equals("string")) values.add(new String(""));
                	}
                	else {
                		values.add(currentNode.getFirstChild().getNodeValue());
                	}
                } catch (DOMException e){}
            }
        }
    }


    /**
     * Decodes a Edge.
     *
     * @param enode XML Node.
     * @param enode_attrs Cell Attributes.
     */
    protected static void decodeEdge(Node enode, Map enode_attrs) {
        decodeCell(enode, enode_attrs);
    }


    /**
     * Manage DTD offline.
     *
     * @param db XML document builder.
     */
    private static void manageDTDLookup(DocumentBuilder db) {
        // Error During DTD validation : can't find www.gupro.de
        // Forget validation
        db.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId)
            throws SAXException, java.io.IOException {
                InputSource is = null;
                try {
                    URL url = new URL(systemId);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.connect();
                    int responseCode = con.getResponseCode();
                    con.disconnect();
                    if (responseCode != HttpURLConnection.HTTP_NOT_FOUND) {
                        is = new InputSource(systemId);
                    } else {
                        is = new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
                    }
                } catch (IOException e) {
                    is = new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
                }
                return is;
            }
        });
    }

    /**
     * Decodes a GXL File.
     *
     * @param inputStream Stream to be decoded.
     * @param graph Graph where the decode file is inserted.
     */
    public static void decode(InputStream inputStream,
                              JGraph graph) throws Exception {
        GraphModel model = graph.getModel();
        // Create a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // Create a DocumentBuilder
        DocumentBuilder db = dbf.newDocumentBuilder();
        // Manage the compliange with the DTD File.
        if (validateDTD == false) {
            manageDTDLookup(db);
        }
        // Parse the input file to get a Document object
        Document doc = db.parse(inputStream);
        // Get the first child (the graph-element)
        // List for the new Cells
        List newCells = new ArrayList();
        // ConnectionSet for the Insert method
        ConnectionSet cs = new ConnectionSet();
        // Hashtable for the ID lookup (ID to Vertex)
        decodeHash = new Hashtable();
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
                            if (id != null && !decodeHash.keySet().contains(id)) {
                                // Create Vertex with label
                                DefaultGraphCell vertex = new DefaultGraphCell(label);
                                // Add One Floating Port
                                vertex.add(new DefaultPort());
                                // Add ID, Vertex pair to Hashtable
                                decodeHash.put(id, vertex);
                                // Add Default Attributes
                                Map node_attrs = new Hashtable(); //createDefaultAttributes(new Hashtable());
                                decodeCell(node, node_attrs);
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
                            DefaultEdge edge = new DefaultEdge(label);
                            // Find Source Port
                            if (source != null) {
                                // Fetch Vertex for Source ID
                                DefaultGraphCell vertex =
                                (DefaultGraphCell) decodeHash.get(source);
                                if (vertex != null)
                                    // Connect to Source Port
                                    cs.connect(edge, vertex.getChildAt(0), true);
                            }
                            // Find Target Port
                            if (target != null) {
                                // Fetch Vertex for Target ID
                                DefaultGraphCell vertex =
                                (DefaultGraphCell) decodeHash.get(target);
                                if (vertex != null)
                                    // Connect to Target Port
                                    cs.connect(edge, vertex.getChildAt(0), false);
                            }

                            boolean edge_directed = ("true".equals(edge_node.getAttribute("isdirected")) || defaultDirected)
                            && !("false".equals(edge_node.getAttribute("isdirected")));
                            Map map = new Hashtable();
                            if (edge_directed) {
                                GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
                                GraphConstants.setEndFill(map, true);
                            }
                            decodeEdge(edge_node, map);
                            attributes.put(edge, map);

                            // Add Edge to new Cells
                            newCells.add(edge);
                        }
                    }
                }
            }
        }
        // Insert the cells (View stores attributes)
        model.insert(newCells.toArray(), attributes, cs, null, null);
    }

    /**
     * Returns an attributeMap for the specified position and color.
     */
    protected static Map createDefaultAttributes(Map map) {
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
            && attr.getAttributes().getNamedItem("name").getNodeValue().equals(
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

    /**
     * Setter for the property validateDTD
     *
     * @param validate True, the validation will occur.
     */
    public static void setValidateDTD(boolean validate){
        validateDTD = validate;
    }

    /**
     * Getter for the property validateDTD
     */
    public static boolean getValidateDTD(){
        return validateDTD;
    }
}
