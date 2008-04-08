/**
 * 
 */
package org.caleydo.util.graph.item;

import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract class implementing IGraphDataHandler interface.
 * 
 * @see org.caleydo.util.graph.item.GraphItemDataSequenzer
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGraphDataHandler implements IGraphDataHandler {

	private static final int iInitialSize = 3;
	
	protected final HashMap <Integer,Object> key_2_data;
		
	
	/**
	 * 
	 */
	protected AGraphDataHandler() {
		this(iInitialSize);
	}
	
	/**
	 * 
	 * @param initialSize define initial size and number of expected objects.
	 */
	protected AGraphDataHandler( int initialSize ) {
		key_2_data = new HashMap <Integer, Object> (initialSize);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#containsData(int)
	 */
	public final boolean containsData(int key) {
		return key_2_data.containsKey(new Integer(key));
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#containsDataObject(java.lang.Object)
	 */
	public final boolean containsDataObject(Object data) {
		return key_2_data.containsValue(data);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#getAllData()
	 */
	public final Collection <Object> getAllData() {
		return key_2_data.values();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#getData(int)
	 */
	public final Object getData(int key) {
		return key_2_data.get(new Integer(key));
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#removeAllData()
	 */
	public final void removeAllData() {
		key_2_data.clear();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#removeData(int)
	 */
	public final Object removeData(int key) {
		return key_2_data.remove(new Integer(key));
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.item.IGraphDataHandler#setData(int, java.lang.Object)
	 */
	public final boolean setData(int key, Object data) {
		return (key_2_data.put(new Integer(key), data) == null ) ? false : true;
	}
	
}
