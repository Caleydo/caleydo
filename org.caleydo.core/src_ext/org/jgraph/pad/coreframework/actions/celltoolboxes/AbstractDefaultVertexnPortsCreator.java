package org.jgraph.pad.coreframework.actions.celltoolboxes;

/*
 * Created on 8 juin 2005
 * This is GNU GPL free software
 * Copyright (C) 2004 Rapha�l Valyi, See LICENSE file in distribution for license details
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.IVertexFactory;

/**
 * @author Raphael Valyi
 * 
 * This is the super class of various java beans containing their own data for the
 * default cell creation of a given graph cell VERTEX type once a button is pressed. This class
 * contains generic methods to insert the cell and its ports in the graph model.
 * @see org.jgraph.pad.actions.celltoolboxes.ToolBoxEllipse
 */
public abstract class AbstractDefaultVertexnPortsCreator extends
		AbstractCellCreator implements IVertexFactory {

	private static String DEFAULT_PORT_NAME = "default_port";

	/**
	 * By default a cell don't have ports
	 * 
	 * @return double[]
	 */
	public double[] getXPortsList() {
		double[] empty = {};
		return empty;
	}

	/**
	 * By default a cell don't have ports
	 * 
	 * @return double[]
	 */
	public double[] getYPortsList() {
		double[] empty = {};
		return empty;
	}

	/**
	 * Unless the cell is created with differenciated port names, ports are all
	 * called undifferenciated_port followed by the i indice.
	 * 
	 * @param i
	 *            int
	 * @return String
	 */
	public String getPortName(int i) {
		String[] names = this.getPortNames();
		if (names == null || names.length < i + 1)
			return DEFAULT_PORT_NAME + "_n" + i;
		return names[i];
	}

	public String[] getPortNames() {
		return null;
	}

	/**
	 * By default there aren't any floatting port. If you want some, then you
	 * will get a better graphical result by implementing the getPerimeterPoint
	 * method in the renderer of the cell view.
	 * 
	 * @return boolean
	 */
	public boolean hasFlottingPort() {
		return true;
	}

	public AbstractDefaultVertexnPortsCreator(GPGraphpad graphpad) {
		this.graphpad = graphpad;
	}

	public AbstractDefaultVertexnPortsCreator() {
	}

	/**
	 * This is a convenient hook to automatically add a cell, its ports and a
	 * custom user object. Especially override in priority some of the other
	 * methods of this class in order to meet your needs.
	 * 
	 * @param bounds
	 */
	public GraphCell addVertexnPorts(Rectangle2D bounds) {
		List toInsert = new LinkedList();

		// creation of the cell:
		GraphCell cell = createCell();

		// creation of the cell model (user object) and/or properties map:
		/*ICellBuisnessObject user =  (ICellBuisnessObject) GPPluginInvoker.instanciateObjectForKey("UserObject.class");
		user.setProperties(getUserObjectProperties(cell));
		if (cell instanceof MutableTreeNode)
			((MutableTreeNode) cell).setUserObject(user);*/

		// view attributes creation:
		AttributeMap attributeMap = getAttributeMap(cell, bounds);

		// insertion of the cell:
		Map viewMap = new Hashtable();
		viewMap.put(cell, attributeMap);
		toInsert.add(cell);

		// Now we add the default ports to the cell:
		int permille = GraphConstants.PERMILLE;
		Object port;
		int i;
		for (i = 0; i < this.getXPortsList().length; i++) {
			attributeMap = new AttributeMap();
			GraphConstants.setOffset(attributeMap, new Point((int) (this
					.getXPortsList()[i] * permille),
					(int) (this.getYPortsList()[i] * permille)));
			port = new DefaultPort(this.getPortName(i));
			((DefaultMutableTreeNode) cell).add((DefaultPort) port);
			viewMap.put(port, attributeMap);
			toInsert.add(port);
		}

		if (hasFlottingPort()) {
			port = new DefaultPort(this.getPortName(i));
			((DefaultMutableTreeNode) cell).add((DefaultPort) port);
			toInsert.add(port);
		}

		graphpad.getCurrentGraph().getModel().insert(toInsert.toArray(),
				viewMap, null, null, null);

		// finally a specific action can be started:
		actionForCell(cell);
		return cell;
	}

	/**
	 * By default we don't resize the cell but this may be be overriden in order
	 * to implement an automatic resizing...
	 * 
	 * @param bounds
	 * @return the resized rectangle
	 */
	protected Rectangle2D reSize(Rectangle2D bounds) {
		// example: to uncomment the following code here
		// or in the an overriding method
		// to force cells to be squared
		// double m = (bounds.getWidth() + bounds.getHeight()) / 2;
		// bounds.setRect(bounds.getX(), bounds.getY(), m, m);
		return bounds;
	}

	/**
	 * You can override how the view attributes of the cell are created
	 * 
	 * @param cell
	 * @param bounds
	 * @return a new attribute map with the defined bounds
	 */
	public AttributeMap getAttributeMap(GraphCell cell,
			Rectangle2D bounds) {
		AttributeMap attributeMap = new AttributeMap();
		double scale = graphpad.getCurrentGraph().getScale();
		bounds.setRect(bounds.getX()/scale, bounds.getY()/scale, bounds.getWidth()/scale, bounds.getHeight()/scale);
		bounds = reSize(bounds); // thus we can constraint the bounding box
									// size
		GraphConstants.setBounds(attributeMap, bounds);
		GraphConstants.setOpaque(attributeMap, true);
		GraphConstants.setInset(attributeMap, 40);
		GraphConstants.setBorderColor(attributeMap, Color.black);
		String fontName = Translator.getString("FontName");
		try {
			int fontSize = Integer.parseInt(Translator.getString("FontSize"));
			int fontStyle = Integer.parseInt(Translator.getString("FontStyle"));
			GraphConstants.setFont(attributeMap, new Font(fontName, fontStyle,
					fontSize));
		} catch (Exception e) {
		}
		return attributeMap;
	}

	public static String getDEFAULT_PORT_NAME() {
		return DEFAULT_PORT_NAME;
	}

	public static void setDEFAULT_PORT_NAME(String default_port_name) {
		DEFAULT_PORT_NAME = default_port_name;
	}
}
