package org.jgraph.pad.util;

import java.io.Serializable;
import java.util.Map;

import org.jgraph.JGraph;

/**
 * The interface the GPGraphpad framework uses to handle the objects you put
 * inside graph cells. If you want GPGraphpad to handle your custom business
 * objects, simply make them implement that interface and register them in the
 * ICellBuisnessObject factory.
 * 
 * @author rvalyi, consider this as a LGPL contribution
 */
public interface ICellBuisnessObject extends Serializable, Cloneable {

	public abstract void setValue(Object label);

	public abstract Object getProperty(Object key);

	public abstract Object putProperty(Object key, Object value);

	/**
	 * @return da properties
	 */
	public abstract Map getProperties();

	/**
	 * @param map
	 */
	public abstract void setProperties(Map map);

	public abstract void showPropertyDialog(JGraph graph, final Object cell);

	public abstract Object clone();

	public abstract String toString();
}